package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 订单表
 *
 */
@ApiModel(description = "订单实体")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class WBOrder(

        @Id
        @ApiIgnore("隐藏")
        var orderId: String = "wb-${Date().time}",

        @ApiModelProperty(value = "生成的订单类型 0 - 技能订单(即技能下单) 1 - 跑腿代办 2 - 同城配送", required = true)
        var orderType: Int,

        @ApiModelProperty(value = "订单类型对应的有误ID，比如为0时传入的为技能ID 1为跑腿代办ID 2为同城配送ID", required = true)
        var orderBusinessId: String? = "",

        @ApiModelProperty("订单状态码 0 默认状态（未产生交易方/被交易方） 1 待付款  -1 已取消 2 已支付  -2 支付失败 3 订单完成 4 待服务(跑腿代办、同城配送) 5已取待送达(跑腿代办、同城配送) 6 已评价(双方已评)", required = true)
        var state: Int? = 0,

        @ApiModelProperty("购买的数量", required = true)
        var number: Int,

        @ApiModelProperty("服务时间(传时间戳) -1 表示尽快", required = true)
        var sTime: Long,

        @ApiModelProperty("服务地址索引ID(跑腿代办和同城配送中为收获地址)", required = true)
        var sAddressId: Int,

        @ApiModelProperty("订单备注信息", required = false)
        var orderDec: String? = null,

        @ApiModelProperty("订单金额 -单价", required = true)
        var price: Float,

        @ApiModelProperty("买方ID")
        @ApiIgnore
        var userIdFromBuy: String? = "",

        @ApiModelProperty("卖方ID")
        @ApiIgnore
        var userIdFromServer: String = "",

        @ApiModelProperty("订单支付时间")
        @ApiIgnore
        var orderPayTime: Long? = null,


        @ApiModelProperty("订单创建时间")
        @ApiIgnore
        var orderCreateTime: Long? = Date().time,

        //评价表
        var commId: Long? = null,

        @ApiModelProperty("服务方用户实体")
        @Transient
        var userFromServer: User? = null,

        @ApiModelProperty("购买方用户实体")
        @Transient
        var userFromBuy: User? = null,
        @Transient
        var address: Address? = null,
        //完成订单的时间
        var compilerOrderTime: Long? = null,
        //规格ID
        var specsId: Long? = null,
        var addPrice: Float = 0.0f,
        var getOrderTime: Long? = null,//抢单的时间
        var takeDeliveryTime: Long? = null,//取货时间
        var transactionCode: Int? = null,//取货码
        @Transient
        var orderBusinessBean: Any? = null,
        var payment: Int? = null, //支付方式 0 余额支付  1-支付宝支付 2-微信支付
        @Transient
        var commed: Boolean? = false,//当前用户是否已经评价
        @Transient
        var specsBean: SpecsTabBean? = null,
        //代表当前订单的购买方是否为需求发布方 false表示一般用户购买  true为需求发布方购买，在付款成功后要将需求状态改动
        var buyFromDemand: Boolean? = false,
        //需求ID 当buyFromDemand为true时有值
        var demandId: Long? = null

)