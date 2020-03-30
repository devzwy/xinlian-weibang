package com.xinlian.wb.jdbc.tabs_web

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Province(
        @Id
        var _id: Long,
        var name: String,
        var provinceId: Long,
        @Transient
        var cityList: List<City>? = null
) : Serializable