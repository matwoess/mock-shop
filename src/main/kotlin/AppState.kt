import androidx.compose.runtime.mutableStateOf

object AppState {
    private var darkMode = mutableStateOf(false)
    fun getDarkMode() = darkMode.value
    fun setDarkMode(on: Boolean) {
        darkMode.value = on
    }

    private val selectedCategory = mutableStateOf(ProductCategory.ALL)

    fun getCurrentCategory() = selectedCategory.value

    fun selectCategory(cat: ProductCategory) {
        selectedCategory.value = cat
        setExpandedItem(-1L)
    }

    private val products = mutableStateOf(mutableListOf<Product>())

    fun getProductsByCategory(cat: ProductCategory) =
        if (cat == ProductCategory.ALL) {
            products.value
        } else {
            products.value.filter { it.category == cat }
        }

    fun addProduct(prod: Product) {
        products.value += prod
    }

    private val shoppingCart = mutableStateOf(mutableMapOf<Product, Int>())

    fun getShoppingCart() = shoppingCart.value

    fun addToShoppingCart(prod: Product) {
        if (!shoppingCartContains(prod)) {
            shoppingCart.value[prod] = 1
        } else {
            shoppingCart.value[prod] = shoppingCart.value[prod]!! + 1
        }
    }

    fun removeFromShoppingCart(prod: Product) {
        if (shoppingCartContains(prod)) {
            val amount = shoppingCart.value[prod]!!
            if (amount == 1) {
                shoppingCart.value.remove(prod)
            } else {
                shoppingCart.value[prod] = shoppingCart.value[prod]!! - 1
            }
        }
    }

    private fun shoppingCartContains(prod: Product): Boolean {
        return prod in shoppingCart.value.keys
    }

    fun getShoppingCartAmountOf(prod: Product): Int {
        return shoppingCart.value[prod] ?: 0
    }

    fun getShoppingCartTotal(): Float {
        var total: Float = 0f
        for ((prod, amount) in shoppingCart.value) {
            total += prod.price * amount
        }
        return total
    }

    private val wishList = mutableStateOf(mutableListOf<Product>())

    fun getWishList() = wishList.value

    fun addToWishList(prod: Product) {
        if (!wishListContains(prod)) {
            wishList.value += prod
        }

    }

    fun removeFromWishList(prod: Product) {
        if (wishListContains(prod)) {
            wishList.value -= prod
        }
    }

    fun wishListContains(prod: Product): Boolean {
        return prod in wishList.value
    }

    private var expandedItem = mutableStateOf(-1L)

    fun getExpandedItem() = expandedItem.value

    fun setExpandedItem(id: Long) {
        if (expandedItem.value == id) {
            expandedItem.value = -1
        } else {
            expandedItem.value = id
        }
    }
}
