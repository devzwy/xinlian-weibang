package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

@ApiModel(description = "第一个子标签模型")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class SubSkillTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @ApiModelProperty(value = "第一个子标签ID")
        val subTagId: Long,
        @ApiModelProperty(value = "第一个子标签名称")
        val subTitle: String,
        @ApiModelProperty(value = "第一个子标签平均价格")
        val suggestPrice: Float,
        @ApiModelProperty(value = "第一个子标签单位")
        val tagUnit: String,
        @ApiModelProperty(value = "第一个子标签对应的图片Url")
        val tagImgUrl: String,
        @ApiModelProperty(value = "排序id")
        val orderByNumber: Int,
        @ApiModelProperty(value = "是否为热门技能标签，0 不是  1 是 为1时需要在首页展示")
        var hotTag: Int = 0,
        @ApiModelProperty(value = "父Id")
        @ManyToOne
        @JoinColumn(name = "parentId")
        @JsonIgnore
        val skillParentTab: SkillParentTab,
        @ApiModelProperty(value = "在全部标签中是否显示标签")
        var tagIsShow: Boolean = true
)