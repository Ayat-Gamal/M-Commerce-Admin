package com.example.m_commerce_admin.features.products.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.products.domain.repository.ProductRepository
import com.example.m_commerce_admin.features.products.presentation.ProductState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(
    private val repo: ProductRepository
) : UseCase<GetProductsParams, Flow<ProductState>> {

    override fun invoke(params: GetProductsParams): Flow<ProductState> {
        return repo.getProducts(params.first, params.after)
    }
}
