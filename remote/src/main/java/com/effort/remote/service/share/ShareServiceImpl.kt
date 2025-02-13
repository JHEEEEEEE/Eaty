package com.effort.remote.service.share

import android.content.Context
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.LocationTemplate
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class ShareServiceImpl @Inject constructor(
    private val context: Context
) : ShareService {
    override suspend fun createKakaoShareUrl(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): String {
        val locationTemplate = LocationTemplate(
            address = lotNumberAddress,
            content = Content(
                title = title,
                description = "위치를 확인하려면 클릭하세요!",
                link = Link(
                    androidExecutionParams = mapOf(
                        "title" to title,
                        "lotNumberAddress" to lotNumberAddress,
                        "roadNameAddress" to roadNameAddress,
                        "distance" to distance,
                        "phoneNumber" to phoneNumber,
                        "placeUrl" to placeUrl
                    ),
                    mobileWebUrl = "recycrew://recycrew.com/detail?title=$title"
                )
            )
        )

        return if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            suspendCancellableCoroutine { continuation ->
                ShareClient.instance.shareDefault(context, locationTemplate) { sharingResult, error ->
                    if (error != null) {
                        continuation.resume("공유 실패: ${error.message}")
                    } else {
                        continuation.resume(sharingResult?.intent?.data.toString())
                    }
                }
            }
        } else {
            WebSharerClient.instance.makeDefaultUrl(locationTemplate).toString()
        }
    }
}