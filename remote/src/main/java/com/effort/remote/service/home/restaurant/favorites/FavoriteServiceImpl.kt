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

    /**
     * 사용자의 즐겨찾기에 레스토랑 추가한다.
     * - Firestore에서 중복 확인 후 저장
     * - 동일한 레스토랑이 이미 존재하면 추가하지 않음
     */
    override suspend fun addRestaurantToFavorites(
        userId: String,
        restaurant: RestaurantResponse
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurant.title)

            val documentSnapshot = userDocRef.get().await()
            if (documentSnapshot.exists()) {
                Log.d(
                    "FavoriteServiceImpl",
                    "Restaurant '${restaurant.title}' already exists for user: $userId"
                )
                return false
            }

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

    /**
     * 사용자의 즐겨찾기에서 레스토랑 제거한다.
     * - Firestore에서 특정 레스토랑 문서 삭제
     */
    override suspend fun removeRestaurantFromFavorites(
        userId: String,
        restaurantName: String
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurantName)

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

    /**
     * 사용자의 즐겨찾기 목록에서 특정 레스토랑이 존재하는지 확인한다.
     * - Firestore에서 문서 존재 여부 확인 후 반환
     */
    override suspend fun checkIfRestaurantIsFavorite(
        userId: String,
        restaurantName: String
    ): Boolean {
        return try {
            val userDocRef = firestore.collection("users")
                .document(userId)
                .collection("restaurants")
                .document(restaurantName)
                .get()
                .await()

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

    /**
     * 사용자의 즐겨찾기 목록을 Firestore에서 가져와 반환한다.
     * - Firestore 데이터 -> RestaurantFavoritesResponse 리스트 변환
     */
    override suspend fun getFavorites(userId: String): RestaurantFavoritesWrapperResponse {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("restaurants")
            .get()
            .await()

        // Firestore 데이터를 RestaurantFavoritesResponse 리스트로 변환
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