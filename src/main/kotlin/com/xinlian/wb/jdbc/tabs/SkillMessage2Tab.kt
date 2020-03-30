package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.*

/**
 * 用户技能留言表2
 *
 */
@ApiModel(description = "用户技能留言表2")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class SkillMessage2Tab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @JsonIgnore
        @ApiModelProperty(value = "留言的用户ID")
        var userId: String? = null,
        @ApiModelProperty(value = "留言的用户")
        @Transient
        var user: User? = null,
        @ApiModelProperty(value = "留言内容")
        var content: String = "",
        @ApiModelProperty(value = "留言的时间")
        var messageCreateTime: Long = Date().time,
        @ApiModelProperty(value = "上级刘表")
        @ManyToOne
        @JoinColumn(name = "parentId")
        @JsonIgnore
        var skillMessageTab: SkillMessageTab? = null
)