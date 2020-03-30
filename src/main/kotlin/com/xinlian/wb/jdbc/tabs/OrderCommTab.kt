package com.xinlian.wb.jdbc.tabs

import javax.persistence.*

/**
 * 订单评价表
 */
@Entity
data class OrderCommTab(@Id
                        @GeneratedValue(strategy = GenerationType.AUTO)
                        var id: Long? = 0,
        //订单号
                        var orderId: String,
        //买方评价内容
                        var buyCommContent: String? = "",
        //卖方评价内容
                        var serverCommContent: String? = "",
        //买家评价时间
                        var commTimeFormBuy: Long? = null,
        //卖家评价时间
                        var commTimeFormServer: Long? = null,
        //买家打分
                        var commStarFromBuy: Int = 0,
        //卖家打分
                        var commStarFromServer: Int = 0,
        //评价的图片
                        var imgUrls: String? = null,
        //技能编号
                        var skillId: Long? = 0,
        //买家编号
                        var userIdFromBuy: String? = "",
                        @Transient
                        var user: User? = null
)
