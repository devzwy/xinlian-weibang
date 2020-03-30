package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs_web.ShowWebBoradTab
import com.xinlian.wb.jdbc.tabs_web.User_Web
import com.xinlian.wb.jdbc.tabs_web.WebTabTitleBean
import javax.servlet.http.HttpServletResponse


interface IWebService {

    /**
     * 用户充值流水记录
     */
    fun userTopUpWater(mRequest_AddBalanceNoto: Request_AddBalanceNoto, token: String): HttpResponse<Any>

    /**
     * 分成流水记录查询
     */
    fun dividedWaterSearch(mRequest_DivWater: Request_DivWater, token: String): HttpResponse<Any>

    /**
     * 提现成功
     */
    fun markWithdrawal(mReqBeanMarkWithdrawal: ReqBeanMarkedWithdrawal, token: String): HttpResponse<Any>

    /**
     * 获取商户列表
     */
    fun getMerchantList(mRequest_Web_Merchan: Request_Web_Merchan, token: String): HttpResponse<Any>

    /**
     * 删除注册码
     */
    fun deleteRegisterCode(mRequestBean_RegisterCodeDetail: RequestBean_RegisterCodeDetail, token: String): HttpResponse<Any>

    /**
     * 获取首页菜单详情
     */
    fun getHomeTabDetail(mRequestBean_DeleteWebTab: RequestBean_DeleteWebTab, token: String): HttpResponse<Any>

    /**
     * 删除封禁代理商
     */
    fun delOrBanAgentUser(userWeb: RquestBean_DelAgent, token: String): HttpResponse<Any>

    /**
     * 修改个人资料
     */
    fun updateUserInfo(userWeb: User_Web, token: String): HttpResponse<Any>

    /**
     * 查询代理商列表
     */
    fun getAgentList(mRequestBean_List: RequestBean_List, token: String): HttpResponse<Any>

    /**
     * 获取首页菜单
     */
    fun getWebHomeTab(token: String): HttpResponse<Any>

    /**
     * 修改首页菜单
     */
    fun updateWebHomeTab(mre: RequestBean_DeleteWebTab, token: String): HttpResponse<Any>

    /**
     * 删除首页菜单
     */
    fun deleteWebHomeTab(mre: RequestBean_DeleteWebTab, token: String): HttpResponse<Any>

    /**
     * 获取注册码详细数据
     */
    fun getRegisterCodeDetail(mRequestBean_RegisterCodeDetail: RequestBean_RegisterCodeDetail, token: String): HttpResponse<Any?>

    /**
     * 获取有效注册码
     */
    fun getRegisterCode(token: String?): HttpResponse<Any>

    /**
     * 获取省市区信息
     */
    fun getCityList(token: String?): HttpResponse<Any>

    /**
     * 生成注册码
     */
    fun creatRegisterCode(mRequestBean_RegisterCode: RequestBean_RegisterCode, token: String): HttpResponse<Any>

    /**
     * 删除公告栏
     */
    fun deleteShowBoradContent(token: String): HttpResponse<Any>

    /**
     * 发布公告栏
     */
    fun sendShowBoradContent(mShowWebBoradTab: ShowWebBoradTab, token: String): HttpResponse<Any>

    /**
     * 获取首页数据
     */
    fun getHomeData(token: String): HttpResponse<Any>

    /**
     * 添加首页侧边栏
     */
    fun addWebHomeTab(webTabTitleBean: WebTabTitleBean, token: String): HttpResponse<Any>


    /**
     * 登录
     */
    fun login(userWeb: Request_Web_Register, httpServletResponse: HttpServletResponse): HttpResponse<Any>

    /**
     * 注册管理员或代理商
     */
    fun register(userWeb: Request_Web_Register): HttpResponse<Any>

    /**
     * 根据电话号码查询验证码
     */
    fun getMsgCodeByPhoneNumber(mRequestBean_GetMsgCode: RequestBean_GetMsgCode, token: String): HttpResponse<Any>

    /**
     * 抽奖
     */
    fun getLuckFrawNumber(): HttpResponse<Any>

    /**
     *获取用户列表
     */
    fun getAllUsers(mRequestBean_GetAllSkill: RequestBean_GetAllSkill, token: String): HttpResponse<Any>

    /**
     * 删除或封禁用户
     */
    fun deleteOrBanUser(mRequestBean_DeleteOrBan: RequestBean_DeleteOrBan, token: String): HttpResponse<Any>

    /**
     * 获取web首页数据
     */
    fun getWebViewInfo(token: String): HttpResponse<Any>

    /**
     * 获取崩溃日志列表
     */
    fun getLogList(): HttpResponse<Any>

    /**
     * web端删除日志接口
     */
    fun deleteLog(mRequestBean_DeleteLog: RequestBean_DeleteLog): HttpResponse<Any>

    /**
     * 标记对应日志为已修复
     */
    fun fixLogByLogId(mRequestBean_DeleteLog: RequestBean_DeleteLog): HttpResponse<Any>

    fun verMerchant(mRequest_Web_Merchan2: Request_Web_Merchan2, token: String): HttpResponse<Any>
    fun getTransactionCode(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any>
    fun addBalance(mAddBalanceBean: AddBalanceBean, token: String): HttpResponse<Any>
}