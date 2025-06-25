package com.example.m_commerce_admin.features.inventory.domain.usecase

import com.example.m_commerce_admin.features.inventory.domain.entity.InventoryLevel
import com.example.m_commerce_admin.features.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoryLevelsUseCase @Inject constructor(
    private val repo: InventoryRepository
) {
    suspend operator fun invoke(): Flow<Result<List<InventoryLevel>>>  =
        repo.getInventoryLevels()
}