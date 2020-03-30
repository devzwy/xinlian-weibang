package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

@ApiModel(description = "用户身份认证表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class IdentityCardAuthenticationTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = 0,

        @ApiModelProperty(value = "用户信息")
        @OneToOne
        @JoinColumn(name = "userId")
        @JsonIgnore
        var user: User? = null,
        @ApiModelProperty(value = "实名的姓名")
        var idemyityName: String?,
        @ApiModelProperty(value = "实名的身份证号")
        var idemyityCardNumber: String?
)