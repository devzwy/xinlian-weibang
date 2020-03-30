package com.xinlian.wb.jdbc.tabs_web

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class WebTabTitleBean(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = 0,//id
        var title: String = "",//显示的标题
        var url: String = "",//跳转连接
        var permission: Int = -1,//权限 0 系统管理员加载  1 一级代理商显示  2 二级代理商显示 3 三级代理商显示
        var parentTabId: Long? = null,//上级TabID
        @Transient
        var subTabList: List<WebTabTitleBean>? = arrayListOf() //二级列表
)