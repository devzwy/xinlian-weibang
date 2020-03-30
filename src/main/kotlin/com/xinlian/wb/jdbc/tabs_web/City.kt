package com.xinlian.wb.jdbc.tabs_web

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(indexes = arrayOf(Index(columnList = "provinceId")))
data class City(
        @Id
        var _id: Long,
        var name: String,//名称
        var cityId: Long,//市ID
        var provinceId: Long,//省 ID
        @Transient
        var countyList: List<County>? = null
) : Serializable