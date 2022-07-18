package it.stefanotroia.mvpmatch.controller.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.stefanotroia.mvpmatch.model.product.ProductModel
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.repository.PageResponse
import java.security.Principal
import java.util.*

@ApiResponses(
    ApiResponse(
        description = "unauthorized",
        responseCode = "401",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
    ApiResponse(
        description = "forbidden",
        responseCode = "403",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
    ApiResponse(
        description = "bad request",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
)
interface ProductSwagger {


    @Operation(
        tags = ["products"],
        summary = "Get list of products",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "list",
            content = [Content(schema = Schema(implementation = PageResponse::class, description = "list"))]
        ),
    )
    fun listProducts(offset: Int = 0, limit: Int = 10): PageResponse<ProductModel>



    @Operation(
        tags = ["products"],
        summary = "Get product by id",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "product",
            content = [Content(schema = Schema(implementation = ProductModel::class, description = "product"))]
        ),
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun getProduct(productId: UUID): ProductModel


    @Operation(
        tags = ["products"],
        summary = "Create product",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "product",
            content = [Content(schema = Schema(implementation = ProductModel::class, description = "product"))]
        ),
        ApiResponse(
            description = "conflict",
            responseCode = "409",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun createProduct(product: ProductModel, @Parameter(hidden = true) principal: Principal): ProductModel


    @Operation(
        tags = ["products"],
        summary = "Update product",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "product",
            content = [Content(schema = Schema(implementation = ProductModel::class, description = "product"))]
        ),
        ApiResponse(
            description = "conflict",
            responseCode = "409",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun updateProduct(
        productId: UUID,
        product: ProductModel,
        @Parameter(hidden = true) principal: Principal
    ): ProductModel


    @Operation(
        tags = ["products"],
        summary = "Delete product by id",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun deleteProduct(
        productId: UUID,
        @Parameter(hidden = true) principal: Principal
    )
}
