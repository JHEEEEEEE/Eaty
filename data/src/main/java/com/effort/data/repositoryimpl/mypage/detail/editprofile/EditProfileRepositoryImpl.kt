package com.effort.data.repositoryimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class EditProfileRepositoryImpl @Inject constructor(
    private val editProfileRemoteDataSource: EditProfileRemoteDataSource
) : EditProfileRepository {

    /**
     * 닉네임을 업데이트한다.
     * - 원격 데이터 소스를 호출하여 닉네임을 변경
     * - 성공 여부를 반환하며, 실패 시 로그를 출력
     */
    override suspend fun updateNickname(nickname: String): DataResource<Boolean> {
        Timber.d("updateNickname() 호출 - 닉네임: $nickname")

        return try {
            DataResource.loading<Boolean>()

            val isNicknameUpdated = editProfileRemoteDataSource.updateNickname(nickname)
            Timber.d("닉네임 업데이트 성공: $isNicknameUpdated")

            DataResource.success(isNicknameUpdated)
        } catch (e: Exception) {
            Timber.e(e, "닉네임 업데이트 실패")
            DataResource.error(e)
        }
    }

    /**
     * 닉네임 중복 여부를 체크한다.
     * - Flow를 사용하여 실시간으로 중복 여부를 감지
     * - 중복 체크 결과를 성공 상태로 반환하며, 예외 발생 시 로그 출력
     */
    override fun checkNicknameDuplicated(nickname: String): Flow<DataResource<Boolean>> = flow {
        Timber.d("checkNicknameDuplicated() 호출 - 닉네임: $nickname")

        emit(DataResource.loading()) // 로딩 상태 방출

        try {
            editProfileRemoteDataSource.checkNicknameDuplicated(nickname)
                .collect { isAvailable ->
                    Timber.d("닉네임 중복 여부 체크 결과: $isAvailable")
                    emit(DataResource.success(isAvailable)) // 성공 상태 방출
                }
        } catch (e: Exception) {
            Timber.e(e, "닉네임 중복 체크 실패")
            emit(DataResource.error(e)) // 에러 상태 방출
        }
    }

    /**
     * 프로필 사진을 업데이트한다.
     * - 원격 데이터 소스를 호출하여 프로필 사진을 변경
     * - 성공 여부를 반환하며, 실패 시 로그를 출력
     */
    override suspend fun updateProfilePic(profilePicPath: String): DataResource<Boolean> {
        Timber.d("updateProfilePic() 호출 - 프로필 사진 경로: $profilePicPath")

        return try {
            DataResource.loading<Boolean>()

            val isProfilePicUpdated = editProfileRemoteDataSource.updateProfilePic(profilePicPath)
            Timber.d("프로필 사진 업데이트 성공: $isProfilePicUpdated")

            DataResource.success(isProfilePicUpdated)
        } catch (e: Exception) {
            Timber.e(e, "프로필 사진 업데이트 실패")
            DataResource.error(e)
        }
    }
}