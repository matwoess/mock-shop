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

    private val basket = mutableStateOf(mutableListOf<Product>())

    fun getBasket() = basket.value

    fun addToBasket(prod: Product) {
        basket.value += prod
    }

    private val wishList = mutableStateOf(mutableListOf<Product>())

    fun getWishList() = wishList.value

    fun addToWishList(prod: Product) {
        wishList.value += prod
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
