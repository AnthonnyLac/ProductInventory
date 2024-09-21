package com.example.productinventory.model


object Stock {
    private val _products = mutableListOf<Product>()

    val products: List<Product>
        get() = _products

    fun addProduct(product: Product) {
        _products.add(product)
    }

    fun calculateTotalValue(): Double {
        return _products.sumOf { it.price * it.quantity }
    }
}