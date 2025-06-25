package com.example.m_commerce_admin.features.products.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.products.data.mapper.toGraphQL
import com.example.m_commerce_admin.features.products.domain.entity.StagedUploadTarget
import com.example.m_commerce_admin.features.products.data.remote.ProductRemoteDataSource
import com.example.m_commerce_admin.features.products.data.retrofitRemote.ProductDto
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.MediaContentType
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return remoteDataSource.deleteProduct(productId)
    }

    override suspend fun uploadImagesAndAddProduct(
        product: DomainProductInput,
        imageUris: List<Uri>,
        context: Context
    ): Result<Unit> = runCatching {
        // 1. Prepare upload inputs
        val inputs = remoteDataSource.prepareStagedUploadInputs(context, imageUris)

        // 2. Get upload targets from Shopify
        val targets = remoteDataSource.requestStagedUploads(inputs)

        // 3. Upload each image to its target
        imageUris.zip(targets).forEachIndexed { index, (uri, target) ->
            val success = uploadFileToTarget(context, uri, target)
            if (!success) {
                val errorMsg = "Failed to upload image ${uri.lastPathSegment} (${index + 1}/${imageUris.size})"
                throw Exception(errorMsg)
            }
        }

        // 4. Create media inputs for product
        val mediaInputs = targets.map { target ->
            CreateMediaInput(
                originalSource = target.resourceUrl,
                alt = Optional.Absent,
                mediaContentType = MediaContentType.IMAGE
            )
        }

        val productInput = product.toGraphQL()

           val result = remoteDataSource.addProductWithMedia(productInput, mediaInputs)
         if (result.isFailure) {
            Log.e("ProductRepo", "❌ Failed to create product", result.exceptionOrNull())
            throw result.exceptionOrNull() ?: Exception("Failed to create product")
        }

    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { 
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