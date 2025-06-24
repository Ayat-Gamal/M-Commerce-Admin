package com.example.m_commerce_admin.features.products.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.data.model.StagedUploadTarget
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSourceImpl
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.MediaContentType
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> {
        return remoteDataSource.getProducts(first, after)
    }

    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return remoteDataSource.addProduct(product)
    }


    override suspend fun uploadImagesAndAddProduct(
        product: DomainProductInput,
        imageUris: List<Uri>,
        context: Context
    ): Result<Unit> = runCatching {
        Log.d("ProductRepo", "🚀 Starting image upload process for ${imageUris.size} images")
        
        // 1. Prepare upload inputs
        val inputs = remoteDataSource.prepareStagedUploadInputs(context, imageUris)
        Log.d("ProductRepo", "📋 Prepared ${inputs.size} upload inputs")

        // 2. Get upload targets from Shopify
        val targets = remoteDataSource.requestStagedUploads(inputs)
        Log.d("ProductRepo", "🎯 Received ${targets.size} upload targets from Shopify")

        // 3. Upload each image to its target
        imageUris.zip(targets).forEachIndexed { index, (uri, target) ->
            Log.d("ProductRepo", "📤 Uploading image ${index + 1}/${imageUris.size}: ${uri.lastPathSegment}")
            val success = uploadFileToTarget(context, uri, target)
            if (!success) {
                val errorMsg = "Failed to upload image ${uri.lastPathSegment} (${index + 1}/${imageUris.size})"
                Log.e("ProductRepo", "❌ $errorMsg")
                throw Exception(errorMsg)
            }
            Log.d("ProductRepo", "✅ Successfully uploaded image ${index + 1}/${imageUris.size}")
        }

        // 4. Create media inputs for product
        val mediaInputs = targets.map { target ->
            CreateMediaInput(
                originalSource = target.resourceUrl,
                alt = Optional.Absent,
                mediaContentType = MediaContentType.IMAGE
            )
        }
        Log.d("ProductRepo", "🖼️ Created ${mediaInputs.size} media inputs")

        // 5. Convert domain product to GraphQL input
        val productInput = product.toGraphQL()

        // 6. Create product with media references
        Log.d("ProductRepo", "🏪 Creating product with ${mediaInputs.size} images")
        remoteDataSource.addProductWithMedia(productInput, mediaInputs)
            .getOrThrow()
            
        Log.d("ProductRepo", "🎉 Product created successfully with images!")
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { 
            Log.e("ProductRepo", "💥 Failed to upload images and create product", it)
            Result.failure(it) 
        }
    )

    private suspend fun uploadFileToTarget(
        context: Context,
        uri: Uri,
        target: StagedUploadTarget
    ): Boolean {
        return remoteDataSource.uploadImageToStagedTarget(context, uri, target)
    }
}