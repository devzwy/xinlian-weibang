package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs.Demand


interface IOtherService {


    /**
     * 获取系统通知
     */
    fun getSystemNotifications(mReq_GetSystemNotification: Req_GetSystemNotification, token: String): HttpResponse<Any>


    /**
     * 获取全部派送员和商户列表
     */
    fun getAllDispatcherAndMerchants(mReqtgetAllDispatcherAndMerchants: ReqtgetAllDispatcherAndMerchants, token: String): HttpResponse<Any>

    /**
     * 根据订单ID获取周围服务者或派送员
     */
    fun getNearUserList(mRequestBean_getNearUserBean: RequestBean_getNearUserBean, token: String): HttpResponse<Any>

    /**
     * 派送员认证
     */
    fun dispatcherCertification(token: String): HttpResponse<Any>

    /**
     * 重置支付密码
     */
    fun reSetPassword(mRequestBean_SetPsw: RequestBean_SetPsw, token: String): HttpResponse<Any>

    /**
     * 设置支付密码
     */
    fun setPassword(mRequestBean_SetPsw: RequestBean_SetPsw, token: String): HttpResponse<Any>

    /**
     * 获取余额流水
     */
    fun getBalanceRecord(mRequestBean_GetBalanceRe: RequestBean_GetBalanceRe, token: String): HttpResponse<Any>

    /**
     * 余额充值
     */
    fun addBalance(mAddBalanceBean: AddBalanceBean, token: String): HttpResponse<Any>

    /**
     * 发布需求
     */
    fun releaseRequirements(mDemand: Demand, token: String): HttpResponse<Any>

    /**
     * 报名需求
     */
    fun signUpRequirements(mSignDemandBean: SignDemandBean, token: String): HttpResponse<Any>


    /**
     * 需求详情
     */
    fun requirementsDetails(mDemand: Demand, token: String): HttpResponse<Any>

    /**
    获取我发布的需求列表
     */
    fun getMyRequirements(mDemand: RequestBean_GetDemands, token: String): HttpResponse<Any>

    /**
    获取全部需求列表
     */
    fun getAllRequirements(mRequestBean_GetDemands: RequestBean_GetDemands, token: String): HttpResponse<Any>

}