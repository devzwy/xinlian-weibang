package com.xinlian.wb.jdbc.tabs_web

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

/**
 * web公告栏表
 */
@Entity
data class ShowWebBoradTab(
        @Id
        var id: Long? = -1,
        var title: String = "",//标题
        var content: String = "",//内容
        var creatTime: Long = Date().time,//发布的时间
        var creatUserId: String = ""//发布用户的ID
)