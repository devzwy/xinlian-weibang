package com.xinlian.wb.jdbc.tabs_web

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.xinlian.wb.util.Constant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class User_Web(
        @Id
        @Column(nullable = false)
        var userId: String = "",//用户ID
        @Column(nullable = false)
        var phoneNumber: String = "",//用户电话号码
        @Column(nullable = false)
        @JsonIgnore
        var pwd: String = "",//用户密码
        var userName: String? = "威帮Web用户",//用户名
        var sex: Int? = -1,//性别  -1 未知 男 1 女 0
        var userLogo: String? = Constant.finalParams.DEFULT_USER_LOGO_URL,//用户头像
        var userType: Int = -1,//用户类型 0系统管理员  1-一级代理商  2 - 二级代理商
        var realName: String = "",//用户真实姓名
        var superAgentId: String? = "999",//上级ID 管理员上级为空  一级代理商上级999 代表公司 其他情况填上级的用户ID
        var proportion: Float? = null,//与上级的分成比例
        var balanceAll: Float? = 0.0f,//账户总余额
        var free_balance: Float? = 0.0f,//冻结金额
        var balance_geted: Float? = 0.0f,//已提取金额
        var balance: Float? = 0.0f,//可用余额
        var boundAreaListStr: String? = null,//当前代理商代理的地区ID 多个用逗号隔开
        var isBan: Boolean? = false,//用户是否被封禁 0表示封禁
        @Transient
        var token: String? = null,
        @Transient
        var boundAreaListBean: String? = null
)
