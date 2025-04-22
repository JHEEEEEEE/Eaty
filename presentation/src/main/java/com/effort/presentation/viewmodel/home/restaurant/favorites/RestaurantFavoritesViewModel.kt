package com.effort.presentation.viewmodel.home.restaurant.favorites

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
import timber.log.Timber
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

    /**
     * 사용자가 특정 식당을 찜 목록에 추가한다.
     * - 현재 로그인된 사용자를 확인 후 실행
     * - `addRestaurantToFavoritesUseCase`를 호출하여 데이터 업데이트
     *
     * @param restaurant 찜할 식당 정보
     */
    fun addRestaurantToFavorites(restaurant: RestaurantModel) {
        Timber.d("addRestaurantToFavorites() 호출 - 식당: ${restaurant.title}")

        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _addFavoriteState,
            onSuccessState = _addFavoriteState
        ) { user ->
            addRestaurantToFavoritesUseCase(user?.email ?: "", restaurant.toDomain()).also {
                Timber.d("addRestaurantToFavorites() 완료 - 결과: $it")
            }
        }
    }

    /**
     * 사용자가 특정 식당을 찜 목록에서 제거한다.
     * - 현재 로그인된 사용자를 확인 후 실행
     * - `removeRestaurantFromFavoritesUseCase`를 호출하여 데이터 업데이트
     *
     * @param restaurantName 제거할 식당의 이름
     */
    fun removeRestaurantFromFavorites(restaurantName: String) {
        Timber.d("removeRestaurantFromFavorites() 호출 - 식당: $restaurantName")

        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _removeFavoriteState,
            onSuccessState = _removeFavoriteState
        ) { user ->
            removeRestaurantFromFavoritesUseCase(user?.email ?: "", restaurantName).also {
                Timber.d("removeRestaurantFromFavorites() 완료 - 결과: $it")
            }
        }
    }

    /**
     * 특정 식당이 사용자의 찜 목록에 있는지 확인한다.
     * - 현재 로그인된 사용자를 확인 후 실행
     * - `checkIfRestaurantIsFavoriteUseCase`를 호출하여 상태 업데이트
     *
     * @param restaurantName 확인할 식당의 이름
     */
    fun checkIfRestaurantIsFavorite(restaurantName: String) {
        Timber.d("checkIfRestaurantIsFavorite() 호출 - 식당: $restaurantName")

        executeWithCurrentUser(
            userState = _userState,
            onErrorState = _isFavoriteState,
            onSuccessState = _isFavoriteState
        ) { user ->
            checkIfRestaurantIsFavoriteUseCase(user?.email ?: "", restaurantName).also {
                Timber.d("checkIfRestaurantIsFavorite() 결과 - 식당: $restaurantName, 상태: $it")
            }
        }
    }

    /**
     * 사용자의 찜 목록을 가져온다.
     * - 로그인된 사용자를 확인 후 실행
     * - `getFavoriteListUseCase`를 호출하여 찜한 식당 목록을 가져옴
     */
    fun fetchFavorites() {
        Timber.d("fetchFavorites() 호출")

        val currentUser =
            validateCurrentUser(_userState, _getFavoriteState) ?: return // 사용자 확인 실패 시 종료

        val userId = currentUser.email

        viewModelScope.launch {
            getFavoriteListUseCase(userId)
                .onStart {
                    Timber.d("fetchFavorites() 데이터 로딩 중")
                    setLoadingState(_getFavoriteState) // 로딩 상태로 업데이트
                }
                .onCompletion { cause ->
                    Timber.d("fetchFavorites() 완료 - 상태: $cause")
                    handleCompletionState(_getFavoriteState, cause) // 로딩 종료 처리
                }
                .collectLatest { dataResource ->
                    _getFavoriteState.value = dataResource.toUiStateList { it.toPresentation() }
                    Timber.d("fetchFavorites() 성공 - 데이터 개수: ${_getFavoriteState.value}")
                }
        }
    }

    /**
     * 사용자 상태를 관찰하여 UI를 업데이트한다.
     * - 로그인된 사용자의 정보를 감지하고 상태를 업데이트
     */
    private fun observeUser() {
        Timber.d("observeUser() 호출")

        viewModelScope.launch {
            observeUserUpdateUseCase()
                .collectLatest { userResource ->
                    _userState.value = when (userResource) {
                        is DataResource.Success -> {
                            Timber.d("observeUser() - 사용자 업데이트됨: ${userResource.data.email}")
                            UiState.Success(userResource.data.toPresentation())
                        }
                        is DataResource.Error -> {
                            Timber.e(userResource.throwable, "observeUser() - 사용자 정보 업데이트 실패")
                            UiState.Error(userResource.throwable)
                        }
                        is DataResource.Loading -> {
                            Timber.d("observeUser() - 사용자 정보 로딩 중")
                            UiState.Loading
                        }
                    }
                }
        }
    }
}