package com.example.m_commerce_admin.features.login.domain.usecase

import com.example.m_commerce_admin.features.login.domain.entity.AdminUser
import javax.inject.Inject

class LoginUseCase @Inject constructor() {
    operator fun invoke(admin:AdminUser): Boolean {
        return admin.username == "admin" && admin.password == "1234"
    }
}
