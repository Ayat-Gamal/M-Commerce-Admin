package com.example.m_commerce_admin.features.products.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce_admin.AddProductMutation
import com.example.m_commerce_admin.AddProductWithImagesMutation
import com.example.m_commerce_admin.GetProductsQuery
import com.example.m_commerce_admin.TestProductsQuery
import com.example.m_commerce_admin.features.products.data.mapper.toDomain
import com.example.m_commerce_admin.features.products.domain.entity.DomainProductInput
import com.example.m_commerce_admin.features.products.presentation.states.GetProductState
import com.example.m_commerce_admin.type.CreateMediaInput
import com.example.m_commerce_admin.type.ProductInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val context: Context
) : ProductRemoteDataSource {

    override fun getProducts(first: Int, after: String?): Flow<GetProductState> = flow {
        try {
            Log.d("ProductRemoteDataSource", "Starting getProducts: first=$first, after=$after")
            emit(GetProductState.Loading)

            Log.d("ProductRemoteDataSource", "Executing GraphQL query...")
            val response = apolloClient.query(
                GetProductsQuery(first = first, after = Optional.presentIfNotNull(after))
            ).execute()

            Log.d("ProductRemoteDataSource", "Query executed. Has errors: ${response.hasErrors()}")
            Log.d("ProductRemoteDataSource", "Response data: ${response.data != null}")
            Log.d("ProductRemoteDataSource", "Response errors: ${response.errors}")

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "GraphQL error occurred"
                Log.e("ProductRemoteDataSource", "GraphQL errors: ${response.errors}")
                emit(GetProductState.Error(errorMessage))
                return@flow
            }

            if (response.data == null) {
                Log.e("ProductRemoteDataSource", "Response data is null")
                emit(GetProductState.Error("No data received from server"))
                return@flow
            }

            val products = response.data?.products?.edges?.map { edge ->
                edge.node.toDomain()
            } ?: emptyList()

            val hasNextPage = response.data?.products?.pageInfo?.hasNextPage ?: false
            val endCursor = response.data?.products?.pageInfo?.endCursor

            Log.d("ProductRemoteDataSource", "Products loaded: ${products.size}, hasNext: $hasNextPage, cursor: $endCursor")
            emit(GetProductState.Success(products, hasNextPage, endCursor))
        } catch (e: ApolloException) {
            Log.e("ProductRemoteDataSource", "ApolloException in getProducts", e)
            emit(GetProductState.Error("GraphQL error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("ProductRemoteDataSource", "Exception in getProducts", e)
            emit(GetProductState.Error(e.message ?: "Network error occurred"))
        }
    }

    override suspend fun addProduct(product: ProductInput): Result<Unit> {
        return try {
            val gqlInput = ProductInput(
                title = product.title,
                descriptionHtml = product.descriptionHtml,
                productType = product.productType,
                vendor = product.vendor,
                status = product.status
            )

            val response = apolloClient.mutation(AddProductMutation(gqlInput)).execute()

            val errors = response.data?.productCreate?.userErrors
            if (!errors.isNullOrEmpty()) {
                Result.failure(Exception(errors.first().message))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProductWithImages(product: DomainProductInput, imageUris: List<String>): Result<Unit> {
        return try {
            Log.d("ProductRemoteDataSource", "Starting addProductWithImages with ${imageUris.size} images")
            
            // Convert URIs to base64 encoded strings
            val mediaInputs = mutableListOf<CreateMediaInput>()
            
            for (uriString in imageUris) {
                try {
                    val uri = Uri.parse(uriString)
                    val base64Data = convertImageToBase64(uri)
                    val mimeType = getMimeType(uri)
                    
                    val mediaInput = CreateMediaInput(
                        mediaContentType = com.example.m_commerce_admin.type.MediaContentType.IMAGE,
                        originalSource = base64Data
                    )
                    mediaInputs.add(mediaInput)
                    
                    Log.d("ProductRemoteDataSource", "Converted image to base64: ${base64Data.take(50)}...")
                } catch (e: Exception) {
                    Log.e("ProductRemoteDataSource", "Error converting image $uriString", e)
                    return Result.failure(Exception("Failed to process image: ${e.message}"))
                }
            }

            val gqlInput = ProductInput(
                title = Optional.presentIfNotNull(product.title),
                descriptionHtml = Optional.presentIfNotNull(product.descriptionHtml),
                productType = Optional.presentIfNotNull(product.productType),
                vendor = Optional.presentIfNotNull(product.vendor),
                status = Optional.presentIfNotNull(product.status.toGraphQLStatus())
            )

            Log.d("ProductRemoteDataSource", "Executing AddProductWithImages mutation")
            val response = apolloClient.mutation(
                AddProductWithImagesMutation(
                    input = gqlInput,
                    media = Optional.presentIfNotNull(mediaInputs)
                )
            ).execute()

            Log.d("ProductRemoteDataSource", "Mutation executed. Has errors: ${response.hasErrors()}")
            
            val errors = response.data?.productCreate?.userErrors
            if (!errors.isNullOrEmpty()) {
                val errorMessage = errors.first().message
                Log.e("ProductRemoteDataSource", "GraphQL errors: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                Log.d("ProductRemoteDataSource", "Product created successfully with images")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("ProductRemoteDataSource", "Exception in addProductWithImages", e)
            Result.failure(e)
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Could not open image file")
        
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        
        val imageBytes = byteArrayOutputStream.toByteArray()
        inputStream.close()
        byteArrayOutputStream.close()
        
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    private fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "image/jpeg"
    }
}




