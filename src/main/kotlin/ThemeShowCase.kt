import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun ThemeShowCaseLight() {
    AppTheme(false) {
        ThemeShowCase()
    }
}

@Composable
@Preview
fun ThemeShowCaseDark() {
    AppTheme(true) {
        ThemeShowCase()
    }
}

@Composable
fun ThemeShowCase() {
    Column {
        ColorBox(MaterialTheme.colors.primary, "primary")
        ColorBox(MaterialTheme.colors.primaryVariant, "primaryVariant")
        ColorBox(MaterialTheme.colors.secondary, "secondary")
        ColorBox(MaterialTheme.colors.secondaryVariant, "secondaryVariant")
        ColorBox(MaterialTheme.colors.background, "background")
        ColorBox(MaterialTheme.colors.surface, "surface")
        ColorBox(MaterialTheme.colors.error, "error")
        ColorBox(MaterialTheme.colors.onPrimary, "onPrimary")
        ColorBox(MaterialTheme.colors.onSecondary, "onSecondary")
        ColorBox(MaterialTheme.colors.onBackground, "onBackground")
        ColorBox(MaterialTheme.colors.onSurface, "onSurface")
        ColorBox(MaterialTheme.colors.onError, "onError")
    }
}

@Composable
fun ColorBox(color: Color, colorName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .background(color = color)
        )
        Text(" - $colorName")
    }
}