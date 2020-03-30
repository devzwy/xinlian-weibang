package com.xinlian.wb.jdbc.tabs_web

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class RegisterCode(
        @Id
        var registerId: String = UUID.randomUUID().toString(),
        var creatUserId: String,//生成的对象 用户ID 可能为管理员或一级代理商
        var boundAreaListSrt: String,//该注册码绑定的代理区域 多个地区用逗号隔开 可能为城市ID或者镇区ID
        var proportion: Float? = null,//分成比例 生成一级代理商时为空 生成二级代理商时表示与二级代理商的分成比例 生成二级代理商时必填
        var creatTime: Long = Date().time,//生成的时间
        var registerCodeType: Int,//注册码类型 0-一级代理商注册码  1-二级代理商注册码
        @Transient
        var boundAreaList: List<BoundAreaBean>? = null
)

data class BoundAreaBean(val name: String, val code: Long)