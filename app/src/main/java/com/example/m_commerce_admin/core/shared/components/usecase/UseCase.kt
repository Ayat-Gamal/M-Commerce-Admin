package com.example.m_commerce_admin.core.shared.components.usecase

interface UseCase <in Params, out Result>{
    suspend operator fun invoke(params: Params) : Result
}