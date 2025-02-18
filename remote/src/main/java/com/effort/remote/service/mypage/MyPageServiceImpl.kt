package com.effort.remote.service.mypage

import android.net.Uri
import android.util.Log
import com.effort.remote.model.auth.UserResponse
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

class MyPageServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MyPageService {

    /**
     * Firestore에서 'faq' 컬렉션 데이터를 가져와 정렬 후 반환한다.
     * - FAQ 목록을 가져와 FaqWrapperResponse로 감싸서 반환
     */
    override suspend fun getFaqs(): FaqWrapperResponse {
        val snapshot = firestore.collection("faq")
            .get()
            .await()

        val faqList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val category = getString("category") ?: ""
                val question = getString("question") ?: ""
                val answer = getString("answer") ?: ""
                val timestamp = getTimestamp("timestamp").toString()
                FaqResponse(category, question, answer, timestamp)
            }
        }
        return FaqWrapperResponse(resultFaqs = faqList)
    }

    /**
     * Firestore에서 'notice' 컬렉션 데이터를 가져와 정렬 후 반환한다.
     * - 공지사항 목록을 가져와 NoticeWrapperResponse로 감싸서 반환
     */
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

    /**
     * Firestore에서 닉네임을 업데이트한다.
     * - 사용자 인증 상태 확인 후 닉네임 업데이트 실행
     */
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

    /**
     * 닉네임 중복 여부 확인한다.
     * - Firestore에서 동일한 닉네임이 존재하는지 실시간 감지
     */
    override fun checkNicknameDuplicated(nickname: String): Flow<Boolean> = callbackFlow {
        val listenerRegistration = firestore.collection("users")
            .whereEqualTo("nickname", nickname)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val isAvailable = snapshot?.documents.isNullOrEmpty()
                trySend(isAvailable) // 중복 여부 전송 (중복 존재 시 false, 없으면 true)
            }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * 사용자 프로필 사진 업데이트한다.
     * - Firebase Storage에 업로드 후 Firestore에 URL 저장
     */
    override suspend fun updateProfilePic(profilePicPath: String): Boolean {
        return try {
            Log.d("UpdateProfilePic", "profilePicPath: $profilePicPath")

            val currentUserEmail = auth.currentUser?.email
                ?: throw Exception("User not logged in")

            Log.d("UpdateProfilePic", "currentUserEmail: $currentUserEmail")

            val storageRef = storage.reference.child("profile_pictures/${currentUserEmail}.jpg")

            val uri = Uri.parse(profilePicPath)

            Log.d("UpdateProfilePic", "URI: $uri")

            storageRef.putFile(uri).await()

            val downloadUrl = storageRef.downloadUrl.await()

            Log.d("UpdateProfilePic", "downloadUrl: $downloadUrl")

            firestore.collection("users").document(currentUserEmail)
                .update("profilePicPath", downloadUrl.toString()).await()

            true
        } catch (e: Exception) {
            Log.e("UpdateProfilePic", "Error: ${e.message}", e)
            false
        }
    }

    /**
     * 사용자 정보 변경을 실시간으로 감지한다.
     * - Firestore에서 사용자 정보가 변경될 때마다 스트림으로 전송
     */
    override fun observeUserUpdate(): Flow<UserResponse> = callbackFlow {
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
                snapshot?.toObject(UserResponse::class.java)?.let {
                    trySend(it)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}