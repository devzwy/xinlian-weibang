package com.xinlian.wb.redis

import java.util.*

data class LogBean(
        val id: String = UUID.randomUUID().toString(),
        //日志产生时间
        val createTime: String,
        //日志级别
        val logLever: Int,
        //异常名称
        val exceptionName: String,
        //异常内容
        val exceptionContent: String,
        //发生的行数
        val lineNumber: String,
        //是否已经解决 0 默认待处理状态   1 已处理状态
        var exceptionState: Int

)