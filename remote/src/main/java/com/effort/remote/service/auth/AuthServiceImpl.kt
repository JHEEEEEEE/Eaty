package com.effort.remote.service.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthService {

    override suspend fun authenticateUser(idToken: String): Boolean {
        return try {
            // 1. Firebase 인증
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            // 2. Firebase 인증 결과 가져오기
            val firebaseUser = result.user ?: throw Exception("Failed to authenticate user")

            // 3. Firestore에서 사용자 정보 확인 및 저장 로직 호출
            val email = firebaseUser.email ?: throw Exception("Email is null")
            saveUser(
                email = email,
                name = firebaseUser.displayName ?: "Unknown",
                profilePicPath = firebaseUser.photoUrl?.toString() ?: ""
            )

            // 4. 인증 성공 시 true 반환
            true
        } catch (e: Exception) {
            // 인증 실패 시 false 반환
            false
        }
    }

    override suspend fun checkUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    private suspend fun saveUser(email: String, name: String, profilePicPath: String) {
        try {
            // 1. Firestore에서 사용자 문서 확인
            val userDocument = firestore.collection("users").document(email).get().await()

            if (!userDocument.exists()) {
                // 2. 사용자 정보가 존재하지 않으면 Firestore에 저장
                val userData = mapOf(
                    "name" to name,
                    "nickname" to "",
                    "email" to email,
                    "profilePicPath" to profilePicPath
                )
                firestore.collection("users").document(email).set(userData).await()
            }
            // 사용자 정보가 이미 존재하면 아무 작업도 하지 않음 (자동 로그인 처리)
        } catch (e: Exception) {
            throw Exception("Failed to save user: ${e.message}", e)
        }
    }
}
