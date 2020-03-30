package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.RequestBean_DoSign
import com.xinlian.wb.core.entity.RequestBean_Register
import javax.servlet.http.HttpServletResponse


interface ISignService {

    /**
     * 获取验证码
     * [phoneNumber] 电话号码
     */
    fun getMsgCode(phoneNumber: String): HttpResponse<Any>

    /**
     * 用户注册
     */
    fun register(requestbeanRegister: RequestBean_Register, httpServletResponse: HttpServletResponse): HttpResponse<Any>
//    /**
//     * 第三方登录绑定用户
//     */
//    fun boundAccount(requestbeanRegister: RequestBean_Register): HttpResponse<User>
    /**
     * 用户登录
     */
    fun doSign(requestbeanDosign: RequestBean_DoSign, httpServletResponse: HttpServletResponse): HttpResponse<Any>

    /**
     * 重置密码
     */
    fun reSetPassword(requestbeanDosign: RequestBean_Register): HttpResponse<Any>

    /**
     * 校验token
     */
    fun checkUserToken(token: String): HttpResponse<Any>

    /**
     * 退出登录
     */
    fun signOut(token: String): HttpResponse<Any>


}