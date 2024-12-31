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

    override suspend fun checkUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d("AuthServiceImpl", "checkUserLoggedIn(): $isLoggedIn")
        return isLoggedIn
    }

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
