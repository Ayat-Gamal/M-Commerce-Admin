package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.core.shared.components.usecase.UseCase
import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoryLevelsUseCase @Inject constructor(
    private val repo: InventoryRepository
) : UseCase<Unit, Flow<Result<List<InventoryLevel>>>> {
    override suspend operator fun invoke(params: Unit): Flow<Result<List<InventoryLevel>>> =
        repo.getInventoryLevels()
}