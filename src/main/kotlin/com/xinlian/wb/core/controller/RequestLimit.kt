package com.xinlian.wb.core.controller

import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.util.Constant.Code.IP_FILETER_CODE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class RequestLimit {
    /**
     * 对高频ip限制，携带剩余限制时间
     *
     * @param request
     * @return
     */
    @RequestMapping("/error/requestLimit")
    fun requestLimitTime(request: HttpServletRequest): HttpResponse<*> {
        val limitTime = request.getAttribute("remainingTime")
        return HttpResponse<Any?>(IP_FILETER_CODE, "IP已被限制，解除限制剩余${limitTime.toString().toInt() / 60}分钟", limitTime)
        //        return ResultTemplate.errorData(5001, "IP已被限制，请稍后在试~", limitTime);
    }
}