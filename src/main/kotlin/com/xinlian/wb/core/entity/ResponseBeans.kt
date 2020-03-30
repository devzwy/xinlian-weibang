package com.xinlian.wb.core.entity

import com.xinlian.wb.jdbc.tabs.SkillParentTab
import com.xinlian.wb.jdbc.tabs.User
import com.xinlian.wb.jdbc.tabs.UserSkill
import com.xinlian.wb.jdbc.tabs.WBOrder
import com.xinlian.wb.jdbc.tabs_web.ShowWebBoradTab
import com.xinlian.wb.util.Constant
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.io.Serializable

data class ErrorResponse(var code: Int = -999, var message: String = "服务端处理异常,请重试")

@ApiModel(description = "响应数据头")
data class HttpResponse<T>(var code: Int,
                           var message: String,
                           var data: T?) : Serializable {
    constructor(data: T) : this(Constant.Code.SUCCESSFUL_CODE, "Success", data)
}

/**
 * 获取WEB首页数据返回数据
 */
data class WebHomeDataBean(var upDayAddUserNumber: Int,//昨日新增用户数量
                           var upDayUserNumber: Int,//昨日用户总数 昨日之前注册的用户数量
                           var upDayOrderNumber: Int,//昨日订单总数
                           var upDayOrderAllPrice: Float, //昨日订单总额
                           var userTop10: List<User>?, //新注册的前10个用户
                           var upDayCanUseBalance: Float?,//昨日可用余额
                           var upDayAllGetedBalance: Float?,//昨日总计收入
                           var boardShowDec: ShowWebBoradTab?,//公告栏展示内容
                           var orderTop10: List<WBOrder>//前10个订单
)

@ApiIgnore
data class TaskResponse(val executionTime: Double)

//web首页数据
data class ResponseBean_Web_Home(val usersNumber: Int, val skillsNumber: Int, val skillSuccNumber: Int, val logNumber: Int, val skillList: List<UserSkill>)

///**
// * 回传的用户信息 区别与数据库实体
// */
//@ApiModel(description = "登录成功后回传的用户实体")
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//data class User(
//        @ApiModelProperty(value = "用户ID")
//        var userId: String,
//        @ApiModelProperty(value = "用户电话号码")
//        var phoneNumber: String,
//        @ApiModelProperty(value = "用户名")
//        var userName: String = phoneNumber,
//        @ApiModelProperty(value = "性别  -1 未知 男 1 女 0")
//        var sex: Int = -1,
//        @ApiModelProperty(value = "用户头像")
//        var userLogo: String = Constant.finalParams.DEFULT_USER_LOGO_URL,
//        @ApiModelProperty(value = "云信accid(聊天)")
//        var wyy_accid: String,
//        @ApiModelProperty(value = "云信token(聊天)")
//        var wyy_token: String
//)

/**
 * 回传的用户信息 区别与数据库实体
 */
@ApiModel(description = "获取动态回传实体")
data class ResponseBean_Dynamic(

        @ApiModelProperty(value = "发布该动态的用户ID")
        val userId: String = "",

        @ApiModelProperty(value = "发布需求为0 发布技能为1")
        val b_Type: Int = 0,

        @ApiModelProperty(value = "发布的技能或者需求名称")
        val b_Name: String = "",

        @ApiModelProperty(value = "发布该动态的用户头像")
        val userLogo: String? = "",

        @ApiModelProperty(value = "发布该动态的用户昵称")
        val userName: String? = "")


/**
 * 回传的Tag数据
 */
@ApiModel(description = "回传的Tag数据，建议客户端全局请求一次，根据tagVersion字段判断是否需要更新")
data class ResponseBean_Tag(

        @ApiModelProperty(value = "当前tag的版本号，每次升级时会+1，客户端缓存数据是否需要更新的标示")
        val tagVersion: Int = 0,

        @ApiModelProperty(value = "技能列表，包括每项子类")
        val tagList: List<SkillParentTab>)

/**
 * 身份证认证 用友的回传
 */
@ApiIgnore
data class ResponseBean_Auth_YY(

        val code: Int = 0,
        val message: String? = "",
        val success: Boolean? = false)


/**
 * 支付回传参数
 */
@ApiIgnore
data class ResponseBean_Pay(val alipayStr: String?, val weChatPayResult: WeChatPayResultBean?)


data class WeChatPayResultBean(
        val appid: String,
        val noncestr: String,
        val `package`: String,
        val partnerid: String,
        val prepayid: String,
        val sign: String,
        val timestamp: Int
)

data class AliResp(
        val app_id: String,
        val auth_app_id: String,
        val body: String,
        val buyer_id: String,
        val buyer_logon_id: String,
        val buyer_pay_amount: String,
        val charset: String,
        val fund_bill_list: String,
        val gmt_create: String,
        val gmt_payment: String,
        val invoice_amount: String,
        val notify_id: String,
        val notify_time: String,
        val notify_type: String,
        val out_trade_no: String,
        val point_amount: String,
        val receipt_amount: String,
        val seller_email: String,
        val seller_id: String,
        val sign: String,
        val sign_type: String,
        val subject: String,
        val total_amount: String,
        val trade_no: String,
        val trade_status: String,
        val version: String
)

data class WxResp(
        val appid: String,
        val bank_type: String,
        val cash_fee: String,
        val fee_type: String,
        val is_subscribe: String,
        val mch_id: String,
        val nonce_str: String,
        val openid: String,
        val out_trade_no: String,
        val result_code: String,
        val return_code: String,
        val sign: String,
        val time_end: String,
        val total_fee: String,
        val trade_type: String,
        val transaction_id: String
)



