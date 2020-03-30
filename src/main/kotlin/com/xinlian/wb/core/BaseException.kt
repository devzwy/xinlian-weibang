package com.xinlian.wb.core

import com.xinlian.wb.core.entity.ErrorResponse
import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.WBException
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.redis.LogBean
import com.xinlian.wb.util.ktx.getErrorRespons
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * 错误全局处理
 */
@ControllerAdvice
@RestController
class BaseException : ErrorController {

    private val logger = LoggerFactory.getLogger(BaseException::class.java)


    @Autowired
    private val redisTemplate: RedisTemplate<String, Any>? = null

    //    /**
//     * 页面找不到异常处理
//     */
//    @RequestMapping
//    @ResponseBody
//    fun error(response: HttpServletResponse, request: HttpServletRequest): ErrorResponse {
//        logger.info("请求资源不存在")
//        return ErrorResponse(404, "请求资源不存在")
//    }
    @ResponseBody
    @RequestMapping(path = ["/error"])
    fun error(request: HttpServletRequest?, response: HttpServletResponse): HttpResponse<Any> {
        logger.error("全局拦截错误的错误," + "  错误代码：" + response.status)
        when (response.status) {
            400 -> {
                //必填参数为空 可能的错误原因：请求头为空  请求的参数中有必填字段 没有打？的字段 或者赋了初始值的字段
                return "必填参数为空".getErrorRespons()
            }
            404 -> {
                //请求路径不存在
                return "请求路径不存在".getErrorRespons()
            }
            415 -> {
                //请求头错误
                return "请使用正确的请求头".getErrorRespons()
            }
            500 -> {
                //服务器异常
                return "服务器异常".getErrorRespons()
            }
            else -> {
                return "未知错误".getErrorRespons()
            }
        }
    }

    /**
     * 页面访问异常，处理失败
     */
    @ExceptionHandler(value = [Exception::class])
    fun jsonErrorHandler(req: HttpServletRequest, e: Exception): ErrorResponse {
        when {
            e is HttpRequestMethodNotSupportedException -> {
                return ErrorResponse(-1, "请求方式有误或请求的接口不存在")
            }
            e is HttpMessageNotReadableException || e is MissingServletRequestParameterException -> {
                return ErrorResponse(-1, "传入参数有误")
            }
            e is WBException -> {
                return ErrorResponse(e.code, e.msg)
            }
            else -> {
                logger.error("服务器发生异常,", e)
                val stringBuffer = StringBuffer()
                val stringBuffer_line = StringBuffer()
                for (e1 in e.stackTrace) {
                    val claname = e1.className
                    if (claname.contains("xinlian")) {
                        stringBuffer.append(claname).append(",")
                        stringBuffer_line.append(e1.lineNumber).append(",")
                    }
                }
                RedisUtil.insertLog(LogBean(createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()), logLever = 0, exceptionName = e.message.toString(), exceptionContent = if (stringBuffer.length > 1) stringBuffer.substring(0, stringBuffer.length - 1) else "未找到", lineNumber = if (stringBuffer_line.length > 1) stringBuffer_line.substring(0, stringBuffer_line.length - 1) else "未找到", exceptionState = 0))

                return ErrorResponse(-1, "服务器异常")
            }
        }
    }

    /**
     * Returns the path of the error page.
     * @return the error path
     */
    override fun getErrorPath(): String = "/error"

}