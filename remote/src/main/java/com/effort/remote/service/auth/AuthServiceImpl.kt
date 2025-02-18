package com.effort.remote.service.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
            Log.d("AuthServiceImpl", "authenticateUser() 호출")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            val firebaseUser = result.user ?: throw Exception("Failed to authenticate user")
            Log.d("AuthServiceImpl", "Firebase 인증 성공: ${firebaseUser.email}")

            val email = firebaseUser.email ?: throw Exception("Email is null")
            saveUser(
                email = email,
                name = firebaseUser.displayName ?: "Unknown",
                profilePicPath = firebaseUser.photoUrl?.toString() ?: ""
            )

            Log.d("AuthServiceImpl", "사용자 저장 완료: $email")
            true
        } catch (e: Exception) {
            Log.e("AuthServiceImpl", "authenticateUser() 실패: ${e.message}")
            false
        }
    }

    /**
     * 현재 로그인 상태 확인한다.
     * - Firebase Authentication을 사용하여 현재 사용자가 존재하는지 검사
     */
    override suspend fun checkUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d("AuthServiceImpl", "checkUserLoggedIn(): $isLoggedIn")
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
            Log.d("AuthServiceImpl", "saveUser() 호출: $email")
            val userDocument = firestore.collection("users").document(email).get().await()

            if (!userDocument.exists()) {
                val userData = mapOf(
                    "name" to name,
                    "nickname" to "",
                    "email" to email,
                    "profilePicPath" to profilePicPath
                )
                firestore.collection("users").document(email).set(userData).await()
                Log.d("AuthServiceImpl", "새 사용자 저장 완료: $email")
            } else {
                Log.d("AuthServiceImpl", "사용자 정보 존재: $email")
            }
        } catch (e: Exception) {
            Log.e("AuthServiceImpl", "saveUser() 실패: ${e.message}")
            throw Exception("Failed to save user: ${e.message}", e)
        }
    }

    /**
     * 사용자를 로그아웃 처리한다.
     * - Firebase Authentication을 통해 로그아웃 수행
     * - 예외 발생 시 로그를 출력하고 실패 반환
     */
    override suspend fun signOut(): Boolean {
        return try {
            Log.d("AuthServiceImpl", "signOut() 호출")
            auth.signOut()
            Log.d("AuthServiceImpl", "signOut() 성공")
            true
        } catch (e: Exception) {
            Log.e("AuthServiceImpl", "signOut() 실패: ${e.message}")
            false
        }
    }
}