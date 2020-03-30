package com.xinlian.wb.jdbc.tabs_web

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class WebBalanceNoteTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = 0,
        var userId: String,//流水   对应的对象
        var bType: Int,//0-支出 1-收入 2提现
        var price: Float,//变动的金额
        var toOrFromUserId: String,//流水发生的对象 这里的userId可能为用户ID或代理商ID 如果这里的对象为Admin时表示管理员提现
        var orderId: String? = null,//交易id 技能ID
        var noteDec: String,//交易说明
        var bFType: Int,//流水发生对象的类型 代理商或用户  0-用户 1-代理商
        var createTime: Long = Date().time,
        @Transient
        var userBean: User_Web? = null,
        @Transient
        var toOrFromUserBean: Any? = null
)


/*
 公司生成一级代理商注册码（携带代理区域  A B C）
 用户LLLL使用注册码注册为一级代理商
     一级代理商LLLL分别设置A、B、C、区域的用户分成比例为 20% 30% 50%

     当用户张三在A区域认证成为服务者时默认绑定在一级代理商LLLL名下

     服务者张三技能服务收入金额100

     根据一级代理商LLLL给A区域设置的分成比例计算后，服务者张三得80元

     剩余20元 公司固定抽取30%=6元 剩下14元归代理商所得


 一级代理商LLLL生成二级代理商注册码（该注册码携带与二级代理商YYYY的分成比例50%和代理区域E、F）
 用户YYYY使用二级代理商注册码注册为二级代理商 上级指定为LLLL
    二级代理商YYYY分别设置E、F区域的用户分成比例为 60% 40%

    当用户李四在E区域认证成为服务者时默认绑定在二级代理商YYYY的名下

        服务站李四技能收入100元
        根据二级代理商YYYY给E区域设置的分成比例计算后，服务者李四得40元

        剩余60元 公司固定抽取30%=20元
        剩余40元 根据一级代理商LLLL设置给二级代理商YYYY的分成比例计算后得出 一级代理商LLLL得20元  二级代理商YYYY得20元

 */