package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SystemNotification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface SystemNotificationRepository : JpaRepository<SystemNotification, Long> {


    /**
     * 根据用户ID查询该用户所有的系统通知
     */
    @Query(value = "SELECT * FROM system_notification WHERE user_id = :user_id ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("user_id") user_id: String, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<SystemNotification>
}