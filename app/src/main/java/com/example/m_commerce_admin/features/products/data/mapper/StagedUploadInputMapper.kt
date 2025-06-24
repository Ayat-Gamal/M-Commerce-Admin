package com.example.m_commerce_admin.features.products.data.mapper

import com.apollographql.apollo.api.Optional
import com.example.m_commerce_admin.features.products.data.model.StagedUploadInput as LocalInput
import com.example.m_commerce_admin.type.StagedUploadInput as GQLInput

fun LocalInput.toGraphQL(): GQLInput {
    return GQLInput(
        filename = this.fileName,
        mimeType = this.mimeType,
        resource = this.resource
    )
}


fun List<LocalInput>.toGraphQL(): List<GQLInput> = this.map { it.toGraphQL() }
