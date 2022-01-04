import ProductCategory.*

object DemoData {
    fun fillProducts(state: AppState) {
        val products = mutableListOf(
            Product(0).apply {
                name = "Heinmann's Bread"
                category = PRODUCTS
                description = "Nutritious bread"
                price = 2.19f
                image = "bread.svg"
            },
            Product(1).apply {
                name = "Alp Milk"
                category = PRODUCTS
                description = "Best Milk in the Galaxy"
                price = 1.19f
                image = "milk.png"
            },
            Product(2).apply {
                name = "Emmental cheese"
                category = PRODUCTS
                description = "Tasty, juicy and local cheese"
                price = 2.49f
                image = "cheese.svg"
            },
            Product(3).apply {
                name = "iPhone 13 Pro"
                category = DEVICES
                description = "The latest elite phone from Apple"
                price = 1_199f
                image = "phone.svg"
            },
            Product(4).apply {
                name = "iPhone 13"
                category = DEVICES
                description = "The latest phone from Apple"
                price = 999f
                image = "phone.svg"
            },
            Product(5).apply {
                name = "Pixel 6 Pro - 128GB Internal Memory, AT&T, 6GB RAM"
                category = DEVICES
                description = "The latest flagship from Google"
                price = 799f
                image = "phone.svg"
            },
            Product(6).apply {
                name = "Pixel 6 (5G variant, 256GB Internal Memory, Unlocked)"
                category = DEVICES
                description = "The latest flagship from Google with 5G"
                price = 649f
                image = "phone.svg"
            },
            Product(7).apply {
                name = "MaxBoost Speakers - Feel the bass!"
                category = DEVICES
                description = "Immersive speakers"
                price = 79.24f
                image = "speakers.png"
            },
            Product(8).apply {
                name = "Nightstand Lamp (AC-Cable included)"
                category = LIVING
                description = "Brighten up your rooms"
                price  = 33.65f
                image = "lamp.png"
            },
            Product(9).apply {
                name = "Aquarium"
                category = LIVING
                description = "Keep your fish save and visible"
                price = 139f
                image = "aquarium.png"
            },
            Product(10).apply {
                name = "Couch XXL"
                category = LIVING
                description = "Space four you, your guests and your stuff"
                price = 457.54f
                image = "sofa.svg"
            },
            Product(11).apply {
                name = "Batteries Combo-Pack"
                category = MISCELLANEOUS
                description = "Juice for all your (old) devices"
                price = 8.64f
                image = "batteries.png"
            },
            Product(12).apply {
                name = "Paper 100 Pack"
                category = MISCELLANEOUS
                description = "Print anything on it"
                price = 8.90f
                image = "paper_stack.svg"
            },
            Product(446).apply {
                name = "Tissue box"
                category = MISCELLANEOUS
                description = "For cols or emotional films"
                price = 0.86f
                image = "tissue_box.svg"
            },
        )
        products.forEach { state.addProduct(it) }
    }
}