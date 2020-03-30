package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.*

/**
 * 用户技能留言表
 *
 */
@ApiModel(description = "用户技能留言表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class SkillMessageTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @ApiModelProperty(value = "留言的用户ID")
        @JsonIgnore
        var userId: String? = null,
        @ApiModelProperty(value = "留言内容")
        var content: String = "",
        @ApiModelProperty(value = "留言的时间")
        var messageCreateTime: Long = Date().time,
        var skillId: String? = null, //一对多
        @ApiModelProperty(value = "二级留言类别")
        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
        @JoinColumn(name = "parentId")
        var subMessageList: List<SkillMessage2Tab>? = null,
        @Transient
        var user: User? = null
)