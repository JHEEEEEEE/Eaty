package com.effort.remote.service.mypage

import com.effort.remote.model.auth.UserResponse
import com.effort.remote.model.mypage.detail.faq.FaqWrapperResponse
import com.effort.remote.model.mypage.detail.notice.NoticeWrapperResponse
import kotlinx.coroutines.flow.Flow

// Firebase와의 통신을 추상화한 인터페이스. 데이터를 직접 노출하지 않고 감싸서 제공, FirebaseServiceImpl에서 구현.
interface FirebaseService {

    suspend fun getFaqs(): FaqWrapperResponse

    suspend fun getNotices(): NoticeWrapperResponse

    suspend fun updateNickname(nickname: String): Boolean

    fun checkNicknameDuplicated(nickname: String): Flow<Boolean>

    suspend fun updateProfilePic(profilePicPath: String): Boolean

    fun observeUserUpdate(): Flow<UserResponse>
}