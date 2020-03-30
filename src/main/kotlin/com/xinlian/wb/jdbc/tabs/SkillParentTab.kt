package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

/**
 * 技能标签最大表
 */
@ApiModel(description = "技能父类表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class SkillParentTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @ApiModelProperty(value = "父标签ID")
        val parentId: Long,
        @ApiModelProperty(value = "父标签名称")
        val parentTagName: String,
        @ApiModelProperty(value = "排序")
        val orderByNumber: Int,
        @ApiModelProperty(value = "第一个子类标签集合")
        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
        @JoinColumn(name = "parentId")
        var subSkills: List<SubSkillTab>? = null

)