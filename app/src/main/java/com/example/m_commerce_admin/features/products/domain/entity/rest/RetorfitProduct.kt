//package com.example.m_commerce_admin.features.products.domain.entity.rest
//
//import com.example.m_commerce_admin.features.products.data.retrofitRemote.ImageDto
//import com.example.m_commerce_admin.features.products.data.retrofitRemote.ProductDto
//import com.example.m_commerce_admin.features.products.data.retrofitRemote.VariantDto
//
//data class RetrofitProduct(
//    val id: Long,
//    val title: String,
//    val descriptionHtml: String,
//    val productType: String,
//    val vendor: String,
//    val variants: List<RetrofitProductVariant>,
//    val image: List<ImageDto>,
//
//
//)
///*
//ProductCard: RestProductImage(id=47455362515193, src=https://cdn.shopify.com/s/files/1/0755/0271/5129/files/product_29_image1.jpg?v=1749927984, alt=ADIDAS | CLASSIC BACKPACK, width=635, height=560, position=1)
//ProductCard: RestProduct(id=8845374095609, title=ADIDAS | CLASSIC BACKPACK, descriptionHtml="This women kpack has a glam look., productType=ACCESSORIES, vendor=ADIDAS, status=active, createdAt=2025-06-14T15:06:22-04:00, updatedAt=2025-06-26T06:40:27-04:00, publishedAt=2025-06-14T15:06:25-04:00, templateSuffix=null, handle=adidas-classic-backpack, tags=adidas, backpack, clothes, egnition-sample-data, variants=[RestProductVariant(id=46559953322233, sku=AD-03-OS-black, price=90.00, inventoryItemId=48643403022585, quantity=15, title=OS / black, weight=0.0, weightUnit=kg, barcode=null, inventoryManagement=shopify, inventoryPolicy=deny, fulfillmentService=manual, taxable=true, requiresShipping=true)], images=[RestProductImage(id=47455362515193, src=https://cdn.shopify.com/s/files/1/0755/0271/5129/files/product_29_image1.jpg?v=1749927984, alt=ADIDAS | CLASSIC BACKPACK, width=635, height=560, position=1), RestProductImage(id=47455362482425, src=https://cdn.shopify.com/s/files/1/0755/0271/5129/files/product_29_image2.jpg?v=1749927984, alt=ADIDAS | CLASSIC BACKPACK, width=635, height=560, position=2), RestProductImage(id=47455362547961, src=https://cdn.shopify.com/s/files/1/0755/0271/5129/files/product_29_image3.jpg?v=1749927984, alt=ADIDAS | CLASSIC BACKPACK, width=635, height=560, position=3)], options=[RestProductOption(id=11142378094841, name=Size, position=1, values=[OS]), RestProductOption(id=11142378127609, name=Color, position=2, values=[black])]).images
//*/
//data class RetrofitProductVariant(
//    val id: Long,
//    val sku: String,
//    val price: String,
//    val inventoryItemId: Long,
//    val quantity: Int
//)
//
//fun ProductDto.toDomain() = RetrofitProduct(
//    id = id,
//    title = title,
//    descriptionHtml = descriptionHtml.orEmpty(),
//    productType = productType.orEmpty(),
//    vendor = vendor.orEmpty(),
//    variants = variants.map { it.toDomain() },
//    image = images.orEmpty()
//)
//
//fun VariantDto.toDomain() = RetrofitProductVariant(
//    id = id,
//    sku = sku.orEmpty(),
//    price = price,
//    inventoryItemId = inventoryItemId,
//    quantity = quantity
//)
