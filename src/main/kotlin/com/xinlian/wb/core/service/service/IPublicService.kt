package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.ReqBeanGetWithdrawal
import com.xinlian.wb.core.entity.ReqBeanWithdrawal


interface IPublicService {
    /**
     * 提现申请
     */
    fun withdrawal(mReqBeanWithdrawal: ReqBeanWithdrawal, token: String): HttpResponse<Any>


    /**
     * 获取提现申请记录
     */
    fun getWithdrawal(mReqBeanGetWithdrawal: ReqBeanGetWithdrawal, token: String): HttpResponse<Any>
}