package com.effort.remote.service

import android.util.Log
import com.effort.remote.model.FaqResponse
import com.effort.remote.model.FaqWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


// Firebase에서 데이터를 가져오는 구현체. FirebaseServiceImpl은 FirebaseService 인터페이스를 구현
// Firestore에서 데이터를 가져오고 FaqWrapperResponse로 감싸 반환
class FirebaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirebaseService {

    // Firestore에서 'faq' 컬렉션 데이터를 가져와 정렬 후 FaqWrapperResponse로 반환
    override suspend fun getFaqs(): FaqWrapperResponse {
        val snapshot = firestore.collection("faq")
            //.orderBy("timestamp", Query.Direction.DESCENDING) domain에서 비즈니스 로직처리 여기에서는 그냥 데이터만 가져온다
            .get() // 데이터를 한 번 가져옴
            .addOnSuccessListener { snapshot -> Log.d("Firestoresuc", "Data: ${snapshot.documents}") }
            .addOnFailureListener { exception -> Log.d("Firestorefail", "Error: ${exception.message}")}
            .await() // Coroutine 지원을 위해 await() 사용

        /*// Firestore 데이터를 FaqResponse 리스트로 변환
        val faqList = snapshot.documents.mapNotNull { document ->
            document.toObject(FaqResponse::class.java)
        }*/

        val faqList = snapshot.documents.mapNotNull { document ->
            val category = document.getString("category") ?: ""
            val question = document.getString("question") ?: ""
            val answer = document.getString("answer") ?: ""
            val timestamp = document.getTimestamp("timestamp").toString()
            FaqResponse(category, question, answer, timestamp)
        }

        // FaqResponse 리스트를 FaqWrapperResponse로 감싸 반환
        return FaqWrapperResponse(summaryFaqs = faqList)
    }
}
