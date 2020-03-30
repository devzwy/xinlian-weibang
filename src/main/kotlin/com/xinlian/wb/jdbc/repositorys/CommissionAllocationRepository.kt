package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.CommissionAllocation
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface CommissionAllocationRepository : JpaRepository<CommissionAllocation, Long> {
    /**
     * 查询配置的手续费信息信息
     */
    fun findTop1ById(id: Long = 0): CommissionAllocation?

}