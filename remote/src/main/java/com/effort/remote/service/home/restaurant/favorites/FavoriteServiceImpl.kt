package com.effort.remote.service.home.restaurant.favorites

import android.util.Log
import com.effort.remote.model.home.restaurant.RestaurantResponse
import com.effort.remote.model.home.restaurant.favorites.RestaurantFavoritesResponse
import com.effort.remote.model.home.restaurant.favorites.RestaurantFavoritesWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FavoriteService {

    override suspend fun addRestaurantToFavorites(
        userId: String,
        restaurant: RestaurantResponse
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurant.title)

            // Firestore에서 중복 확인
            val documentSnapshot = userDocRef.get().await()
            if (documentSnapshot.exists()) {
                Log.d(
                    "FavoriteServiceImpl",
                    "Restaurant '${restaurant.title}' already exists for user: $userId"
                )
                return false // 중복된 데이터가 이미 존재
            }

            // Firestore에 저장
            userDocRef.set(restaurant).await()
            Log.d(
                "FavoriteServiceImpl",
                "Restaurant '${restaurant.title}' successfully added for user: $userId"
            )
            true
        } catch (e: Exception) {
            Log.e(
                "FavoriteServiceImpl",
                "Failed to add restaurant '${restaurant.title}' for user: $userId",
                e
            )
            false
        }
    }

    override suspend fun removeRestaurantFromFavorites(
        userId: String,
        restaurantName: String
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurantName)

            // Firestore에서 문서 삭제
            userDocRef.delete().await()
            Log.d(
                "FavoriteServiceImpl",
                "Restaurant '$restaurantName' successfully removed for user: $userId"
            )
            true
        } catch (e: Exception) {
            Log.e(
                "FavoriteServiceImpl",
                "Failed to remove restaurant '$restaurantName' for user: $userId",
                e
            )
            false
        }
    }

    override suspend fun checkIfRestaurantIsFavorite(
        userId: String,
        restaurantName: String
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurantName)
                .get() // Firestore 문서 가져오기
                .await() // 비동기 작업 대기

            val exists = userDocRef.exists()
            Log.d(
                "FavoriteServiceImpl",
                "Restaurant '$restaurantName' exists for user: $userId: $exists"
            )
            exists // 문서 존재 여부 반환
        } catch (e: Exception) {
            Log.e(
                "FavoriteServiceImpl",
                "Failed to check restaurant '$restaurantName' for user: $userId",
                e
            )
            false
        }
    }

    override suspend fun getFavorites(userId: String): RestaurantFavoritesWrapperResponse {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("restaurants")
            .get()
            .await()

        // Firestore 데이터를 FaqResponse 리스트로 변환
        val favoriteList = snapshot.documents.mapNotNull { document ->
            with(document) {
                val title = getString("title") ?: ""
                val lotNumberAddress = getString("lotNumberAddress") ?: ""
                val roadNameAddress = getString("roadNameAddress") ?: ""
                val phoneNumber = getString("phoneNumber") ?: ""
                val placeUrl = getString("placeUrl") ?: ""
                val distance = getString("distance") ?: ""
                val longitude = getString("longitude") ?: ""
                val latitude = getString("latitude") ?: ""
                RestaurantFavoritesResponse(
                    title,
                    lotNumberAddress,
                    roadNameAddress,
                    phoneNumber,
                    placeUrl,
                    distance,
                    longitude,
                    latitude
                )
            }
        }
        return (RestaurantFavoritesWrapperResponse(resultFavorites = favoriteList))
    }
}