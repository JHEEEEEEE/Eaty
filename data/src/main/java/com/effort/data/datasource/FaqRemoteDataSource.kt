package com.effort.data.datasource

import com.effort.data.model.FaqEntity

interface FaqRemoteDataSource {

    suspend fun getFaqs(): List<FaqEntity>
}