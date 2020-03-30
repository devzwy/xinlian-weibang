package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs.SystemNotification
import org.springframework.web.bind.annotation.RequestBody

interface IWebService2 {

    /**
     * 发送系统通知
     */
    fun sendSystemNotification(@RequestBody mSystemNotification: SystemNotification, token: String): HttpResponse<Any>

    /**
     * 封禁/解封技能
     */
    fun doBanSkill(mRequestBean_BanSkill: RequestBean_BanSkill, token: String): HttpResponse<Any>

    /**
     * 获取全部技能列表
     */
    fun getAllSkill(mReqBeanGetAllSkill: ReqBeanGetAllSkill, token: String): HttpResponse<Any>

    /**
     * 封禁/解封需求
     */
    fun doBanDemand(mRequestBean_BanDemand: RequestBean_BanDemand, token: String): HttpResponse<Any>

    /**
     * 获取全部需求列表
     */
    fun getAllDemand(mReqBeanGetWithdrawal: ReqBeanGetWithdrawal2, token: String): HttpResponse<Any>

    /**
     * 提现审核
     */
    fun withdrawalAudit(mWithdrawalAuditBean: WithdrawalAuditBean, token: String): HttpResponse<Any>


    /**
     * 禁用/启用标签
     */
    fun controllerSubTag(mSubTagControllerBean: SubTagControllerBean, token: String): HttpResponse<Any>


    /**
     * 关闭/显示首页展示标签
     */
    fun openOrCloseHomeSubTag(mSubTagControllerBean: SubTagControllerBean, token: String): HttpResponse<Any>


}