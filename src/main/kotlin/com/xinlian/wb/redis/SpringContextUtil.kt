package com.xinlian.wb.redis

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

@Service
class SpringContextUtil : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContextUtil.context = applicationContext
    }

    companion object {
        private var context: ApplicationContext? = null
        fun <T> getBean(name: String, requiredType: Class<T>): T {
            return context!!.getBean(name, requiredType)
        }
    }
}