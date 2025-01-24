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

    override suspend fun addComment(restaurantId: String, comment: CommentResponse): Boolean {
        return try {
            firestore.collection("restaurants")
                .document(restaurantId)
                .collection("comments")
                .document(comment.userId) // 자동 생성 ID 사용
                .set(comment)
                .await() // 비동기 Firestore 작업

            Log.d("Debug", "Comment data: ${comment}")
            true
        } catch (e: Exception) {
            Log.e("CommentService", "Failed to add comment: ${e.message}", e)
            throw e // 예외를 재던지기
        }
    }

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
        awaitClose { listener.remove() } // Listener 해제
    }

}