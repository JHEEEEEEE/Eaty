package com.effort.remote.service.share

import android.content.Context
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.LocationTemplate
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class ShareServiceImpl @Inject constructor(
    private val context: Context
) : ShareService {

    /**
     * 카카오톡 공유 URL 생성
     * - 위치 정보를 기반으로 LocationTemplate을 생성하여 공유 URL을 반환
     * - 카카오톡이 설치되어 있으면 `ShareClient`를 사용, 없으면 `WebSharerClient`를 사용
     */
    override suspend fun createKakaoShareUrl(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): String {
        Timber.d("createKakaoShareUrl() 호출 - title: $title, address: $lotNumberAddress")

        val locationTemplate = LocationTemplate(
            address = lotNumberAddress, content = Content(
                title = title, description = "위치를 확인하려면 클릭하세요!", link = Link(
                    androidExecutionParams = mapOf(
                        "title" to title,
                        "lotNumberAddress" to lotNumberAddress,
                        "roadNameAddress" to roadNameAddress,
                        "distance" to distance,
                        "phoneNumber" to phoneNumber,
                        "placeUrl" to placeUrl
                    ), mobileWebUrl = "recycrew://recycrew.com/detail?title=$title"
                )
            )
        )

        return if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            /**
             * 1. 카카오톡 앱이 설치된 경우
             * - `ShareClient.instance.shareDefault()`를 사용하여 공유 URL 생성
             * - 공유 실패 시 예외 메시지 반환
             */
            Timber.d("카카오톡 설치 확인됨 - 앱을 통해 공유 진행")

            suspendCancellableCoroutine { continuation ->
                ShareClient.instance.shareDefault(
                    context, locationTemplate
                ) { sharingResult, error ->
                    if (error != null) {
                        Timber.e(error, "카카오톡 공유 실패")
                        continuation.resume("공유 실패: ${error.message}")
                    } else {
                        val shareUrl = sharingResult?.intent?.data.toString()
                        Timber.d("카카오톡 공유 성공 - URL: $shareUrl")
                        continuation.resume(shareUrl)
                    }
                }
            }
        } else {
            /**
             * 2. 카카오톡이 설치되지 않은 경우
             * - `WebSharerClient.instance.makeDefaultUrl()`을 사용하여 웹 공유 URL 생성
             */
            Timber.w("카카오톡 미설치 - 웹 공유 URL 생성")
            val webShareUrl = WebSharerClient.instance.makeDefaultUrl(locationTemplate).toString()
            Timber.d("웹 공유 URL: $webShareUrl")
            webShareUrl
        }
    }
}