package com.effort.remote.service.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : AuthService {

    /**
     * 사용자 인증을 수행하고 Firebase에 저장한다.
     * - Google ID 토큰을 사용하여 Firebase Authentication 진행
     * - 성공 시 사용자의 이메일을 기반으로 Firestore에 저장
     * - 사용자 정보가 Firestore에 없을 경우 새로 생성
     * - 예외 발생 시 로그를 남기고 실패 처리
     */
    override suspend fun authenticateUser(idToken: String): Boolean {
        return try {
            Timber.i("authenticateUser() 시작")

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            val firebaseUser = result.user ?: throw Exception("Firebase 인증 실패: 사용자 정보 없음")
            val email = firebaseUser.email ?: throw Exception("Firebase 인증 성공했지만 이메일 없음")

            Timber.i("Firebase 인증 성공 - 이메일: $email")

            saveUser(
                email = email,
                name = firebaseUser.displayName ?: "Unknown",
                profilePicPath = firebaseUser.photoUrl?.toString() ?: ""
            )

            Timber.i("사용자 Firestore 저장 완료: $email")
            true
        } catch (e: Exception) {
            Timber.e(e, "authenticateUser() 실패")
            false
        }
    }

    /**
     * 현재 로그인 상태 확인한다.
     * - Firebase Authentication을 사용하여 현재 사용자가 존재하는지 검사
     */
    override suspend fun checkUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Timber.d("checkUserLoggedIn(): $isLoggedIn")
        return isLoggedIn
    }

    /**
     * 사용자 정보를 Firestore에 저장한다.
     * - 이메일을 기반으로 Firestore에서 사용자 정보 조회
     * - 사용자가 존재하지 않으면 새로 저장
     * - 기존 사용자는 업데이트하지 않고 그대로 유지
     */
    private suspend fun saveUser(email: String, name: String, profilePicPath: String) {
        try {
            Timber.i("saveUser() 실행 - 이메일: $email")

            val userDocument = firestore.collection("users").document(email).get().await()

            if (!userDocument.exists()) {
                val userData = mapOf(
                    "name" to name,
                    "nickname" to "",
                    "email" to email,
                    "profilePicPath" to profilePicPath
                )
                firestore.collection("users").document(email).set(userData).await()
                Timber.i("새 사용자 저장 완료: $email")
            } else {
                Timber.d("사용자 정보 존재 - 저장 불필요: $email")
            }
        } catch (e: Exception) {
            Timber.e(e, "saveUser() 실패 - 이메일: $email")
            throw Exception("Firestore 사용자 저장 실패", e)
        }
    }

    /**
     * 사용자를 로그아웃 처리한다.
     * - Firebase Authentication을 통해 로그아웃 수행
     * - 예외 발생 시 로그를 출력하고 실패 반환
     */
    override suspend fun signOut(): Boolean {
        return try {
            Timber.i("signOut() 실행")
            auth.signOut()
            Timber.i("signOut() 성공")
            true
        } catch (e: Exception) {
            Timber.e(e, "signOut() 실패")
            false
        }
    }
}