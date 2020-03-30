package com.xinlian.wb.jdbc.tabs

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class SpecsTabBean(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var specsId: Long? = null,//规格ID
        var skillId: Long? = null,//技能ID
        var specsTitle: String? = null,//规格标题
        var specsPrice: Float? = null,//规格对应的价格
        var createTime: Long = Date().time,//规格创建的时间
        var createUserId: String? = null,//创建规格的用户ID
        var unit: String? = null//规格单位
)