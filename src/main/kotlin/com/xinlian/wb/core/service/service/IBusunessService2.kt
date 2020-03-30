package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs.MerchanAuthTab
import com.xinlian.wb.jdbc.tabs.UserRunErrand
import com.xinlian.wb.jdbc.tabs.WBOrder

interface IBusunessService2 {
    /**
     * 获取同城、跑腿列表
     */
    fun getAllRunErrand(mReq_GetAllRun: Req_GetAllRun, token: String): HttpResponse<Any?>
    fun getMerchantList(token: String,mReqBeanGetAllMerchantList: ReqBeanGetAllMerchantList): HttpResponse<Any?>
    fun getQNToken(): HttpResponse<Any?>
    fun getOrderDetail(wbOrder: WBOrder, token: String): HttpResponse<Any?>
    fun getMerchantDetail(mMerchanAuthTab: MerchanAuthTab, token: String): HttpResponse<Any>
    fun merchantAuth(mMerchanAuthTab: MerchanAuthTab, token: String): HttpResponse<Any>
    fun getRunErrandsTags(token: String): HttpResponse<Any>
    fun createRunErrand(mUserRunErrand: UserRunErrand, token: String): HttpResponse<Any>
    fun grabUserRunErrandSheet(orderId: WBOrder, token: String): HttpResponse<Any>
    fun takeDelivery(orderId: WBOrder, token: String): HttpResponse<Any>
    fun calculatePrice(mCPrice: CPrice, token: String): HttpResponse<Any>
    fun searchSkills(mReqBeanSearchSkills: ReqBeanSearchSkills, token: String): HttpResponse<Any>
}