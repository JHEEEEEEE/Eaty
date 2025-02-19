package com.effort.remote.service.home.restaurant.detail.comment

import com.effort.remote.model.home.restaurant.detail.comment.CommentResponse
import com.effort.remote.model.home.restaurant.detail.comment.CommentWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class CommentServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentService {

    /**
     * Firestore에 댓글 추가한다.
     * - 특정 레스토랑의 "comments" 컬렉션에 댓글 저장
     * - 문서 ID를 사용자 ID로 설정하여 중복 방지
     * - Firestore 작업이 완료될 때까지 await() 사용하여 비동기 처리
     */
    override suspend fun addComment(restaurantId: String, comment: CommentResponse): Boolean {
        return try {
            Timber.i("addComment() 시작 - restaurantId: $restaurantId, userId: ${comment.userId}")

            firestore.collection("restaurants").document(restaurantId).collection("comments")
                .document(comment.userId).set(comment).await()

            Timber.i("댓글 추가 성공 - restaurantId: $restaurantId, userId: ${comment.userId}")
            true
        } catch (e: Exception) {
            Timber.e(e, "addComment() 실패 - restaurantId: $restaurantId, userId: ${comment.userId}")
            throw e
        }
    }

    /**
     * Firestore에서 특정 레스토랑의 댓글 목록을 실시간으로 가져온다.
     * - `callbackFlow`를 사용하여 데이터 변경을 즉시 반영
     * - "timestamp" 기준으로 내림차순 정렬
     * - 스냅샷이 변경될 때마다 데이터를 새로 전송
     */
    override fun getCommentList(restaurantId: String): Flow<CommentWrapperResponse> = callbackFlow {
        Timber.i("getCommentList() 호출 - restaurantId: $restaurantId")

        val listener =
            firestore.collection("restaurants").document(restaurantId).collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error, "getCommentList() 실패 - restaurantId: $restaurantId")
                        trySend(CommentWrapperResponse(resultComments = emptyList())).isSuccess
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val comments = snapshot.documents.mapNotNull {
                            it.toObject(CommentResponse::class.java)
                        }
                        Timber.d("getCommentList() 성공 - restaurantId: $restaurantId, 댓글 개수: ${comments.size}")
                        trySend(CommentWrapperResponse(resultComments = comments)).isSuccess
                    }
                }

        awaitClose {
            Timber.i("getCommentList() 리스너 해제 - restaurantId: $restaurantId")
            listener.remove()
        }
    }
}