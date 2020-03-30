package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.OrderCommTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface OrderCommRepository : JpaRepository<OrderCommTab, Long> {

    /**
     * 根据订单号查询该订单的评价信息
     */
    fun findTop1ByOrderId(orderID: String): OrderCommTab?

    fun findAllBySkillId(skillId: Long): List<OrderCommTab>
}
