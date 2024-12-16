package com.effort.remote.service

import com.effort.remote.model.mypage.detail.faq.FaqResponse
import com.effort.remote.model.mypage.detail.faq.FaqWrapperResponse
import com.effort.remote.model.mypage.detail.notice.NoticeResponse
import com.effort.remote.model.mypage.detail.notice.NoticeWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
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
            .get() // 데이터를 한 번 가져옴
            .await()

        // Firestore 데이터를 FaqResponse 리스트로 변환
        val faqList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val category = getString("category") ?: ""
                val question = getString("question") ?: ""
                val answer = getString("answer") ?: ""
                val timestamp = getTimestamp("timestamp").toString()
                FaqResponse(category, question, answer, timestamp)
            }
        }

        // FaqResponse 리스트를 FaqWrapperResponse로 감싸 반환
        return FaqWrapperResponse(resultFaqs = faqList)
    }

    override suspend fun getNotices(): NoticeWrapperResponse {
        val snapshot = firestore.collection("notice")
            .get()
            .await()

        val noticeList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val category = getString("category") ?: ""
                val title = getString("title") ?: ""
                val description = getString("description") ?: ""
                val imageUrl = getString("imageUrl") ?: ""
                val timestamp = getTimestamp("timestamp").toString()
                NoticeResponse(category, title, description, imageUrl, timestamp)
            }
        }

        return NoticeWrapperResponse(resultNotices = noticeList)
    }
}
