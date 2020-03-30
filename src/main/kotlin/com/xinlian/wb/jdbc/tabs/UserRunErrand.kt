package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 用户跑腿代办 同城配送表
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class UserRunErrand(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        var bType: Int? = null,//类型 0 - 跑腿代办  1 - 同城配送
        var intraType: Int? = null,//同城配送的订单类型 0-帮我取 1-帮我送
        var userId: String? = null,//买方userID
        var tradeNames: String? = null,//商品名称
        var tempoDiServizio: Long? = null, //送达时间 -1为尽快送达 其他时间请传时间戳
        var businessAddressId: Long? = null,//跑腿代办中传购买地址ID  同城配送传取货地址ID
        var getShopTimeLong: Long? = null,//取货时间 同城配送时有值 -1表示尽快取
        var sendLat: Double? = 999.0,//发单人的经纬度
        var sendLng: Double? = 999.0,//发单人的经纬度
        var addresId: Long? = null,//收货地址ID
        var price: Float? = null,//配送费
        var decInfo: String? = null,//备注
        @Transient
        var businessAddressBean: Address? = null,//跑腿代办中为购买地址  同城配送为取货地址
        @Transient
        var addresBean: Address? = null,//收货地址
        var createTime: Long = Date().time,
        @Transient
        var user: User? = null, //用户实体
        @Transient
        var wbOrder: WBOrder? = null //对应的订单实体
)