import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

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
                toggleTheme = { state.setDarkMode(!state.getDarkMode()) },
                expandWishList = { },
                expandBasket = { })
            Body(state)
        }
    }
}

@Composable
fun Header(state: AppState, toggleTheme: () -> Unit, expandWishList: () -> Unit, expandBasket: () -> Unit) {
    TopAppBar {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .padding(start = 16.dp, end = 16.dp),
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
                color = MaterialTheme.colors.onPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = toggleTheme,
                ) {
                    Text("Theme")
                }
                Button(
                    onClick = expandWishList,
                ) {
                    Icon(Icons.Default.List, contentDescription = "Your wish list")
                }
                Button(
                    onClick = expandBasket,
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Your shopping cart")
                }
            }
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
                onClick = { state.selectCategory(cat); state.setExpandedItem(-1L) },
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
                    addToBasket = { AppState.addToBasket(it) },
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
    addToBasket: () -> Unit,
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
                Text("€ ${"%.2f".format(prod.price)},-")
                if (state.getExpandedItem() == prod.productId) {
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = addToWishList, modifier = Modifier.fillMaxWidth()) {
                            Text("Add to Wish List")
                        }
                        Button(
                            onClick = addToBasket,
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