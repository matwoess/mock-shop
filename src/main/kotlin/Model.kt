enum class ProductCategory(val catName: String) {
    ALL("All Categories"),
    PRODUCTS("Products"),
    DEVICES("Devices"),
    LIVING("Living"),
    MISCELLANEOUS("Miscellaneous"),
}

data class Product(val productId: Long) {
    var name: String = "<name missing>"
    var category: ProductCategory = ProductCategory.MISCELLANEOUS
    var description: String = "<no description yet>"
    var price: Float = -1f
    var image: String = "blank.svg"
}