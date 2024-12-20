package com.effort.remote.service.mypage

import android.net.Uri
import android.util.Log
import com.effort.remote.model.auth.FirebaseUserResponse
import com.effort.remote.model.mypage.detail.faq.FaqResponse
import com.effort.remote.model.mypage.detail.faq.FaqWrapperResponse
import com.effort.remote.model.mypage.detail.notice.NoticeResponse
import com.effort.remote.model.mypage.detail.notice.NoticeWrapperResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


// Firebase에서 데이터를 가져오는 구현체. FirebaseServiceImpl은 FirebaseService 인터페이스를 구현
// Firestore에서 데이터를 가져오고 FaqWrapperResponse로 감싸 반환
class FirebaseServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FirebaseService {

    // Firestore에서 'faq' 컬렉션 데이터를 가져와 정렬 후 FaqWrapperResponse로 반환
    override suspend fun getFaqs(): FaqWrapperResponse {
        val snapshot = firestore.collection("faq")
            .get() // 데이터를 한 번 가져옴
            .await()

        // Firestore 데이터를 FaqResponse 리스트로 변환
        val faqList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val category = getString("category") ?: ""
                val question = getString("question") ?: ""
                val answer = getString("answer") ?: ""
                val timestamp = getTimestamp("timestamp").toString()
                FaqResponse(category, question, answer, timestamp)
            }
        }

        // FaqResponse 리스트를 FaqWrapperResponse로 감싸 반환
        return FaqWrapperResponse(resultFaqs = faqList)
    }

    override suspend fun getNotices(): NoticeWrapperResponse {
        val snapshot = firestore.collection("notice")
            .get()
            .await()

        val noticeList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val category = getString("category") ?: ""
                val title = getString("title") ?: ""
                val description = getString("description") ?: ""
                val imageUrl = getString("imageUrl") ?: ""
                val timestamp = getTimestamp("timestamp").toString()
                NoticeResponse(category, title, description, imageUrl, timestamp)
            }
        }

        return NoticeWrapperResponse(resultNotices = noticeList)
    }

    override suspend fun updateNickname(nickname: String): Boolean {
        return try {
            val currentUserEmail =
                auth.currentUser?.email ?: throw Exception("User not logged in")

            val userDoc = firestore.collection("users").document(currentUserEmail)
            userDoc.update("nickname", nickname).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    override fun checkNicknameDuplicated(nickname: String): Flow<Boolean> = callbackFlow {

        val listenerRegistration = firestore.collection("users")
            .whereEqualTo("nickname", nickname)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // 예외 발생 시 스트림 종료
                    return@addSnapshotListener
                }

                val isAvailable = snapshot?.documents.isNullOrEmpty()
                trySend(isAvailable) // 중복 여부를 스트림으로 전송 -> 중복 있으면 false, 없으면 true
            }

        awaitClose {
            listenerRegistration.remove() // 리스너 해제
        }
    }

    override suspend fun updateProfilePic(profilePicPath: String): Boolean {
        return try {
            // Log profilePicPath to check its value
            Log.d("UpdateProfilePic", "profilePicPath: $profilePicPath")

            val currentUserEmail = auth.currentUser?.email
                ?: throw Exception("User not logged in")

            // Log email to ensure current user email is fetched correctly
            Log.d("UpdateProfilePic", "currentUserEmail: $currentUserEmail")

            val storageRef = storage.reference.child("profile_pictures/${currentUserEmail}.jpg")

            val uri = Uri.parse(profilePicPath)

            // Log URI to verify it's parsed correctly
            Log.d("UpdateProfilePic", "URI: $uri")

            storageRef.putFile(uri).await()

            val downloadUrl = storageRef.downloadUrl.await()

            // Log download URL after upload
            Log.d("UpdateProfilePic", "downloadUrl: $downloadUrl")

            firestore.collection("users").document(currentUserEmail)
                .update("profilePicPath", downloadUrl.toString()).await()

            true
        } catch (e: Exception) {
            // Log exception message for debugging
            Log.e("UpdateProfilePic", "Error: ${e.message}", e)
            false
        }
    }


    override fun observeUserUpdate(): Flow<FirebaseUserResponse> = callbackFlow {
        val email = auth.currentUser?.email.orEmpty()
        if (email.isEmpty()) {
            close(IllegalStateException("User email is null or empty")) // 이메일이 없으면 예외 처리
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("users")
            .document(email)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                snapshot?.toObject(FirebaseUserResponse::class.java)?.let {
                    trySend(it)
                }
            }
        awaitClose { listenerRegistration.remove() } // 리스너 해제
    }
}
