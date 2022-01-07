import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
        title = "Mock-Shop",
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
    TopAppBar(elevation = 8.dp) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Image(
                    painter = painterResource("m_letter.svg"),
                    contentDescription = "App logo",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "ock-Shop",
                    style = MaterialTheme.typography.h5,
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShoppingCartPreviewButton(state)
                WishListPreviewButton(state)
                IconButton(onClick = toggleTheme) {
                    Icon(Icons.Default.Settings, contentDescription = "Toggle dark theme")
                }
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
                    Text(
                        text = "Currently no items in wish list.",
                        modifier = Modifier.padding(16.dp),
                    )
                } else {
                    for (item in state.getWishList()) {
                        WishListItem(
                            item = item,
                            onRemove = { state.removeFromWishList(item) },
                            onMove = { state.removeFromWishList(item); state.addToShoppingCart(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishListItem(item: Product, onRemove: () -> Unit, onMove: () -> Unit) {
    var inList by remember { mutableStateOf(true) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 8.dp),
    ) {
        Text(
            text = item.name,
            style = if (!inList) TextStyle(
                fontSize = MaterialTheme.typography.h6.fontSize,
                textDecoration = TextDecoration.LineThrough
            ) else MaterialTheme.typography.h6,
        )
        Spacer(Modifier.width(16.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                enabled = inList,
                onClick = {
                    onRemove()
                    inList = false
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from List",
                )
            }
            IconButton(
                enabled = inList,
                onClick = {
                    onMove()
                    inList = false
                },
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
        modifier = Modifier.padding(end = 8.dp)
    ) {
        IconButton(
            enabled = amount > 0,
            onClick = {
                onDecrease()
                amount = getAmount()
            }
        ) {
            Icon(
                painterResource("minus.svg"),
                contentDescription = "Decrease amount",
            )
        }
        IconButton(
            onClick = {
                onIncrease()
                amount = getAmount()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase amount"
            )
        }
        Spacer(Modifier.width(24.dp))
        Text(
            text = "${amount}x",
            style = MaterialTheme.typography.h6
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.name,
            style = if (amount <= 0) TextStyle(
                fontSize = MaterialTheme.typography.h6.fontSize,
                textDecoration = TextDecoration.LineThrough
            ) else MaterialTheme.typography.h6,
        )
        Spacer(Modifier.width(24.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = formatPrice(item.price * amount),
                style = MaterialTheme.typography.h6,
            )
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
            style = MaterialTheme.typography.h5
        )
    }
}

fun formatPrice(price: Float): String {
    val formatter = DecimalFormat("#,###.00")
    return "€ ${formatter.format(price)},-"
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Checkout(itemCount: Int, totalPrice: Float, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Column {
        var showDialog by remember { mutableStateOf(false) }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }

        if (showDialog) {
            AlertDialog(
                modifier = Modifier.width(700.dp).padding(16.dp),
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Confirm payment",
                        style = MaterialTheme.typography.h5
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Please confirm buying these $itemCount items for a total amount of ${
                                formatPrice(totalPrice)
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
                            showDialog = false
                            onConfirm()
                        }) {
                        Text("Confirm Payment")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
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
                text = "Categories:",
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
        items(ProductCategory.values()) { cat ->
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
            items(state.getProductsByCategory(state.getCurrentCategory())) { product ->
                ProductCard(
                    prod = product,
                    isExpanded = state.getExpandedItem() == product.productId,
                    onExpand = { AppState.setExpandedItem(product.productId) },
                    addToWishList = { AppState.addToWishList(product) },
                    addToShoppingCart = { AppState.addToShoppingCart(product) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProductCard(
    prod: Product,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    addToWishList: () -> Unit,
    addToShoppingCart: () -> Unit,
) {
    val elevation by animateDpAsState(if (isExpanded) 8.dp else 0.dp)
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = elevation,
    ) {
        Row(Modifier
            .clickable { onExpand() }
            .padding(all = 16.dp)
        ) {
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
                AnimatedVisibility(isExpanded) {
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = addToWishList,
                            modifier = Modifier.fillMaxWidth()
                        ) {
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