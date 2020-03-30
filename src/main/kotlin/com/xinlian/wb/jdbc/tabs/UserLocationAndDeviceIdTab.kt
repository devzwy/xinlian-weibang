package com.xinlian.wb.jdbc.tabs

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 用户地理位置和推送id表
 *
 */
@Entity
data class UserLocationAndDeviceIdTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = 0,
        var userId: String? = null,//用户ID
        var lat: Double? = 999.0,//经纬度
        var lng: Double? = 999.0,//经纬度
        var deviceId: String? = ""//推送ID
)