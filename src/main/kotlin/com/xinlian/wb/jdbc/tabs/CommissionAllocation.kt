package com.xinlian.wb.jdbc.tabs

import javax.persistence.Entity
import javax.persistence.Id

/**
 * 手续费配置表
 */
@Entity
data class CommissionAllocation(
        @Id
        var id: Long,
        val cancleOrderCommissionAllocation: Float = 20.0f,//用户主动取消订单的手续费
        val agentCommissionAllocation: Float = 30.0f,//公司与代理商的分成比例
        val unBoundAgentUserCommissionAllocation: Float = 30.0f)//公司与未绑定代理商的区域用户的分成比例