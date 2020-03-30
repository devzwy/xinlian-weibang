package com.xinlian.wb.util

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class TimeAspect {

    // 修正Timer注解的全局唯一限定符
    @Pointcut("@annotation(com.xinlian.wb.util.ApiTimer)")
    private fun pointcut() {
    }

    @Around("pointcut()")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint): Any {
        // 获取目标Logger
        val logger = LoggerFactory.getLogger(joinPoint.target.javaClass)

        // 获取目标类名称
        val clazzName = joinPoint.target.javaClass.name

        // 获取目标类方法名称
        val methodName = joinPoint.signature.name

        val start = System.currentTimeMillis()
        logger.info("进入 [{}] ->> [{}]函数", clazzName, methodName)

        // 调用目标方法
        val result = joinPoint.proceed()

        val time = System.currentTimeMillis() - start
        logger.info("[{}] ->> [{}] 耗时 :  {} ms", clazzName, methodName, time)
        return result
    }
}
