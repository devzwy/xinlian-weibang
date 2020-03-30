package com.xinlian.wb.core

import org.slf4j.LoggerFactory
import java.util.*
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

@WebListener
class MyApplicationListener : ServletContextListener {
    private val logger = LoggerFactory.getLogger(MyApplicationListener::class.java)
    override fun contextInitialized(sce: ServletContextEvent) {
        val context = sce.servletContext
        // IP存储器
        val ipMap: Map<String, Array<Long>> = HashMap()
        context.setAttribute("ipMap", ipMap)
        // 限制IP存储器：存储被限制的IP信息
        val limitedIpMap: Map<String, Long> = HashMap()
        context.setAttribute("limitedIpMap", limitedIpMap)
//        logger.info("ipmap：" + ipMap.toString() + ";limitedIpMap:" + limitedIpMap.toString() + "初始化成功-----")
    }

    override fun contextDestroyed(sce: ServletContextEvent) {}
}