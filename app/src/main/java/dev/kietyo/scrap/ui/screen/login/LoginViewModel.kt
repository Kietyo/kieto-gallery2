
package dev.kietyo.scrap.ui.screen.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kietyo.scrap.navigation.routing.generatePath
import dev.kietyo.scrap.ui.screen.Routes
import dev.kietyo.scrap.ui.screen.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    private val mUsernameState = MutableStateFlow("")
    val usernameState: StateFlow<String>
        get() = mUsernameState

    fun onChangeUsername(username: String) {
        mUsernameState.value = username
    }

    fun onClickLogin() = viewModelScope.launch {
        navigateTo(Routes.Dashboard.generatePath(
            "username" to usernameState.value
        ))
    }

}