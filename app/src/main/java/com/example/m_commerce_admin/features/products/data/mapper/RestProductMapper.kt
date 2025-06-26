package com.example.m_commerce_admin.features.products.data.mapper

import com.example.m_commerce_admin.features.products.data.retrofitRemote.*
import com.example.m_commerce_admin.features.products.domain.entity.*

// DTO to Domain mappers
fun ProductDto.toRestProduct(): RestProduct {
    return RestProduct(
        id = id,
        title = title,
        descriptionHtml = descriptionHtml,
        productType = productType,
        vendor = vendor,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt,
        templateSuffix = templateSuffix,
        handle = handle,
        tags = tags,
        variants = variants.map { it.toRestProductVariant() },
        images = images?.map { it.toRestProductImage() },
        options = options?.map { it.toRestProductOption() }
    )
}

fun VariantDto.toRestProductVariant(): RestProductVariant {
    return RestProductVariant(
        id = id,
        sku = sku,
        price = price,
        inventoryItemId = inventoryItemId,
        quantity = quantity,
        title = title,
        weight = weight,
        weightUnit = weightUnit,
        barcode = barcode,
        inventoryManagement = inventoryManagement,
        inventoryPolicy = inventoryPolicy,
        fulfillmentService = fulfillmentService,
        taxable = taxable,
        requiresShipping = requiresShipping
    )
}

fun ImageDto.toRestProductImage(): RestProductImage {
    return RestProductImage(
        id = id,
        src = src,
        alt = alt,
        width = width,
        height = height,
        position = position
    )
}

fun OptionDto.toRestProductOption(): RestProductOption {
    return RestProductOption(
        id = id,
        name = name,
        position = position,
        values = values
    )
}

// Domain to DTO mappers
fun RestProductInput.toProductCreateDto(): ProductCreateDto {
    return ProductCreateDto(
        title = title,
        descriptionHtml = descriptionHtml,
        productType = productType,
        vendor = vendor,
        status = status,
        tags = tags,
        variants = variants?.map { it.toVariantCreateDto() },
        images = images?.map { it.toImageCreateDto() },
        options = options?.map { it.toOptionCreateDto() }
    )
}

fun RestProductUpdateInput.toProductUpdateDto(): ProductUpdateDto {
    return ProductUpdateDto(
        title = title,
        descriptionHtml = descriptionHtml,
        productType = productType,
        vendor = vendor,
        status = status,
        tags = tags,
        variants = variants?.map { it.toVariantUpdateDto() },
        images = images?.map { it.toImageCreateDto() },
        options = options?.map { it.toOptionCreateDto() }
    )
}

fun RestProductVariantInput.toVariantCreateDto(): VariantCreateDto {
    return VariantCreateDto(
        price = price,
        sku = sku,
        title = title,
        weight = weight,
        weightUnit = weightUnit,
        barcode = barcode,
        inventoryManagement = inventoryManagement,
        inventoryPolicy = inventoryPolicy,
        fulfillmentService = fulfillmentService,
        taxable = taxable,
        requiresShipping = requiresShipping
    )
}

fun RestProductVariantUpdateInput.toVariantUpdateDto(): VariantUpdateDto {
    return VariantUpdateDto(
        id = id,
        price = price,
        sku = sku,
        title = title,
        weight = weight,
        weightUnit = weightUnit,
        barcode = barcode,
        inventoryManagement = inventoryManagement,
        inventoryPolicy = inventoryPolicy,
        fulfillmentService = fulfillmentService,
        taxable = taxable,
        requiresShipping = requiresShipping
    )
}

fun RestProductImageInput.toImageCreateDto(): ImageCreateDto {
    return ImageCreateDto(
        src = src,
        alt = alt,
        position = position
    )
}

fun RestProductOptionInput.toOptionCreateDto(): OptionCreateDto {
    return OptionCreateDto(
        name = name,
        position = position,
        values = values
    )
} 