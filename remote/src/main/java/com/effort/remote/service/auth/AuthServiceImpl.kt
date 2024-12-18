package com.effort.remote.service.auth

import com.effort.data.model.auth.FirebaseUserEntity
import com.effort.remote.model.auth.FirebaseUserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthService {

    override suspend fun authenticateUser(idToken: String): FirebaseUserResponse {
        // 1. Firebase 인증
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()

        // 2. Firebase 인증 결과 가져오기
        val firebaseUser = result.user ?: throw Exception("Failed to authenticate user")

        // 3. Firestore에서 nickname 가져오기
        val email = firebaseUser.email ?: throw Exception("Email is null")
        val userDocument = firestore.collection("users").document(email).get().await()

        //4. Firestore에서 가져온 문서의 nickname 필드 값을 가져와 nickname 변수에 할당
        val nickname = userDocument.getString("nickname")
            // nickname이 null이거나 공백이면 null 반환, 그렇지 않으면 그대로 사용
            .takeIf { !it.isNullOrBlank() }
            // nickname이 null 또는 공백일 경우 name 필드 값을 가져옴
            ?: userDocument.getString("name")
            // name 필드도 null이면 빈 문자열("")을 할당
            ?: ""

        return FirebaseUserResponse(
            name = firebaseUser.displayName ?: "",
            email = email,
            profilePicUrl = firebaseUser.photoUrl?.toString() ?: "",
            nickname = nickname
        )
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