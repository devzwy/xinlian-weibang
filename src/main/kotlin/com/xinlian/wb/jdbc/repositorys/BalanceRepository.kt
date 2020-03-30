package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.Balance
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface BalanceRepository : JpaRepository<Balance, Long> {

    /**
     * 查询用户余额
     */
    fun findTop1ByUserId(userId: String): Balance?

    /**
     * 根据用户ID删除用户余额信息
     */
    fun deleteByUserId(userId: String)
}