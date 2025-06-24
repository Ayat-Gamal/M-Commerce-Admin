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
        // 1. Prepare upload inputs
        val inputs = remoteDataSource.prepareStagedUploadInputs(context, imageUris)

        // 2. Get upload targets from Shopify
        val targets = remoteDataSource.requestStagedUploads(inputs)
        targets.forEach { Log.d("DebugTargets", it.toString()) }

        // 3. Upload each image to its target
        imageUris.zip(targets).forEach { (uri, target) ->
            val success = uploadFileToTarget(context, uri, target)
            if (!success) throw Exception("Failed to upload image ${uri.lastPathSegment}")
        }

        // 4. Create media inputs for product
        val mediaInputs = targets.map { target ->
            targets.forEach { Log.d("DebugTargets", it.toString()) }

            CreateMediaInput(
                originalSource = target.resourceUrl,
                alt = Optional.Absent,
                mediaContentType = MediaContentType.IMAGE
            )
        }

        // 5. Convert domain product to GraphQL input
        val productInput = product.toGraphQL()

        // 6. Create product with media references
        remoteDataSource.addProductWithMedia(productInput, mediaInputs)
            .getOrThrow()
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(it) }
    )

    private suspend fun uploadFileToTarget(
        context: Context,
        uri: Uri,
        target: StagedUploadTarget
    ): Boolean {
        // This should be implemented in your remote data source
        // but we're keeping it here temporarily for backward compatibility
        return (remoteDataSource as? ProductRemoteDataSourceImpl)?.uploadImageToStagedTarget(context, uri, target)
            ?: throw IllegalStateException("Missing file upload implementation")
    }
}