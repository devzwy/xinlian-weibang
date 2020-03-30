package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

/**
 * 添加用户收货地址实体
 *
 */
@ApiModel(description = "添加用户收货地址实体")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class Address(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @ManyToOne
        @JoinColumn(name = "userId")
        @JsonIgnore
        var user: User? = null,
        @ApiModelProperty(value = "手机号码")
        var phoneNumber: String? = null,
        @ApiModelProperty(value = "地址前缀")
        var address_head: String? = null,
        @ApiModelProperty(value = "地址后缀(详细地址)")
        var address_end: String? = null,
        @ApiModelProperty(value = "收货人姓名")
        var name: String? = null,
        @ApiModelProperty(value = "收货人性别")
        var sex: String? = null,
        @ApiModelProperty(value = "收货地址标签")
        var tag: String? = null,
        var isDefaultAddress: Boolean? = false,
        @ApiModelProperty(value = "收货地址经纬度")
        var addressLat: Double? = null,
        @ApiModelProperty(value = "收货地址经纬度")
        var addressLng: Double? = null
) {
    override fun toString(): String {
        return "地址：收货人手机号：${phoneNumber}"
    }
}