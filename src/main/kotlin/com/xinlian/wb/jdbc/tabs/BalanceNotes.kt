package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 余额流水
 *
 */
@ApiModel(description = "余额流水")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class BalanceNotes(
        @JsonIgnore
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @ApiModelProperty(value = "用户ID")
        var userId: String,
        @ApiModelProperty(value = "指出收入类型 0 收入  1 支出  2 充值 3 提现")
        var bType: Int,
        @ApiModelProperty(value = "类型为0 或者 1时必须有OrderId 这里补充一下  如果为余额充值也为0。。 前面一句话不成立")
        var orderId: String? = "",
        @Transient
        @ApiModelProperty(value = "订单对应的技能或需求名称 类型为0 或者 1时必须有")
        var orderDec: String? = "",
        @ApiModelProperty(value = "流水变动的金额")
        var price: Float? = 0.0f,
        @ApiModelProperty(value = "流水发生时间")
        var createTime: Long? = Date().time,
        var payType: Int? = -1,//支付方式 bType==1 || 2时出现  0支付宝 1微信 2余额
        @ApiModelProperty(value = "说明文档")
        var notesDec: String? = if (bType == 0) "${orderDec}交易收入${price}元" else if (bType == 1) "${orderDec}交易支出${price}元" else if (bType == 2) {
            if (payType == 0) "支付宝充值收入${price}元" else "微信充值收入${price}元"
        } else {
            "${orderDec}交易支出${price}元"
        },
        @Transient
        @ApiModelProperty(value = "用户实体")
        var user: User? = null,
        var boundCountId: Long? = -1
)