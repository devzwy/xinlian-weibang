package com.xinlian.wb.core.entity

import com.xinlian.wb.util.Constant
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore


data class Request_Web_Merchan(
        var bType: Int? = null,//获取类型 0-获取全部商户 1-获取未审核商户 2-获取审核通过用户 3-获取审核被拒用户
        var userId: String? = null//获取的用户ID，空时获取自己的
)

data class Request_Web_Merchan2(
        var bType: Int? = null,//0-审核通过 1-审核失败
        var errMsg: String? = null,//审核失败的原因
        var merchanId: Long? = null
)


data class Request_Web_Register(
        var phoneNumber: String = "",//用户电话号码
        var pwd: String = "",//用户密码
        var userName: String? = "威帮Web用户",//用户名
        var sex: Int? = -1,//性别  -1 未知 男 1 女 0
        var userLogo: String? = Constant.finalParams.DEFULT_USER_LOGO_URL,//用户头像
        var realName: String = "",//真实姓名
        var proportion: String? = null,//当前代理商设置的与代理区域的用户分成比例 与注册码代理的区域绑定 传参拼接规则：注册码-分成比例,注册码-分成比例 eg:11111-20,22222-50 这里的20/50均代表当前用户所得百分比 比如服务者收入120 设置50后可得120*(50/100.0)=60元
        var registerId: String = "")//注册码

/**
 * 注册和重置密码上送参数
 */
@ApiModel(description = "注册和重置密码公用Model")
data class RequestBean_Register(
        @ApiModelProperty(value = "电话号码")
        var phoneNumber: String = "",
        @ApiModelProperty(value = "验证码")
        var msgCode: String = "",
        @ApiModelProperty(value = "密码")
        var password: String = "",
        @ApiModelProperty(value = "用户身份标示 注册时必传 重置密码时非必填 0 安卓  1 苹果  2小程序  3 web")
        var userType: Int? = null,
        @ApiModelProperty(value = "注册类型  0 正常软件注册  1 微信注册  2 QQ注册")
        var registerType: Int? = null,
        @ApiModelProperty(value = "openId 注册类型为1 或2时必填")
        var openId: String? = "",
        var boundCountId: Long? = null
)

/**
 * 身份认证
 */
@ApiModel(description = "身份证认证实体")
data class RequestBean_AuthCard(@ApiModelProperty(value = "身份证号") var cardNumber: String = "", @ApiModelProperty(value = "真实姓名") var userRelName: String = "")

/**
 * 删除/封禁用户
 */
@ApiIgnore
data class RequestBean_DeleteOrBan(
        @ApiIgnore
        var phoneNumber: String = "",
        //封禁传 0   删除传 1  解封传2
        var doType: Int = -1)

/**
 * 获取验证码上送参数
 */
@ApiModel(description = "获取验证码")
data class RequestBean_GetMsgCode(@ApiModelProperty(value = "电话号码") var phoneNumber: String = "")


@ApiIgnore
data class RequestBean_GetAllUser(
        //type 0 全部用户(app端 不包括admin用户)  1未封禁用户  2 已封禁用户
        var doType: Int? = 0)


/**
 * 登录上送参数
 * [signType] 登录方式  0 短信登录   1 密码登录
 * [phoneNumber] 电话号码
 * [msgCode] 验证码  signType为0时必传
 * [password] 密码  signType为1时必传
 */
@ApiModel(description = "登录请求参数")
data class RequestBean_DoSign(
        @ApiModelProperty(value = "登录方式  0 短信登录   1 密码登录  2 第三方登录")
        var signType: Int = -1,
        @ApiModelProperty(value = "电话号码")
        var phoneNumber: String = "",
        @ApiModelProperty(value = "验证码  signType为0时必传")
        var msgCode: String = "",
        @ApiModelProperty(value = "密码  signType为1时必传")
        var password: String = "",
        @ApiModelProperty(value = "openId  signType为2时必传")
        var openId: String = "",
        var openIdType: Int = 0,
        var userType: Int? = null,
        var cType: Int? = 0//登陆的客户端类型 0 - 客户端 1-服务端
)


/**
 * 获取轮播图
 * [bannerType] 0 表示获取首页轮播图 其他情况待定
 */
@ApiModel(description = "获取轮播图请求参数")
data class RequestBean_GetBanner(
        @ApiModelProperty(value = "0 表示获取首页轮播图 其他情况待定")
        var bannerType: Int = -1)


/**
 * 获取用户个人信息
 * [userId] 0 表示获取首页轮播图 其他情况待定
 */
@ApiModel(description = "获取用户个人信息、技能列表")
data class RequestBean_GetUserInfo(
        @ApiModelProperty(value = "为空时返回当前登录用户的用户资料、技能列表")
        var userId: String? = "")

data class RequestBean_GetSkills(
        @ApiModelProperty(value = "为空时返回当前登录用户的用户资料、技能列表")
        var userId: String? = "",
        @ApiModelProperty(value = "经纬度为空时排序会出问题")
        val lat: Float?,
        val lng: Float?,
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)



/**
 * 获取全部技能列表的筛选条件
 * [subTagId] 副标签ID subTagID
 */
@ApiModel(description = "获取全部技能列表的筛选条件")
data class RequestBean_GetAllSkill(
        @ApiModelProperty(value = "根据技能副标签筛选数据 为空时默认获取全部类型数据")
        val subTagId: Int? = -1,
        @ApiModelProperty(value = "经纬度为空时排序会出问题")
        val lat: Float?,
        val lng: Float?,
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

/**
 * 分页数据上传实体
 */
@ApiModel(description = "获取全部技能列表的筛选条件")
data class RequestBean_List(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

/**
 * 获取余额流水
 */
@ApiModel(description = "获取余额流水")
data class RequestBean_GetBalanceRe(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE,
        @ApiModelProperty(value = "筛选字段 0-全部流水 1-收入流水 2-支出流水")
        val bType: Int = -1
)

data class RequestBean_SetPsw(val password: String, var newPassword: String?)

/**
 * 修改用户个人信息
 */
@ApiModel(description = "修改用户个人信息")
data class RequestBean_UpdateUserInfo(
        @ApiModelProperty(value = "修改用户个人资料")
        var userName: String? = null,//用户名
        @ApiModelProperty(value = "性别  -1 未知 男 1 女 0")
        var sex: Int? = null,//性别  -1 未知 男 1 女 0)
        @ApiModelProperty(value = "用户头像")
        var userLogo: String? = null,
        @ApiModelProperty(value = "生日，时间戳 ms")
        val birthday: Long? = null,
        var userProfiles: String? = null
)

/**
 * 获取技能详情
 */
@ApiModel(description = "获取技能详情")
data class RequestBean_GetSkillDetail(
        @ApiModelProperty(value = "技能ID")
        var skillId: Long = -1L)


/**
 * 删除日志
 */
@ApiIgnore
data class RequestBean_DeleteLog(
        var logId: String = "",
        //delType  0 删除全部  1 删除 已修复  2 删除logId对应的记录
        val delType: Int = -1)


/**
 * 收藏/取消收藏请求数据
 */
@ApiModel(description = "收藏/取消收藏")
data class RequestBean_LikeOrDisLike(
        @ApiModelProperty(value = "需要收藏/取消收藏的用户ID")
        var userId: String? = null,
        @ApiModelProperty(value = "需要收藏传1 收藏传0 默认为收藏")
        var doType: Int? = null)


/**
 * 收藏/取消收藏技能请求数据
 */
@ApiModel(description = "收藏/取消收藏技能")
data class RequestBean_LikeOrDisLikeSkill(
        @ApiModelProperty(value = "需要收藏/取消收藏的技能ID")
        var skillId: String? = "",
        @ApiModelProperty(value = "需要收藏传1 收藏传0 默认为收藏")
        var doType: Int = 0)

/**
 * 技能留言
 */
data class RequestBean_AddSkillMessage(
        @ApiModelProperty(value = "留言内容")
        var content: String? = null,
        @ApiModelProperty(value = "上条留言的ID 如果为回复留言时必填 给技能添加留言时无需填写 ")
        var parentId: Int? = -1,
        @ApiModelProperty(value = "技能ID")
        var skillId: Int? = 0)


data class RequestBean_Pay(
        @ApiModelProperty(value = "订单号", required = true)
        val orderId: String,
        @ApiModelProperty(value = "支付方式  0 余额 1 微信 2 支付宝", required = true)
        val payType: Int,
        @ApiModelProperty(value = "金额", required = true)
        val price: String,
        val addPrice: Float = 0.0f,//小费
        val password: String? = null //余额支付时需填写支付密码
)

data class RequestBean_CancleOrder(
        //订单ID
        val orderId: String,
        val tCode: Int? = null//收获码
)

data class RequestBean_RegisterCodeDetail(
        //注册码编号
        val registerId: String)


data class RequestBean_RegisterCode(
        var boundAreaListSrt: String,//代理的区域 多个用逗号隔开
        var proportion: Float? = null//分成比例 生成一级代理商时为空 生成二级代理商时表示与二级代理商的分成比例 生成二级代理商时必填)

)

data class RequestBean_OrderComm(
        var imgUrls: String = "",//评价的图片
        var skillId: Long = 0,//技能编号
        var userIdFromBuy: String = "",//买方编号
        var orderId: String = "",//订单号
        var content: String = "",//评价的内容
        var commStar: Int = -1//评价的星级
)

data class RequestBean_DeleteWebTab(
        var tabId: Long? = null,
        var title: String? = null,
        var url: String? = null,
        var parentTabId: Long? = null)

data class RquestBean_DelAgent(var userId: String = "",//代理商ID
        //0 - 删除  1-封禁 2-解封
                               var bType: Int = -1)

data class AddBalanceBean(var price: Float? = null,//充值的金额
                          var userId: String? = null,//用户ID
                          var doType: Int? = null//支付类型  0 - 支付宝  1 - 微信
)

data class RequestBean_GetDemands(
        val lat: Float?,
        val lng: Float?,
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)
