package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 系统通知
 *
 */
@ApiModel(description = "系统通知")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class SystemNotification(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @ApiModelProperty(value = "唯一ID")
        var id: Long? = null,

        @ApiModelProperty(value = "用户ID")
        var userId: String,

        @ApiModelProperty(value = "记录创建时间")
        var createTime: Long? = Date().time,

        @ApiModelProperty(value = "通知标题")
        var notificationTitle: String? = null,

        @ApiModelProperty(value = "通知内容")
        var notificationContent: String? = null,

        @ApiModelProperty(value = "是否已读")
        var isRead: Boolean? = false,

        @Transient
        var user: User? = null
)