package com.effort.remote.service.mypage

import android.net.Uri
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
import timber.log.Timber
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
        return try {
            Timber.d("getFaqs() 호출")

            val snapshot = firestore.collection("faq").get().await()

            val faqList = snapshot.documents.mapNotNull { document ->
                with(document) {
                    val category = getString("category") ?: ""
                    val question = getString("question") ?: ""
                    val answer = getString("answer") ?: ""
                    val timestamp = getTimestamp("timestamp").toString()
                    FaqResponse(category, question, answer, timestamp)
                }
            }
            Timber.d("FAQ 데이터 ${faqList.size}개 가져옴")
            FaqWrapperResponse(resultFaqs = faqList)
        } catch (e: Exception) {
            Timber.e(e, "getFaqs() 실패")
            throw e
        }
    }

    /**
     * Firestore에서 'notice' 컬렉션 데이터를 가져와 정렬 후 반환한다.
     * - 공지사항 목록을 가져와 NoticeWrapperResponse로 감싸서 반환
     */
    override suspend fun getNotices(): NoticeWrapperResponse {
        return try {
            Timber.d("getNotices() 호출")

            val snapshot = firestore.collection("notice").get().await()

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
            Timber.d("공지사항 데이터 ${noticeList.size}개 가져옴")
            NoticeWrapperResponse(resultNotices = noticeList)
        } catch (e: Exception) {
            Timber.e(e, "getNotices() 실패")
            throw e
        }
    }

    /**
     * Firestore에서 닉네임을 업데이트한다.
     * - 사용자 인증 상태 확인 후 닉네임 업데이트 실행
     */
    override suspend fun updateNickname(nickname: String): Boolean {
        return try {
            val currentUserEmail = auth.currentUser?.email ?: throw Exception("User not logged in")

            Timber.d("updateNickname() 호출 - 이메일: $currentUserEmail, 변경할 닉네임: $nickname")

            val userDoc = firestore.collection("users").document(currentUserEmail)
            userDoc.update("nickname", nickname).await()

            Timber.d("닉네임 업데이트 성공: $nickname")
            true
        } catch (e: Exception) {
            Timber.e(e, "updateNickname() 실패")
            false
        }
    }

    /**
     * 닉네임 중복 여부 확인한다.
     * - Firestore에서 동일한 닉네임이 존재하는지 실시간 감지
     */
    override fun checkNicknameDuplicated(nickname: String): Flow<Boolean> = callbackFlow {
        Timber.d("checkNicknameDuplicated() 호출 - 닉네임: $nickname")

        val listenerRegistration = firestore.collection("users").whereEqualTo("nickname", nickname)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Timber.e(exception, "닉네임 중복 확인 실패")
                    close(exception)
                    return@addSnapshotListener
                }

                val isAvailable = snapshot?.documents.isNullOrEmpty()
                Timber.d("닉네임 사용 가능 여부: $isAvailable")
                trySend(isAvailable)
            }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * 사용자 프로필 사진 업데이트한다.
     * - Firebase Storage에 업로드 후 Firestore에 URL 저장
     */
    override suspend fun updateProfilePic(profilePicPath: String): Boolean {
        return try {
            val currentUserEmail = auth.currentUser?.email ?: throw Exception("User not logged in")

            Timber.d("updateProfilePic() 호출 - 사용자: $currentUserEmail, 경로: $profilePicPath")

            val storageRef = storage.reference.child("profile_pictures/${currentUserEmail}.jpg")
            val uri = Uri.parse(profilePicPath)

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            firestore.collection("users").document(currentUserEmail)
                .update("profilePicPath", downloadUrl.toString()).await()

            Timber.d("프로필 사진 업데이트 성공 - URL: $downloadUrl")
            true
        } catch (e: Exception) {
            Timber.e(e, "updateProfilePic() 실패")
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
            Timber.w("observeUserUpdate() 실패 - 이메일이 존재하지 않음")
            close(IllegalStateException("User email is null or empty"))
            return@callbackFlow
        }

        Timber.d("observeUserUpdate() 호출 - 이메일: $email")

        val listenerRegistration = firestore.collection("users").document(email)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Timber.e(exception, "사용자 정보 감지 실패")
                    close(exception)
                    return@addSnapshotListener
                }
                snapshot?.toObject(UserResponse::class.java)?.let {
                    Timber.d("사용자 정보 업데이트 감지 - ${it.nickname}")
                    trySend(it)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}