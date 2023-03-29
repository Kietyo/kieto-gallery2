
package dev.kietyo.scrap.navigation.routing

interface NavigableRoute<R: ScreenRoute> {

    val screenRoute: R

    val path: String

}