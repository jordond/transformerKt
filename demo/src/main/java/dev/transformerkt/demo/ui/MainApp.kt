package dev.transformerkt.demo.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import dev.transformerkt.demo.theme.TransformerKtDemoTheme

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainApp(
    darkTheme: Boolean,
    dynamicTheme: Boolean = true,
) {
    TransformerKtDemoTheme(darkTheme, dynamicTheme) {
        val engine = rememberNavHostEngine()
        val navController = engine.rememberNavController()

        val startRoute = NavGraphs.root.startRoute

        val bottomSheetNavigator = rememberBottomSheetNavigator()
        navController.navigatorProvider += bottomSheetNavigator

        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = RoundedCornerShape(16.dp)
        ) {
            DestinationsNavHost(
                engine = engine,
                navController = navController,
                navGraph = NavGraphs.root,
                startRoute = startRoute,
            )
        }
    }
}
