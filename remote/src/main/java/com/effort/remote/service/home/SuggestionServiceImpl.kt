package com.effort.remote.service.home

import android.util.Log
import com.effort.remote.model.home.suggestion.KeywordResponse
import com.effort.remote.model.home.suggestion.KeywordWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SuggestionServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SuggestionService {

    override fun getSuggestions(query: String): Flow<KeywordWrapperResponse> = callbackFlow {
        try {
            // Firestore에서 "keywords" 컬렉션 조회
            firestore.collection("keywords")
                .whereGreaterThanOrEqualTo("keyword", query) // 검색어(query)와 같거나 큰 값 필터
                .whereLessThanOrEqualTo("keyword", query + "\uf8ff") // 검색어(query)로 시작하는 값 필터
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        // 가져온 데이터가 없을 때 로그 출력
                        Log.d("SuggestionServiceImpl", "No data found for query: $query")
                    } else {
                        // Firestore에서 성공적으로 데이터를 가져왔을 때 로그 출력
                        val keywordResponses = snapshot.documents.mapNotNull { document ->
                            document.getString("keyword")?.let { keyword ->
                                Log.d("SuggestionServiceImpl", "Fetched keyword: $keyword") // 키워드 로그 출력
                                KeywordResponse(keyword = keyword) // KeywordResponse 객체 생성
                            }
                        }
                        Log.d(
                            "SuggestionServiceImpl",
                            "Fetched ${keywordResponses.size} keywords for query: $query"
                        )
                        trySend(KeywordWrapperResponse(keywordResponses)) // 데이터를 Flow로 전달
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore에서 데이터를 가져오지 못했을 때 로그 출력
                    Log.e("SuggestionServiceImpl", "Failed to fetch suggestions", exception)
                    close(exception) // 오류를 발생시키고 Flow를 종료
                }
        } catch (e: Exception) {
            // 예외 발생 시 로그 출력
            Log.e("SuggestionServiceImpl", "Error in getSuggestions", e)
            close(e)
        }

        // Flow가 종료될 때 호출
        awaitClose()
    }
}