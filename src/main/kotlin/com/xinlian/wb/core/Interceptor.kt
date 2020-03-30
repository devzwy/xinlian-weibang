package com.xinlian.wb.core

import com.xinlian.wb.core.entity.WBException
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Helper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
open class Interceptor : WebMvcConfigurer {

    private val logger = LoggerFactory.getLogger(Interceptor::class.java)


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {

            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
//                reDirect(request,response)
                response.addHeader("Cache-control", "no-cache")
                response.addHeader("Connection", "close")
                response.addHeader("Content-language", "zh-cn")
                response.addHeader("Date", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date()))
                try {
                    logger.info("前置方法被执行 - 请求涞源ip:" + request.remoteAddr + ",(" + request.method + ")调用Api:" + request.requestURL.toString() + ",请求参数：${
                    if (request.method.equals("POST")) Helper.getBodyString(request) else request.parameterMap} ")
                } catch (e: Exception) {
                }
                logger.info("请求路径：" + request.servletPath)
                //OPTION 为适配前端预请求增加
                if (request.method.equals("GET") || request.method.equals("OPTIONS")) return true
                if (request.servletPath.contains("/api/")) {
                    logger.info("调用Api return true")
                    return true
                }
//                for (headerName in request.headerNames) {
//                    logger.info("headerName ->> $headerName")
//                }
                when (request.servletPath) {
                    "/business/getAllSkills",
                    "/business/getMerchantList",
                    "/business/getBanner",
                    "/business/getDynamic",
                    "/business/getMerchantDetail",
                    "/business/getSkillDetail",
                    "/sign/doSign", "/sign/reSetPassword", "/sign/register",
                    "/sign/getMsgCode", "/user/getTags", "/user/getTagsVersion",
                    "/user/getLuckFrawNumber", "/business/payBack1.json",
                    "/business/payBack2.json", "/web/getLuckFrawNumber.web", "/web/getMsgCodeByPhoneNumber.web",
                    "/web/register.web", "/web/login.web", "/error/requestLimit", "/web/upload1", "/v2/swagger-login" -> {
                        return true
                    }
                    else -> {
                        //校验token
                        val userHeaderToken = request.getHeader("token")
                        logger.info("token:$userHeaderToken")
                        if (userHeaderToken == null) {
                            throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "token不能为空")
                        } else {
                            try {
                                val userId = userHeaderToken.split("|")[0]
                                val userToken = userHeaderToken.split("|")[1]

//                                val dbToken = mJedis.get("${Constant.RedisKey.USER_TOKEN}${userId}")
//                                val dbToken=  com.xinlian.wb.other_utils.RedisUtil.get(mJedis,"${Constant.RedisKey.USER_TOKEN}${userId}",1)
                                val dbToken1 = RedisUtil.get("${Constant.RedisKey.USER_TOKEN}${userId}")
                                val dbToken2 = RedisUtil.getServiceToken("${Constant.RedisKey.USER_TOKEN}${userId}")

                                if (((dbToken1 != null) && dbToken1.toString().trim() == userToken.trim())) {
                                    response.addHeader("token", dbToken1.toString())
                                    return true
                                } else if (((dbToken2 != null) && dbToken2.toString().trim() == userToken.trim())) {
                                    response.addHeader("token", dbToken2.toString())
                                    return true
                                } else {
                                    throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "用户身份失效")
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "token格式传入有误")
                            }
                        }

                    }
                }
            }

            override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
//                logger.info("后置方法被执行")
            }

            override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
//                logger.info("最终方法被执行")
            }
        })
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }
//
//    @Throws(IOException::class)
//    fun reDirect(request: HttpServletRequest, response: HttpServletResponse) {
//        // 获取当前请求的路径
//        val basePath = (request.scheme + "://" + request.serverName + ":" + request.serverPort
//                + request.contextPath)
//        // 如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理
//        if ("XMLHttpRequest" == request.getHeader("X-Requested-With")) {
//            // 告诉ajax我是重定向
//            response.setHeader("REDIRECT", "REDIRECT")
//            // 告诉ajax我重定向的路径
//            response.setHeader("CONTENTPATH", "$basePath/login")
//            response.status = HttpServletResponse.SC_ACCEPTED
//        } else {
//            response.sendRedirect("login/do")
//        }
//    }


//    @Configuration
//   open class multipartConfig {
//        @Bean
//        open fun multipartConfigElement(): MultipartConfigElement {
//            val factory = MultipartConfigFactory()
//            val location = System.getProperty("user.dir") + "/data/tmp";
//            val tmpFile = File(location)
//            if (!tmpFile.exists()) {
//                tmpFile.mkdirs()
//            }
//            val maxFileSize = DataSize.of(5, DataUnit.MEGABYTES) //1M
//            val maxRequestSize = DataSize.of(10, DataUnit.MEGABYTES) //1M
//            factory.setLocation(location)
//            factory.setMaxFileSize(maxFileSize)
//            factory.setMaxRequestSize(maxRequestSize)
//            return factory.createMultipartConfig()
//        }
//    }
}
