
package dev.kietyo.scrap.navigation

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import dev.kietyo.scrap.navigation.routing.NavigableRoute
import dev.kietyo.scrap.navigation.routing.ScreenRoute

class NavigationControllerImpl constructor(
    private val navHostController: NavHostController
) : NavigationController {

    override suspend fun <T : ScreenRoute> navigateTo(
        destinationRoute: NavigableRoute<T>
    ) {
        navHostController.safeNavigate(destinationRoute.path) { }
    }

    override suspend fun <R : ScreenRoute, PR : ScreenRoute> navigateBackTo(
        destinationRoute: NavigableRoute<R>,
        parentRoute: NavigableRoute<PR>
    ) {
        navHostController.safeNavigate(destinationRoute.path) {
            popUpTo(parentRoute.path)
        }
    }

    override fun getNavController(): NavHostController = navHostController
}

/**
 * Safe navigation used to handle unsupported routes
 * @param route destination route
 * @param builder NavOptionsBuilder associated to the route
 */
private fun NavController.safeNavigate(route: String, builder: NavOptionsBuilder.() -> Unit) {
    try {
        this.navigate(route, builder)
    } catch (e: IllegalArgumentException) {
        //TODO define your fallback
        e.printStackTrace()
    }
}