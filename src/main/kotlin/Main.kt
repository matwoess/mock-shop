import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.text.DecimalFormat

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = R.appTitle,
        icon = painterResource("m_letter.svg")
    ) {
        App()
    }
}

@Composable
@Preview
fun App() {
    val state = remember { AppState }
    DemoData.fillProducts(state)

    AppTheme(state.getDarkMode()) {
        Column {
            Header(
                state = state,
                toggleTheme = { state.setDarkMode(!state.getDarkMode()) }
            )
            Body(state)
        }
    }
}

@Composable
fun Header(state: AppState, toggleTheme: () -> Unit) {
    TopAppBar {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource("m_letter.svg"),
                contentDescription = "App logo",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = R.appTitle.slice(1..R.appTitle.lastIndex),
                style = MaterialTheme.typography.h5,
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = toggleTheme,
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Toggle dark theme")
                }
                WishListPreviewButton(state)
                ShoppingCartPreviewButton(state)
            }
        }
    }
}

@Composable
fun WishListPreviewButton(state: AppState) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        onClick = { expanded = !expanded },
    ) {
        Icon(Icons.Default.List, contentDescription = "Your wish list")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column {
                if (state.getWishList().isEmpty()) {
                    Text("Currently no items in wish list.", Modifier.padding(16.dp))
                } else {
                    for (item in state.getWishList()) {
                        WishListItem(
                            item = item,
                            wishListContains = { state.wishListContains(item) },
                            onRemove = { state.removeFromWishList(item) },
                            onMove = { state.removeFromWishList(item); state.addToShoppingCart(item) },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WishListItem(item: Product, wishListContains: () -> Boolean, onRemove: () -> Unit, onMove: () -> Unit) {
    var inList by remember { mutableStateOf(true) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 8.dp),
    ) {
        Text(
            text = item.name,
            style = if (!inList) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(),
        )
        Spacer(Modifier.width(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    onRemove()
                    inList = wishListContains()
                },
                enabled = inList,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from List",
                )
            }
            IconButton(
                onClick = {
                    onMove()
                    inList = wishListContains()
                },
                enabled = inList,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Move to Shopping Cart",
                )
            }
        }
    }
}

@Composable
fun ShoppingCartPreviewButton(state: AppState) {
    var expanded by remember { mutableStateOf(false) }
    var totalPrice by remember { mutableStateOf(state.getShoppingCartTotal()) }
    val recalculateTotal = { totalPrice = state.getShoppingCartTotal() }
    IconButton(
        onClick = { expanded = !expanded; recalculateTotal() },
    ) {
        Icon(Icons.Default.ShoppingCart, contentDescription = "Your shopping cart")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.padding(16.dp)
        ) {
            if (state.getShoppingCart().isEmpty()) {
                Text("Currently no items in shopping cart.")
            } else {
                Column {
                    for (item in state.getShoppingCart().keys) {
                        ShoppingCartItem(
                            item = item,
                            getAmount = { state.getShoppingCartAmountOf(item) },
                            onIncrease = { state.addToShoppingCart(item); recalculateTotal() },
                            onDecrease = { state.removeFromShoppingCart(item); recalculateTotal() },
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Divider()
                    ShoppingCartTotal(totalPrice)
                    Checkout(
                        itemCount = state.getShoppingCartCount(),
                        totalPrice = totalPrice,
                        onConfirm = { state.buyAllItems(); expanded = false },
                        onDismiss = {},
                    )
                }
            }

        }
    }
}

@Composable
fun ShoppingCartItem(
    item: Product,
    getAmount: () -> Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    var amount by remember { mutableStateOf(getAmount()) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
    ) {
        Text(
            text = "${amount}x",
            style = MaterialTheme.typography.button
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.name,
            style = if (amount <= 0) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(),
        )
        Spacer(Modifier.width(16.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    onDecrease()
                    amount = getAmount()
                },
                enabled = amount > 0,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // TODO
                    contentDescription = "Decrease amount",
                )
            }
            IconButton(
                onClick = {
                    onIncrease()
                    amount = getAmount()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // TODO
                    contentDescription = "Increase amount",
                )
            }
            Text(formatPrice(item.price * amount))
        }
    }
}

@Composable
fun ShoppingCartTotal(totalPrice: Float) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Total: ${formatPrice(totalPrice)}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )
    }
}

fun formatPrice(price: Float): String {
    val formatter = DecimalFormat("#,###.00")
    return "€${formatter.format(price)},-"
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Checkout(itemCount: Int, totalPrice: Float, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Column {
        val showDialog = remember { mutableStateOf(false) }

        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }

        if (showDialog.value) {
            AlertDialog(
                modifier = Modifier.width(600.dp),
                onDismissRequest = {
                    showDialog.value = false
                },
                title = {
                    Text(text = "Confirm payment")
                },
                text = {
                    Column {
                        Text(
                            text = "Please confirm buying these $itemCount items for a total amount of ${
                                formatPrice(
                                    totalPrice
                                )
                            }",
                            style = MaterialTheme.typography.h6,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Shipment will be made to the default address.",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            onConfirm()
                        }) {
                        Text("Confirm Payment")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            onDismiss()
                        }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun Body(state: AppState) {
    Row {
        LeftSideBar(state)
        Divider(
            color = Color.Gray.copy(alpha = .5f),
            modifier = Modifier.fillMaxHeight().width(2.dp)
        )
        Main(state)
    }
}

@Composable
fun LeftSideBar(state: AppState) {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(16.dp)
        ) {
            Text(
                text = R.categoriesHeader,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ProductCategories(state)
        }
    }
}

@Composable
fun ProductCategories(state: AppState) {
    LazyColumn {
        this.items(ProductCategory.values()) { cat ->
            TextButton(
                onClick = { state.selectCategory(cat) },
            ) {
                if (state.getCurrentCategory() == cat) {
                    Text(cat.catName, style = MaterialTheme.typography.h6)
                } else {
                    Text(cat.catName)
                }
            }

        }
    }
}


@Composable
fun Main(state: AppState) {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(all = 8.dp),
        ) {
            items(state.getProductsByCategory(state.getCurrentCategory())) {
                ProductCard(
                    prod = it,
                    state = state,
                    onExpand = { AppState.setExpandedItem(it.productId) },
                    addToWishList = { AppState.addToWishList(it) },
                    addToShoppingCart = { AppState.addToShoppingCart(it) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProductCard(
    prod: Product,
    state: AppState,
    onExpand: () -> Unit,
    addToWishList: () -> Unit,
    addToShoppingCart: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier
            .clickable { onExpand() }
            .padding(all = 16.dp)) {
            Image(
                painter = painterResource(prod.image),
                contentDescription = "Product image",
                modifier = Modifier.size(64.dp).clip(RectangleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = prod.name,
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(8.dp))
                Text(prod.description)
                Spacer(Modifier.height(8.dp))
                Text(formatPrice(prod.price))
                if (state.getExpandedItem() == prod.productId) {
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = addToWishList, modifier = Modifier.fillMaxWidth()) {
                            Text("Add to Wish List")
                        }
                        Button(
                            onClick = addToShoppingCart,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add to Shopping Cart")
                        }
                    }
                }
            }
        }
    }
}