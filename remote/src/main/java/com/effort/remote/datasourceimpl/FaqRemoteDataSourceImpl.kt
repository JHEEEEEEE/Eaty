package com.effort.remote.datasourceimpl

import com.effort.data.datasource.FaqRemoteDataSource
import com.effort.data.model.FaqEntity
import com.effort.remote.service.FirebaseService
import javax.inject.Inject

// Remote Layer에서 FirebaseService를 사용해 데이터를 처리하는 구현체
// Data Layer의 FaqRemoteDataSource 인터페이스를 구현하며, 데이터를 Domain 계층으로 전달
class FaqRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
): FaqRemoteDataSource{

    // FirebaseService에서 FaqWrapperResponse 데이터를 가져오고
    // 각 FaqResponse를 toData() 메서드를 통해 FaqEntity로 변환하여 반환
    override suspend fun getFaqs(): List<FaqEntity> {
        return firebaseService.getFaqs().resultFaqs.map { it.toData() }
    }

}