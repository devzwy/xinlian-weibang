package com.xinlian.wb.jdbc.tabs

import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

@Entity
data class UserDynamic(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        @Column(nullable = false)
        var userId: String = "",
        @ApiModelProperty(value = "发布类型 0 技能  1服务")
        @Column(nullable = false)
        var bType: Int = 0,
        var bName: String = "")