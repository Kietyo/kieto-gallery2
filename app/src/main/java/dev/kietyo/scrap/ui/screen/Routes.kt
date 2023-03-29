
package dev.kietyo.scrap.ui.screen

import androidx.navigation.NavType
import dev.kietyo.scrap.navigation.routing.ScreenRoute

object Routes {

    object Login : ScreenRoute(
        routeDefinition = Definition("login")
    )

    object Dashboard : ScreenRoute(
        routeDefinition = Definition("dashboard", argumentKeys = listOf(
            "username" to { type = NavType.StringType; optional = false }
        ))
    )

}