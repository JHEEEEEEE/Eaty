package com.effort.remote.service

import com.effort.remote.model.FaqWrapperResponse

// Firebase와의 통신을 추상화한 인터페이스. 데이터를 직접 노출하지 않고 감싸서 제공, FirebaseServiceImpl에서 구현.
interface FirebaseService {

    suspend fun getFaqs(): FaqWrapperResponse
}