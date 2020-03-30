package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.util.*
import javax.persistence.*

@ApiModel(description = "用户技能表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class UserSkill(@Id
                     @ApiIgnore
                     @GeneratedValue(strategy = GenerationType.AUTO)
                     var id: Long? = null,
                     @ManyToOne(fetch = FetchType.EAGER)
                     @JoinColumn(name = "userId")
                     var user: User? = null,
                     @ApiModelProperty("服务介绍")
                     @Column(columnDefinition = "TEXT")
                     val serviceDec: String,
                     @ApiModelProperty("图片地址 多个地址用 |隔开 编辑技能时会一起返回")
                     @Column(columnDefinition = "text")
                     val imgsUrl: String,
//                     @ApiModelProperty("单位")
//                     val unitStr: String,
//                     @ApiModelProperty("库存")
//                     val stockNumber: Int? = 999,
                     @ApiModelProperty("经纬度")
                     val lat: Double = 999.0,
                     @ApiModelProperty("经纬度")
                     val lng: Double = 999.0,
                     @ApiModelProperty("该技能对应的标签最大类ID")
                     val p_tag: Long? = 0L,
                     @ApiModelProperty("该技能对应的弗标签类ID")
                     val s_tag: Long? = 0L,
                     @ApiModelProperty("该技能类型 0 TA来找我 1 我去找TA 2 线上服务  3 邮寄")
                     val skill_type: Int? = -1,
                     @ApiModelProperty("距离")
                     var distance: Long? = 0L,
                     var title: String,//技能标题
                     @ApiModelProperty("发布该技能的用户是否已被当前用户收藏")
                     @Transient
                     var isLiked: Boolean? = false,
                     @ApiModelProperty("该技能是否已被当前用户收藏")
                     @Transient
                     var liked_skill: Boolean? = false,
                     @ApiModelProperty("是否接受被自动报名 如有人发布该类型需求时自动将该技能报名")
                     var autoRegistration: Boolean? = true,
                     @ApiModelProperty("技能留言列表")
                     @Transient
                     var messageList: List<SkillMessageTab>? = null,
                     @Transient
                     var specsList: List<SpecsTabBean>? = null,//服务规格
                     @Transient
                     var pTagBean: SkillParentTab? = null,
                     @Transient
                     var sTagBean: SubSkillTab? = null,
                     @ApiModelProperty("技能评价列表")
                     @Transient
                     var commList: List<OrderCommTab>? = null,
                     @ApiModelProperty("图文介绍的图片地址 多个地址用 |隔开 编辑技能时会一起返回")
                     @Column(columnDefinition = "text")
                     var contentImages: String? = null,
                     var create_time: Long? = Date().time,//生成时间
                     var isBan: Boolean = false //是否封禁
)
