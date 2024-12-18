package com.effort.remote.service.auth

import com.effort.data.model.auth.FirebaseUserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthService {

    override suspend fun authenticateUser(idToken: String): Boolean {
        return try {
            // 1. Firebase 인증
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            // 2. Firebase 인증 결과 가져오기
            val firebaseUser = result.user ?: throw Exception("Failed to authenticate user")

            // 3. Firestore에서 사용자 정보 확인
            val email = firebaseUser.email ?: throw Exception("Email is null")
            firestore.collection("users").document(email).get().await()

            // 4. 인증 성공 시 true 반환
            true
        } catch (e: Exception) {
            // 인증 실패 시 false 반환
            false
        }
    }

    override suspend fun saveUser(user: FirebaseUserEntity) {
        val userData = mapOf(
            "name" to user.name,
            "nickname" to user.nickname,
            "email" to user.email,
            "profilePicUrl" to user.profilePicUrl
        )
        firestore.collection("users").document(user.email).set(userData).await()
    }

}