package com.effort.presentation.viewmodel.home.detail.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.comment.AddCommentUseCase
import com.effort.domain.usecase.home.restaurant.detail.comment.GetCommentUseCase
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.comment.CommentModel
import com.effort.presentation.model.home.comment.toPresentation
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
class RestaurantReviewViewModel @Inject constructor(
    private val getCommentUseCase: GetCommentUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val observeUserUpdateUseCase: ObserveUserUpdateUseCase
) : ViewModel() {

    private val _getCommentState = MutableStateFlow<UiState<List<CommentModel>>>(UiState.Empty)
    val getCommentState = _getCommentState.asStateFlow()

    private val _addCommentState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val addCommentState = _addCommentState.asStateFlow()

    private val _userState = MutableStateFlow<UserModel?>(null)
    val userState = _userState.asStateFlow()

    init {
        observeUser()
    }

    fun getComments(restaurantId: String) {
        viewModelScope.launch {
            getCommentUseCase(restaurantId)
                .onStart { setLoadingState(_getCommentState) }
                .onCompletion { cause -> handleCompletionState(_getCommentState, cause) }
                .collectLatest { dataResource ->
                    _getCommentState.value = dataResource.toUiStateList { it.toPresentation() }
                }
        }
    }

    fun addComment(restaurantId: String, content: String) {
        val currentUser = _userState.value
        if (currentUser == null) {
            _addCommentState.value = UiState.Error(Exception("User not logged in"))
            return
        }

        val newComment = CommentModel(
            content = content,
            userId = currentUser.email,
            userNickname = currentUser.nickname,
            timestamp = System.currentTimeMillis().toString()
        )

        viewModelScope.launch {
            _addCommentState.value = UiState.Loading
            val result = addCommentUseCase(restaurantId, newComment.toDomain())
            _addCommentState.value = when (result) {
                is DataResource.Success -> UiState.Success(result.data)
                is DataResource.Error -> UiState.Error(result.throwable)
                else -> UiState.Empty
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            observeUserUpdateUseCase()
                .collectLatest { userResource ->
                    if (userResource is DataResource.Success) {
                        _userState.value = userResource.data.toPresentation()
                    }
                }
        }
    }
}