package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 需求表
 *
 */
@ApiModel(description = "用户需求表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class Demand(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        var userId: String? = null,//发布需求的用户ID
        var p_tag: Long? = null,//大类ID
        var s_tag: Long? = null,//小类ID
        var serviceTime: Long? = null,//服务时间戳
        var expiryDate: Int? = null,//服务有效期 0 -一天  1 - 7天  2 - 长期有效
        var genderRequirements: Int? = null,//性别要求 0-女 1-男 -1-不限
        var serviceMode: Int? = null,//服务方式 0 TA来找我 1 我去找TA 2 线上服务  3 邮寄
        var demandDescribe: String? = null,//需求描述
        var registrationSkills: String? = null,//已报名用户发布的技能 多个技能用逗号隔开
        var lat: Double? = null,//经纬度
        var lng: Double? = null,//经纬度
        @Transient
        var registrationSkillList: List<UserSkill>? = null,//已报名技能列表 用来返回给前端
        @Transient
        var user: User? = null,//发布需求的用户实体
        @Transient
        var pTagBean: SkillParentTab? = null,
        @Transient
        var sTagBean: SubSkillTab? = null,
        var createTime: Long? = Date().time,
        var demandState: Int? = null, //需求状态 0-等待报名 1-服务中 2 服务完成  -1 已取消 -2 已过期
        var isBan: Boolean = false //是否被封禁

)