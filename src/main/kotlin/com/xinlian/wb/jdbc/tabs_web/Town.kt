package com.xinlian.wb.jdbc.tabs_web

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(indexes = arrayOf(Index(columnList = "countyId")))
data class Town(
        @Id
        var _id: Long,
        var name: String,
        var countyId: Long,
        var townId: Long,
        var agentId: String? = null,//代理人ID
        var isAgent: Boolean = false, //该区域是否已有代理人
        var proportionForUser: Float? = null//与用户的分成比例
) : Serializable