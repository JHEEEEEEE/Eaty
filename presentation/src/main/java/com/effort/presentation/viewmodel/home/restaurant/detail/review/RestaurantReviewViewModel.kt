package com.effort.presentation.viewmodel.home.restaurant.detail.review

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
import com.effort.presentation.model.home.restaurant.detail.comment.CommentModel
import com.effort.presentation.model.home.restaurant.detail.comment.toPresentation
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

    init {
        observeUser() // 로그인한 사용자 정보 감지
    }

    /**
     * 특정 식당의 댓글 목록을 불러온다.
     * - `restaurantId`에 해당하는 댓글 데이터를 가져오고 UI 상태를 업데이트
     * - 데이터 로딩 시 로딩 상태를 반영하고, 완료 후 상태 업데이트
     *
     * @param restaurantId 조회할 식당의 ID
     */
    fun getComments(restaurantId: String) {
        viewModelScope.launch {
            getCommentUseCase(restaurantId)
                .onStart { setLoadingState(_getCommentState) } // 로딩 상태 반영
                .onCompletion { cause -> handleCompletionState(_getCommentState, cause) } // 완료 시 상태 처리
                .collectLatest { dataResource ->
                    _getCommentState.value = dataResource.toUiStateList { it.toPresentation() }
                }
        }
    }

    /**
     * 새로운 댓글을 추가한다.
     * - 현재 로그인한 사용자의 정보를 기반으로 댓글을 생성하여 저장
     * - 사용자 정보가 없으면 오류 상태 반환
     * - 댓글 추가 성공 여부에 따라 UI 상태 업데이트
     *
     * @param restaurantId 댓글을 추가할 식당의 ID
     * @param content 사용자가 입력한 댓글 내용
     */
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
            val dataResource = addCommentUseCase(restaurantId, newComment.toDomain())
            _addCommentState.value = when (dataResource) {
                is DataResource.Success -> UiState.Success(dataResource.data)
                is DataResource.Error -> UiState.Error(dataResource.throwable)
                else -> UiState.Empty
            }
        }
    }

    /**
     * 사용자 정보 업데이트 감지한다.
     * - 로그인한 사용자 정보를 지속적으로 감시하고 변경 사항을 반영
     */
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