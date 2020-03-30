package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs.UserLocationAndDeviceIdTab
import com.xinlian.wb.jdbc.tabs.UserSkill
import com.xinlian.wb.jdbc.tabs.WBOrder

interface IBusunessService {

    /**
     * 订单评价
     */
    fun orderComm(mRequestBean_OrderComm: RequestBean_OrderComm, token: String): HttpResponse<Any>

    /**
     * 完成订单
     */
    fun finishOrder(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any>

    /**
     * 取消订单
     */
    fun cancleOrder(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any>

    /**
     * 获取轮播图
     */
    fun getBanner(mRequestBean_GetBanner: RequestBean_GetBanner): HttpResponse<Any>

    /**
     * 获取动态
     */
    fun getDynamic(): HttpResponse<Any>

    /**
     * 获取用户发布的技能列表
     */
    fun getSkills(token: String, mRequstSkillsBean: RequestBean_GetSkills): HttpResponse<Any>


    /**
     * 发布技能
     */
    fun sendSkill(userSkill: UserSkill, token: String): HttpResponse<Any>

    /**
     * 获取技能详情
     */
    fun getSkillDetail(requestSkillDetailBean: RequestBean_GetSkillDetail, token: String): HttpResponse<Any>


    /**
     * 获取全部技能列表
     */
    fun getAllSkills(token: String, mRequestBean_GetAllSkill: RequestBean_GetAllSkill): HttpResponse<Any>

    /**
     * 收藏取消收藏技能
     */
    fun likeOrUnUnLikeSkill(mRequestBean_LikeOrDisLikeSkill: RequestBean_LikeOrDisLikeSkill, token: String): HttpResponse<Any>

    /**
     * 获取收藏的技能列表
     */
    fun getLikedSkills(token: String,mReqBeanGetLikedSkills:ReqBeanGetLikedSkills): HttpResponse<Any>

    /**
     * 技能留言
     */
    fun addSkillMessage(mRequestBean_AddSkillMessage: RequestBean_AddSkillMessage, token: String): HttpResponse<Any>

    /**
     * 生成订单
     */
    fun createOrder(mRequestBean_CreateOrder: WBOrder, token: String): HttpResponse<Any>

    /**
     * 查询用户余额
     */
    fun getBalance(token: String): HttpResponse<Any>

    /**
     * 支付
     */
    fun pay(mRequestBean_Pay: RequestBean_Pay, token: String): HttpResponse<Any>

    /**
     * 获取当前用户的订单列表
     */
    fun getOrderList(mReq_GetMyOrderList: Req_GetMyOrderList, token: String): HttpResponse<Any>

    fun updataUserLocationAndDeviceId(reqBean: UserLocationAndDeviceIdTab, token: String): HttpResponse<Any>


}