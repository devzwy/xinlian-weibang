package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.RegisterCode
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional


/**
 * 一级代理商表
 */
@Transactional
interface RegisterCodeRepository : JpaRepository<RegisterCode, Long> {

    /**
     * 根据生成的用户ID获取已生成的所有注册码
     */
    fun findAllByCreatUserId(createUserId: String): List<RegisterCode>?

    /**
     * 查询一个注册码详情
     */
    fun findTop1ByRegisterId(registerId: String): RegisterCode?


    fun deleteByRegisterId(registerId: String)
}

