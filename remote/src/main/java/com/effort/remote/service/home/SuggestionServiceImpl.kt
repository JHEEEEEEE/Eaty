package com.effort.remote.service.home

import com.effort.remote.model.home.suggestion.KeywordResponse
import com.effort.remote.model.home.suggestion.KeywordWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class SuggestionServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SuggestionService {

    /**
     * Firestore에서 검색어 자동완성 데이터 조회
     * - 검색어(query)로 시작하는 키워드를 가져옴
     * - Firestore `whereGreaterThanOrEqualTo` 및 `whereLessThanOrEqualTo` 필터 사용
     * - 실패 시 Flow를 종료하고 예외 처리
     */
    override fun getSuggestions(query: String): Flow<KeywordWrapperResponse> = callbackFlow {
        try {
            Timber.i("getSuggestions() 호출 - query: $query")

            firestore.collection("keywords")
                .whereGreaterThanOrEqualTo("keyword", query) // 검색어(query)와 같거나 큰 값 필터
                .whereLessThanOrEqualTo("keyword", query + "\uf8ff") // 검색어(query)로 시작하는 값 필터
                .get().addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        // 가져온 데이터가 없을 때 로그 출력
                        Timber.d("검색 결과 없음 - query: $query")
                    } else {
                        // Firestore에서 성공적으로 데이터를 가져왔을 때 로그 출력
                        val keywordResponses = snapshot.documents.mapNotNull { document ->
                            document.getString("keyword")?.let { keyword ->
                                Timber.d("가져온 키워드: $keyword")
                                KeywordResponse(keyword = keyword) // KeywordResponse 객체 생성
                            }
                        }
                        Timber.i("검색어 자동완성 조회 성공 - query: $query, 키워드 개수: ${keywordResponses.size}")
                        trySend(KeywordWrapperResponse(keywordResponses)) // 데이터를 Flow로 전달
                    }
                }.addOnFailureListener { exception ->
                    // Firestore에서 데이터를 가져오지 못했을 때 로그 출력
                    Timber.e(exception, "getSuggestions() 실패 - query: $query")
                    close(exception) // 오류를 발생시키고 Flow를 종료
                }
        } catch (e: Exception) {
            Timber.e(e, "getSuggestions() 오류 발생 - query: $query")
            close(e)
        }

        awaitClose()
    }
}