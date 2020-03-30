package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.xinlian.wb.util.Constant
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.util.*
import javax.persistence.*

@ApiModel(description = "登录成功后回传的用户实体")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class User(
        @ApiModelProperty(value = "用户ID")
        @Id
        @Column(nullable = false)
        var userId: String,//用户ID
        @ApiModelProperty(value = "用户身份类型 0 android 1 iOS")
        @Column(nullable = false)
        var userType: Int? = null,//用户类型 0 android 1 iOS
        @ApiModelProperty(value = "用户电话号码")
        @Column(nullable = false)
        var phoneNumber: String,//用户电话号码
        @ApiModelProperty(value = "用户密码")
        @Column(nullable = false)
        @JsonIgnore
        var password: String? = null,//用户密码
        @ApiModelProperty(value = "用户名")
        var userName: String = "${phoneNumber.substring(0, 3)}****${phoneNumber.substring(7, 11)}",//用户名
        @ApiModelProperty(value = "性别  -1 未知 男 1 女 0")
        var sex: Int = -1,//性别  -1 未知 男 1 女 0
        @ApiModelProperty(value = "生日  默认为0")
        var birthday: Long? = Date().time,//生日
        @ApiModelProperty(value = "用户头像")
        var userLogo: String = Constant.finalParams.DEFULT_USER_LOGO_URL,//用户头像
        @ApiModelProperty(value = "云信accid(聊天)")
        var wyy_accid: String,//云信accid(聊天)
        @ApiModelProperty(value = "云信token(聊天)")
        var wyy_token: String,//云信token(聊天)
        @ApiModelProperty(value = "微信登录openId")
        var wOpenIdx: String,
        @ApiModelProperty(value = "QQ登录openId")
        var qOpenIdq: String,
        @ApiModelProperty(value = "当前用户绑定的区域ID 后续与代理商分成")
        var boundCountId: Long? = null,

        @ApiIgnore
        var isBan: Boolean? = null,//是否封禁状态 被后台封禁的用户无法登录

        //一对多
        @ApiModelProperty(value = "用户保存的收货地址")
        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
        @JoinColumn(name = "userId")
        @JsonIgnore
        var address: List<Address>? = null,


        //一对一
        @ApiModelProperty(value = "用户的实名信息")
        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "authId")
        var indentity: IdentityCardAuthenticationTab? = null
        ,  //一对多
        @ApiModelProperty(value = "用户技能列表")
        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
        @JoinColumn(name = "userId")
        @JsonIgnore
        var userSkills: List<UserSkill>? = null,
        var createTime: Long? = Date().time
        ,
        @Transient
        var token: String? = null,

        @Transient
        var merchanAuthState: Int = -1, //用户商户认证状态 -1-未提交审核 0 - 等待审核 1-未通过认证 2-审核成功
        @Transient
        var merchanAuthBean: MerchanAuthTab? = null, //商户实体 如果用户进行了商户认证 则现实实体
        @Column(columnDefinition = "text")
        var userProfiles: String? = null,//用户个人介绍
        var isDispatcher: Boolean = false //是否为配送员
) {
    override fun toString(): String {
        return "用户实体"
    }
}