package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 余额表
 *
 */
@ApiModel(description = "余额表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class Balance(
        @Id
        @ApiModelProperty(value = "用户ID")
        var userId: String = "aaa",
        @ApiModelProperty(value = "用户余额")
        @Column(name = "balance", columnDefinition="double(10,2) default '0.00'")
        var balance: Double? = 0.0,
        @ApiModelProperty(value = "用户冻结余额")
        var freeBalance: Double? = 0.0,
        @ApiModelProperty(value = "支付密码")
        @JsonIgnore
        var payPsw: String? = null,
        @Transient
        @ApiModelProperty(value = "总收入")
        var allFromPrice: Double? = 0.0,
        @Transient
        @ApiModelProperty(value = "是否已设置密码")
        var isSetPsw: Boolean? = payPsw != null
)