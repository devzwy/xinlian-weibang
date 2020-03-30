package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.UserLocationAndDeviceIdTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface UserLocationAndDeviceIdTabRepository : JpaRepository<UserLocationAndDeviceIdTab, Long> {

    /**
     * 根据用户ID查询用户的地理位置和推送ID
     */
    fun findTop1ByUserId(userId: String): UserLocationAndDeviceIdTab?
}