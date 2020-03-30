package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@ApiModel(description = "收藏表")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
data class LikeTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @ApiModelProperty(value = "当前用户ID")
        var userId: String? = null,
        @ApiModelProperty(value = "收藏的用户ID")
        var likedUserId: String? = null

)