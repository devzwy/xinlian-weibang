package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.MerchanAuthTab
import com.xinlian.wb.jdbc.tabs.UserRunErrand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface MerchanAuthRepository : JpaRepository<MerchanAuthTab, Long> {

    fun findTop1ByUserId(userId: String): MerchanAuthTab?
    fun findTop1ByMerchanId(merchanId: Long): MerchanAuthTab?

    fun findAllByVerifyState(verifyState: Int = 2): List<MerchanAuthTab>?

    /**
     * web端查询全部技能列表
     */
    @Query(value = "SELECT * FROM merchan_auth_tab WHERE verify_state= :verifyState ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAllByVerifyState(@Param("verifyState") verifyState: Int = 2, @Param("page") page: Int = 0, @Param("number") number: Int = 20):List<MerchanAuthTab>
}

