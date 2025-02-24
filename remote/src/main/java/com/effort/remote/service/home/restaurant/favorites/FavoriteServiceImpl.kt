package com.effort.remote.service.home.restaurant.favorites

import com.effort.remote.model.home.restaurant.RestaurantResponse
import com.effort.remote.model.home.restaurant.favorites.RestaurantFavoritesResponse
import com.effort.remote.model.home.restaurant.favorites.RestaurantFavoritesWrapperResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
        userId: String, restaurant: RestaurantResponse
    ): Boolean {
        return try {
            Timber.i("addRestaurantToFavorites() 호출 - userId: $userId, restaurant: ${restaurant.title}")

            val userDocRef =
                firestore.collection("users").document(userId).collection("restaurants")
                    .document(restaurant.title)

            val documentSnapshot = userDocRef.get().await()
            if (documentSnapshot.exists()) {
                Timber.d("즐겨찾기 추가 불필요 - 이미 존재하는 레스토랑: ${restaurant.title}, userId: $userId")
                return false
            }

            userDocRef.set(restaurant).await()
            Timber.i("즐겨찾기 추가 성공 - restaurant: ${restaurant.title}, userId: $userId")
            true
        } catch (e: Exception) {
            Timber.e(
                e,
                "addRestaurantToFavorites() 실패 - restaurant: ${restaurant.title}, userId: $userId"
            )
            false
        }
    }

    /**
     * 사용자의 즐겨찾기에서 레스토랑 제거한다.
     * - Firestore에서 특정 레스토랑 문서 삭제
     */
    override suspend fun removeRestaurantFromFavorites(
        userId: String, restaurantName: String
    ): Boolean {
        return try {
            Timber.i("removeRestaurantFromFavorites() 호출 - userId: $userId, restaurant: $restaurantName")

            val userDocRef =
                firestore.collection("users").document(userId).collection("restaurants")
                    .document(restaurantName)

            userDocRef.delete().await()
            Timber.i("즐겨찾기 삭제 성공 - restaurant: $restaurantName, userId: $userId")
            true
        } catch (e: Exception) {
            Timber.e(
                e,
                "removeRestaurantFromFavorites() 실패 - restaurant: $restaurantName, userId: $userId"
            )
            false
        }
    }

    /**
     * 사용자의 즐겨찾기 목록에서 특정 레스토랑이 존재하는지 확인한다.
     * - Firestore에서 문서 존재 여부 확인 후 반환
     */
    override suspend fun checkIfRestaurantIsFavorite(
        userId: String, restaurantName: String
    ): Boolean {
        return try {
            Timber.i("checkIfRestaurantIsFavorite() 호출 - userId: $userId, restaurant: $restaurantName")

            val userDocRef =
                firestore.collection("users").document(userId).collection("restaurants")
                    .document(restaurantName).get().await()

            val exists = userDocRef.exists()
            Timber.d("즐겨찾기 존재 여부 - restaurant: $restaurantName, userId: $userId, exists: $exists")
            exists
        } catch (e: Exception) {
            Timber.e(
                e, "checkIfRestaurantIsFavorite() 실패 - restaurant: $restaurantName, userId: $userId"
            )
            false
        }
    }

    /**
     * 사용자의 즐겨찾기 목록을 Firestore에서 가져와 반환한다.
     * - Firestore 데이터 -> RestaurantFavoritesResponse 리스트 변환
     */
    override suspend fun getFavorites(userId: String): RestaurantFavoritesWrapperResponse {
        return try {
            Timber.i("getFavorites() 호출 - userId: $userId")

            val snapshot =
                firestore.collection("users").document(userId).collection("restaurants").get()
                    .await()

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

            Timber.i("즐겨찾기 목록 조회 성공 - userId: $userId, 총 개수: ${favoriteList.size}")
            RestaurantFavoritesWrapperResponse(resultFavorites = favoriteList)
        } catch (e: Exception) {
            Timber.e(e, "getFavorites() 실패 - userId: $userId")
            RestaurantFavoritesWrapperResponse(resultFavorites = emptyList())
        }
    }
}