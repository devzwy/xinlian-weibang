package com.xinlian.wb.core.entity

import com.xinlian.wb.jdbc.tabs.User
import com.xinlian.wb.jdbc.tabs_web.User_Web
import io.swagger.annotations.ApiModelProperty

data class DoSomeThingResult(val isSucc: Boolean = true, val errorMsg: String = "")

data class RegisterWyyResult(val isSucc: Boolean = true, val errorMsg: String = "", val accid: String = "", val accToken: String = "")

open class AccIdBean(var token: String = "", var accid: String = "", var name: String = "")

data class ResultFromAliYunMsgCode(val Code: String, val Message: String, val BizId: String, val RequestId: String)

data class ResultFromWYYRegisterAccId(var desc: String, var code: Int = 0, var info: AccIdBean? = null)

data class WBException(val code: Int = -9000, var msg: String) : Exception()
data class AlipayTrBean(val msg: String? = null, val code: String?, val sub_msg: String?, val sub_code: String?)

/**
 * 推送实体
 * [pType] 推送的业务类型 客户端用来区分事件处理 0 - 跑腿代办推送  1-同城配送 2 -  需求 bussContent为跑腿代办的ID
 * [title]推送饿标题
 * [content]推送的内容
 * [bussContent]业务数据 可以为任意值
 */
data class PushData(val pType: Int, val title: String, val content: String, val bussContent: String)

data class ReqBeanWithdrawal(
        var price: Double,//提现的金额
        var alipayUserName: String,//支付宝姓名
        var aliaAccount: String//支付宝账号
)

data class WithdrawalAuditBean(
        val withdrawalId: Long? = -1,
        val bType: Int = -1,//0 - 同意退款  1 - 拒绝退款
        val errorMsg: String? = null // 拒绝的原因
)

data class SubTagControllerBean(
        val doType: Int = -1,
        val subTagId:Long = -1
)

data class ReqBeanGetWithdrawal(
        var bType: Int? = -1,//管理员查询时可传入该字段 0 - 仅查询用户提现流水记录 1 - 仅 查询代理商提现流水记录
        var state: Int = -1,//对应的状态 -1 查询全部 默认-1
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class ReqBeanSearchSkills(
        var keyWord: String,//
        var lat: Double = 999.0,//
        var lng: Double = 999.0,//
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class ReqBeanGetAllSkill(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)


data class ReqBeanGetWithdrawal2(
        var state: Int = 3,//3 - 获取全部 筛选字段 0-等待报名 1-服务中 2 服务完成  -1 已取消 -2 已过期
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class ReqBeanGetLikedSkills(
        var lat: Float=999.0F,
        var lng: Float=999.0F,
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class ReqBeanGetAllMerchantList(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class RequestBean_BanDemand(var demandId: Long, var doType: Int = -1)

data class RequestBean_BanSkill(var skillId: Long, var doType: Int = -1)
data class ReqBeanMarkedWithdrawal(
        val id: Long? = null,
        val state: Int? = null,//对应的状态
        val errMsg: String? = null//处理失败的原因
)

data class SignOutBean(var cType1: Int//推出登陆的类型 客户端 - 0  服务端 -1
)

data class CPrice(
        val bType: Int? = -1,//同城传0 跑腿传1
        val businessAddressId: Long? = -1,//同城传取货地址ID  跑腿传购买地址ID(跑腿代办可选)
        val addresId: Long? = -1, //收获地址ID
        val bTime: Long? = -1 //送达时间戳

)

//获取周围的服务者或派送员

data class RequestBean_getNearUserBean(var orderId: String? = null, var demandId: Long = -1)

data class RespBeanNearUserBean(var user: User, var lat: Double, var lng: Double)

data class ReqtgetAllDispatcherAndMerchants(var lat: Double = 999.0, var lng: Double = 999.0)
data class RPrice(var price: Int,//金额
                  var distance: String//距离
)

data class Req_GetMyOrderList(var state: Int = -1,//对应的状态-1 获取全部订单 1-待支付 2-待接单 4-待服务 3-待评价
                              var buyOrServers: Int = -1,//userId不为空时必传 0 - 买方订单  1 - 卖方订单
                              @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
                              val page: Int = 1,
                              @ApiModelProperty(value = "请求的条数 默认返回全部数据")
                              val number: Int = Int.MAX_VALUE,
                              @ApiModelProperty(value = "筛选的用户ID")
                              val userId: String? = null)

data class Req_GetSystemNotification(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class Req_GetAllRun(var type: Int = -1,//0 - 跑腿代办 1 - 同城配送 2 - 全部
                         @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
                         val page: Int = 1,
                         @ApiModelProperty(value = "请求的条数 默认返回全部数据")
                         val number: Int = Int.MAX_VALUE)

data class CityInformation(
        val info: String,
        val infocode: String,
        val regeocodes: List<Regeocode>,
        val status: String
)

data class Regeocode(
        val addressComponent: AddressComponent,
        val formatted_address: String
)

data class UsersBean(val userCount: Int = 0, val userList: List<User>)
data class AgentListBean(val agentCount: Int = 0, val agentList: List<User_Web>)

data class Request_DivWater(var state: Int = 2,//0 - 支出  1 - 收入 2 - 全部
                            @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
                            val page: Int = 1,
                            @ApiModelProperty(value = "请求的条数 默认返回全部数据")
                            val number: Int = Int.MAX_VALUE)

data class Request_AddBalanceNoto(
        @ApiModelProperty(value = "请求的页码 默认 1 必须从1开始")
        val page: Int = 1,
        @ApiModelProperty(value = "请求的条数 默认返回全部数据")
        val number: Int = Int.MAX_VALUE)

data class AddressComponent(
        val adcode: String,
        val building: Building,
        val businessAreas: List<BusinessArea>,
        val city: Any,
        val citycode: String,
        val country: String,
        val district: String,
        val neighborhood: Any,
        val province: String,
        val streetNumber: StreetNumber,
        val towncode: String,
        val township: String
)

data class Building(
        val name: List<Any>,
        val type: List<Any>
)

data class BusinessArea(
        val id: String,
        val location: String,
        val name: String
)


data class StreetNumber(
        val direction: String,
        val distance: String,
        val location: String,
        val number: String,
        val street: String
)

data class BoundCityBean(val county_id: Long, val town_id: Long)

data class SignDemandBean(
        //报名需求的ID
        val demandId: Long,
        //报名需求的技能ID
        val skillId: Long)