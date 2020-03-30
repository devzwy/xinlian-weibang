package com.xinlian.wb.core

import com.xinlian.wb.util.Constant.finalParams.IPFILTER_TIME
import com.xinlian.wb.util.Constant.finalParams.LIMIT_NUMBER
import com.xinlian.wb.util.Constant.finalParams.MIN_SAFE_TIME
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 自定义过滤器，用来判断IP访问次数是否超限
 * 如果前台用户访问网站的频率过快，则判定该ip恶意刷新操作，
 * 限制该IP的访问，1小时后自己解除限制
 */
@WebFilter(urlPatterns = ["/sign/getMsgCode", "/sign/register"])
class MyIpFilter : Filter {
    private var config: FilterConfig? = null
    override fun init(filterConfig: FilterConfig) {
        config = filterConfig //设置属性filterConfig
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter.doFilter
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        val context = config!!.servletContext
        val limitedIpMap = context.getAttribute("limitedIpMap") as MutableMap<String, Long>
        filterLimitedIpMap(limitedIpMap)
        val ip = request.remoteHost
        // 判断是否是被限制的IP，如果是则跳到异常页面
        if (isLimitedIP(limitedIpMap, ip) && limitedIpMap[ip]!! - System.currentTimeMillis() > 0) {
            val limitedTime = limitedIpMap[ip]!! - System.currentTimeMillis()
            request.setAttribute("remainingTime", limitedTime / 1000 + if (limitedTime % 1000 > 0) 1 else 0)
            logger.error("IP访问过于频繁=>：$ip")
            request.getRequestDispatcher("/error/requestLimit").forward(request, response)
            return
        }
        // 获取IP存储器
        val ipMap = context.getAttribute("ipMap") as MutableMap<String, Array<Long>>
        // 判断存储器中是否存在当前IP，如果没有则为初次访问，初始化该ip
        // 如果存在当前ip，则验证当前ip的访问次数
        // 如果大于限制阀值，判断达到阀值的时间，如果不大于[用户访问最小安全时间]则视为恶意访问，跳转到异常页面
        if (ipMap.containsKey(ip)) {
            val ipInfo: Array<Long>? = ipMap[ip]
            ipInfo!![0] = ipInfo[0] + 1
            logger.info("当前IP第[" + ipInfo[0] + "]次访问")
            if (ipInfo[0] > LIMIT_NUMBER) {
                val ipAccessTime = ipInfo[1]
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - ipAccessTime <= MIN_SAFE_TIME) {
                    limitedIpMap[ip] = currentTimeMillis + IPFILTER_TIME
                    request.setAttribute("remainingTime", IPFILTER_TIME / 1000 + if (IPFILTER_TIME % 1000 > 0) 1 else 0)
                    logger.error("IP访问过于频繁：$ip")
                    request.getRequestDispatcher("/error/requestLimit").forward(request, response)
                    return
                } else {
                    initIpVisitsNumber(ipMap, ip)
                }
            }
        } else {
            initIpVisitsNumber(ipMap, ip)
            logger.info("首次访问该网站")
        }
        context.setAttribute("ipMap", ipMap)
        chain.doFilter(request, response)
    }

    override fun destroy() {
    }

    /**
     * @param limitedIpMap
     * @Description 过滤受限的IP，剔除已经到期的限制IP
     */
    private fun filterLimitedIpMap(limitedIpMap: Map<String, Long>) {
        val keys: MutableSet<String> = limitedIpMap.keys.toMutableSet()
        val keyIt = keys.iterator()
        val currentTimeMillis = System.currentTimeMillis()
        while (keyIt.hasNext()) {
            val expireTimeMillis = limitedIpMap[keyIt.next()]!!
            if (expireTimeMillis <= currentTimeMillis) {
                keyIt.remove()
            }
        }
    }

    /**
     * @param limitedIpMap
     * @param ip
     * @return true : 被限制 | false : 正常
     * @Description 是否是被限制的IP
     */
    private fun isLimitedIP(limitedIpMap: Map<String, Long>, ip: String): Boolean {
        return if (limitedIpMap.containsKey(ip)) {
            true
        } else false
    }

    /**
     * 初始化用户访问次数和访问时间
     *
     * @param ipMap
     * @param ip
     */
    private fun initIpVisitsNumber(ipMap: MutableMap<String, Array<Long>>, ip: String) {
//        val ipInfo =
//        ipInfo[0] = 0L
//        ipInfo[1] =  // 初
        ipMap[ip] = arrayOf<Long>(0, System.currentTimeMillis())// 访问次数 和初次访问时间
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MyIpFilter::class.java)
    }
}