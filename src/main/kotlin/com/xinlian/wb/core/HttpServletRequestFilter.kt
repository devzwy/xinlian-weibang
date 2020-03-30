package com.xinlian.wb.core

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@WebFilter(urlPatterns = ["/*"])
class HttpServletRequestFilter : Filter {
    private val mLogger = LoggerFactory.getLogger(HttpServletRequestFilter::class.java)

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {

        var requestWrapper: ServletRequest? = null
        if (servletRequest is HttpServletRequest && servletResponse is HttpServletResponse) {
//            servletResponse.setHeader("Access-Control-Allow-Origin", servletRequest.getHeader("Origin"))
            servletResponse.setHeader("Access-Control-Allow-Origin", servletRequest.getHeader("Origin"))
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true")
            servletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
            servletResponse.setHeader("Access-Control-Max-Age", "3600")
            servletResponse.setHeader("Access-Control-Expose-Headers", "Content-Type, x-requested-with, X-Custom-Header, Authorization,name,session,userId,token")
            servletResponse.setHeader("Auther", "Jason")
            servletResponse.setHeader("Contact", "dev_zwy@aliyun.com")
            servletResponse.setHeader("PersonalHomePage", "https://www.github.com/devzwy")


            servletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, Authorization,name,session,userId,token")
            requestWrapper = BodyReaderHttpServletRequestWrapper(servletRequest)
        }
        //获取请求中的流如何，将取出来的字符串，再次转换成流，然后把它放入到新request对象中
        // 在chain.doFiler方法中传递新的request对象
        if (null == requestWrapper) {
            filterChain.doFilter(servletRequest, servletResponse)
        } else {
            filterChain.doFilter(requestWrapper, servletResponse)
        }
    }

    override fun destroy() {

    }
}