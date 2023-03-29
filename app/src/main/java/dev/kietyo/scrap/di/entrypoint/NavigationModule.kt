
package dev.kietyo.scrap.di.entrypoint

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dev.kietyo.scrap.di.component.ComposableComponent

@InstallIn(ComposableComponent::class)
@EntryPoint
interface ComposableEntryPoint {

}