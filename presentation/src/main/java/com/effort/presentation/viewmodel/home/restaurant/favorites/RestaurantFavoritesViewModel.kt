package com.effort.presentation.viewmodel.home.restaurant.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.favorites.AddRestaurantToFavoritesUseCase
import com.effort.domain.usecase.home.restaurant.favorites.CheckIfRestaurantIsFavoriteUseCase
import com.effort.domain.usecase.home.restaurant.favorites.GetFavoriteListUseCase
import com.effort.domain.usecase.home.restaurant.favorites.RemoveRestaurantFromFavoritesUseCase
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.executeWithCurrentUser
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.core.util.validateCurrentUser
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.model.home.restaurant.toPresentation
import com.effort.presentation.model.mypage.UserModel
import com.effort.presentation.model.mypage.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantFavoritesViewModel @Inject constructor(
    private val addRestaurantToFavoritesUseCase: AddRestaurantToFavoritesUseCase,
    private val removeRestaurantFromFavoritesUseCase: RemoveRestaurantFromFavoritesUseCase,
    private val checkIfRestaurantIsFavoriteUseCase: CheckIfRestaurantIsFavoriteUseCase,
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    private val observeUserUpdateUseCase: ObserveUserUpdateUseCase
) : ViewModel() {

    private val _addFavoriteState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val addFavoriteState get() = _addFavoriteState.asStateFlow()

    private val _removeFavoriteState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val removeFavoriteState get() = _removeFavoriteState.asStateFlow()

    private val _isFavoriteState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val isFavoriteState get() = _isFavoriteState.asStateFlow()

    private val _userState = MutableStateFlow<UiState<UserModel?>>(UiState.Empty)
    val userState = _userState.asStateFlow()

    private val _getFavoriteState = MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getFavoriteState = _getFavoriteState.asStateFlow()

    init {
        observeUser()
    }

    fun addRestaurantToFavorites(restaurant: RestaurantModel) {
        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _addFavoriteState,
            onSuccessState = _addFavoriteState
        ) { user ->
            addRestaurantToFavoritesUseCase(user?.email ?: "", restaurant.toDomain())
        }
    }

    fun removeRestaurantFromFavorites(restaurantName: String) {
        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _removeFavoriteState,
            onSuccessState = _removeFavoriteState
        ) { user ->
            removeRestaurantFromFavoritesUseCase(user?.email ?: "", restaurantName)
        }
    }

    fun checkIfRestaurantIsFavorite(restaurantName: String) {
        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _isFavoriteState,
            onSuccessState = _isFavoriteState
        ) { user ->
            checkIfRestaurantIsFavoriteUseCase(user?.email ?: "", restaurantName)
        }
    }

    fun fetchFavorites() {
        val currentUser =
            validateCurrentUser(_userState, _getFavoriteState) ?: return // 사용자 확인 실패 시 종료

        val userId = currentUser.email

        viewModelScope.launch {
            getFavoriteListUseCase(userId)
                .onStart {
                    // 로딩 상태로 업데이트
                    setLoadingState(_getFavoriteState)
                }
                .onCompletion { cause ->
                    // 로딩 종료
                    handleCompletionState(_getFavoriteState, cause)
                }
                .collectLatest { dataResource ->
                    _getFavoriteState.value = dataResource.toUiStateList { it.toPresentation() }
                }
        }
    }

    // 사용자 상태 관찰
    private fun observeUser() {
        viewModelScope.launch {
            observeUserUpdateUseCase()
                .collectLatest { userResource ->
                    _userState.value = when (userResource) {
                        is DataResource.Success -> {
                            Log.d(
                                "RestaurantFavoritesViewModel",
                                "User updated: ${userResource.data.email}"
                            )
                            UiState.Success(userResource.data.toPresentation())
                        }

                        is DataResource.Error -> {
                            Log.e(
                                "RestaurantFavoritesViewModel",
                                "Failed to update user: ${userResource.throwable.message}"
                            )
                            UiState.Error(userResource.throwable)
                        }

                        is DataResource.Loading -> {
                            UiState.Loading
                        }
                    }
                }
        }
    }
}