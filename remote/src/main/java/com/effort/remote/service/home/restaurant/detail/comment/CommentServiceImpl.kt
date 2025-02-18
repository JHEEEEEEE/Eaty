package com.effort.remote.service.home.restaurant.detail.comment

import android.util.Log
import com.effort.remote.model.home.restaurant.detail.comment.CommentResponse
import com.effort.remote.model.home.restaurant.detail.comment.CommentWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
            firestore.collection("restaurants")
                .document(restaurantId)
                .collection("comments")
                .document(comment.userId)
                .set(comment)
                .await()

            true
        } catch (e: Exception) {
            Log.e("CommentService", "Failed to add comment: ${e.message}", e)
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
        val listener = firestore.collection("restaurants")
            .document(restaurantId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(CommentWrapperResponse(resultComments = emptyList())).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull {
                        it.toObject(CommentResponse::class.java)
                    }
                    trySend(CommentWrapperResponse(resultComments = comments)).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }
}
