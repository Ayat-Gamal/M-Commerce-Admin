package com.example.m_commerce_admin.features.products.data.remote

import android.content.Context
import android.net.Uri
import com.example.m_commerce_admin.features.products.data.model.StagedUploadInput
import com.example.m_commerce_admin.features.products.data.model.StagedUploadTarget
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ProductRemoteDataSource {
    fun getProducts(first: Int, after: String?): Flow<GetProductState>
    suspend fun addProduct(product: ProductInput): Result<Unit>


    suspend fun prepareStagedUploadInputs(context: Context, imageUris: List<Uri>): List<StagedUploadInput>
    suspend fun requestStagedUploads(inputs: List<StagedUploadInput>): List<StagedUploadTarget>
    suspend fun uploadImageToStagedTarget(context: Context, uri: Uri, target: StagedUploadTarget): Boolean
    suspend fun addProductWithMedia(product: ProductInput, media: List<CreateMediaInput>): Result<Unit>


}
