package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 抽奖表 抽奖物品
 */
@Entity
@ApiModel(description = "抽奖表 抽奖物品")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
data class LuckDrawTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0,
        @ApiModelProperty(value = "奖品名称")
        val luckDrawName: String,
        @ApiModelProperty(value = "中奖概率 luckDrawProbability%")
        val luckDrawProbability: Int)