package com.xinlian.wb.core.service.service.impl

import com.xinlian.wb.core.config.OkHttpCli
import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.RequestBean_DoSign
import com.xinlian.wb.core.entity.RequestBean_Register
import com.xinlian.wb.core.entity.WBException
import com.xinlian.wb.core.service.service.ISignService
import com.xinlian.wb.jdbc.repositorys.BalanceRepository
import com.xinlian.wb.jdbc.repositorys.IdentityCardAuthenticationRepository
import com.xinlian.wb.jdbc.repositorys.MerchanAuthRepository
import com.xinlian.wb.jdbc.repositorys.UserRepository
import com.xinlian.wb.jdbc.repositorys_web.CountyRepository
import com.xinlian.wb.jdbc.repositorys_web.TownRepository
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.tabs.Balance
import com.xinlian.wb.jdbc.tabs.User
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Constant.Code.USER_HAS_BEEN_BANNED_CODE
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.decrypt
import com.xinlian.wb.util.ktx.encrypt
import com.xinlian.wb.util.ktx.getErrorRespons
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletResponse


@Service
@Configuration
open class SignServiceImpl : ISignService {

    /**
     * redis操作对象
     *  redis[0] 验证码
     *  redis[1] token
     *  redis[2] 日志
     */


    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mBalanceRepository: BalanceRepository


    @Autowired
    lateinit var okHttpCli: OkHttpCli

    @Autowired
    lateinit var mIdentityCardAuthenticationRepository: IdentityCardAuthenticationRepository


    private val logger = LoggerFactory.getLogger(SignServiceImpl::class.java)

    @Value("\${release}")
    private val isRelease: Boolean? = null

    @Autowired
    lateinit var mMerchanAuthRepository: MerchanAuthRepository


    /**
     * 代理商和管理员表
     */
    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    /**
     * 区 表
     */
    @Autowired
    lateinit var mCountyRepository: CountyRepository

    /**
     * 小镇表
     */
    @Autowired
    lateinit var mTownRepository: TownRepository

    /**
     * 退出登录
     */
    override fun signOut(token: String): HttpResponse<Any> {
        val id = token.split("|")[0]
        val dbToken2 = RedisUtil.getServiceToken("${Constant.RedisKey.USER_TOKEN}${id}")
        val dbToken1 = RedisUtil.get("${Constant.RedisKey.USER_TOKEN}${id}")

        if (Helper.getUserTokenByToken(token).equals(dbToken2)) {
            RedisUtil.delServiceToken("${Constant.RedisKey.USER_TOKEN}${Helper.getUserIdByToken(token)}")
        }

        if (Helper.getUserTokenByToken(token).equals(dbToken1)) {
            RedisUtil.del("${Constant.RedisKey.USER_TOKEN}${Helper.getUserIdByToken(token)}")
        }
        return HttpResponse("")
    }

    /**
     * 校验token
     */
    override fun checkUserToken(token: String): HttpResponse<Any> {
        if (token.isEmpty()) {
            throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "用户token校验失败，请重新登录")
        }
        val userId = Helper.getUserIdByToken(token)
        val token_ = Helper.getUserTokenByToken(token)

        if (userId.isEmpty() || token_.isEmpty()) {
            throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "用户token校验失败，请重新登录")
        }
        val dbToken = RedisUtil.get("${Constant.RedisKey.USER_TOKEN}${userId}")
        val dbToken2 = RedisUtil.getServiceToken("${Constant.RedisKey.USER_TOKEN}${userId}")
        if (((dbToken != null) && dbToken.toString().trim() == token_.trim()) || ((dbToken2 != null) && dbToken2.toString().trim() == token_.trim())) {
            //校验成功
            val user = userRepository.findTop1ByUserId(userId)!!
            user.password = null
            if (user.address == null || user.address?.size == 0) {
                user.address = arrayListOf()
            }
            if (user.indentity != null) {
                user.indentity!!.idemyityCardNumber = user.indentity!!.idemyityCardNumber.toString().encrypt()
                user.indentity!!.idemyityName = user.indentity!!.idemyityName.toString().encrypt()
            }
            user.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(user.userId)?.verifyState ?: -1
            return HttpResponse(user)
        } else {
            throw WBException(code = Constant.Code.USER_AUTH_FAILD_CODE, msg = "用户token校验失败，请重新登录")
        }
    }


    /**
     * 重置密码
     */
    override fun reSetPassword(requestbeanDosign: RequestBean_Register): HttpResponse<Any> {

        val user = userRepository.findTop1ByPhoneNumber(requestbeanDosign.phoneNumber)
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (isRelease != false) {
            //获取该用户发送成功的验证码
            val msgCode_DB = let {
                val mC = RedisUtil.getMsgCode(requestbeanDosign.phoneNumber)
                mC?.toString() ?: ""
            }

            if (msgCode_DB.isEmpty()) {
                //用户未发送过验证码
                return "请发送验证码后再试".getErrorRespons()
            }
            if (!msgCode_DB.equals(requestbeanDosign.msgCode)) {
                //验证码不匹配
                return "验证码校验失败".getErrorRespons()
            }
        }
        if (requestbeanDosign.password.isEmpty()) {
            return "新密码不能为空".getErrorRespons()
        }
        val p = requestbeanDosign.password.decrypt() ?: return "安全校验不通过".getErrorRespons()
        requestbeanDosign.password = p
        user.password = requestbeanDosign.password

        userRepository.saveAndFlush(user)
        return HttpResponse("")

    }

    /**
     * 用户登录
     */
    override fun doSign(requestbeanDosign: RequestBean_DoSign, httpServletResponse: HttpServletResponse): HttpResponse<Any> {

//        加入第三方登录时直接跳过以下逻辑
        if (requestbeanDosign.signType == 2) {

            return doSignFromOtherClient(requestbeanDosign, httpServletResponse)
        }
        if (!Helper.checkPhoneNum(requestbeanDosign.phoneNumber)) {
            return "电话号码格式错误".getErrorRespons()
        }
        var user = userRepository.findTop1ByPhoneNumber(requestbeanDosign.phoneNumber)
        if (requestbeanDosign.signType != 0 && user == null) {
            return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        }
        if (requestbeanDosign.cType != 0 && requestbeanDosign.cType != 1) {
            return "登陆类型参数传入有误".getErrorRespons()
        }

//        //判断用户身份  0为客户端登录  1为web端登录
//        if (requestbeanDosign.userBusinessType == -1) {
//            return HttpResponse(Constant.Code.ERROR__DEFAULT, "A param userBusinessType can not be null", null)
//        }

        if (user != null && user.isBan != false) {
            //用户被封禁
            return "当前用户禁止登录".getErrorRespons(USER_HAS_BEEN_BANNED_CODE)
        }

//        if (user.userBussnessType != requestbeanDosign.userBusinessType) {
//            //web端到客户端登录 或客户端到web端登录 时提示登录类型不匹配
//            return HttpResponse(Constant.Code.ERROR__DEFAULT, "User type checked faild.", null)
//        }

        return doSignFromClient(requestbeanDosign = requestbeanDosign, dbUser2 = user, httpServletResponse = httpServletResponse)
    }

    /**
     * 第三方登录
     */
    private fun doSignFromOtherClient(requestbeanDosign: RequestBean_DoSign, httpServletResponse: HttpServletResponse): HttpResponse<Any> {

        if (requestbeanDosign.openId.isEmpty()) {
            return "openId不能为空".getErrorRespons()
        }
        //查询该用户是否存在
//        userRepository.findAll().forEach {
//            if (requestbeanDosign.openId.equals(it.wx_openId))
//        }
        val user = if (requestbeanDosign.openIdType == 0) {
            userRepository.findTopByWOpenIdx(requestbeanDosign.openId)
                    ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        } else if (requestbeanDosign.openIdType == 1) {
            userRepository.findTopByQOpenIdq(requestbeanDosign.openId)
                    ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        } else {
            return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        }

        if (user.isBan != false) {
            //用户被封禁
            return "当前用户禁止登录".getErrorRespons(USER_HAS_BEEN_BANNED_CODE)
        }
        //登录成功后的用户生成该用户的token
        val token = Helper.toMD5("${user.userId}${Random().nextInt(Int.MAX_VALUE)}")


        if (requestbeanDosign.cType == 0) {
            //客户端登陆
            //将生成的token写入redis
            RedisUtil.set("${Constant.RedisKey.USER_TOKEN}${user.userId}", token)
        }

        if (requestbeanDosign.cType == 1) {
            //服务端登陆
            RedisUtil.insertServiceToken("${Constant.RedisKey.USER_TOKEN}${user.userId}", token)
        }

        httpServletResponse.addHeader("token", token)
        user.password = null
        if (user.address == null || user.address?.size == 0) {
            user.address = arrayListOf()
        }
        if (user.indentity != null) {
            user.indentity!!.idemyityCardNumber = user.indentity!!.idemyityCardNumber.toString().encrypt()
            user.indentity!!.idemyityName = user.indentity!!.idemyityName.toString().encrypt()
        }
        user.token = token
        user.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(user.userId)?.verifyState ?: -1
        return HttpResponse(user)
    }


    private fun doSignFromClient(requestbeanDosign: RequestBean_DoSign, dbUser2: User?, httpServletResponse: HttpServletResponse): HttpResponse<Any> {
        var user = dbUser2
        when (requestbeanDosign.signType) {
            0 -> {
                if (isRelease != false) {
                    //短信登录
                    if (requestbeanDosign.msgCode.isEmpty()) {
                        return "短信登录时验证码不能为空".getErrorRespons()
                    }
                    //发送成功的验证码
                    val msgCodeDB = RedisUtil.getMsgCode(requestbeanDosign.phoneNumber)
                            ?: return "请发送验证码后再试".getErrorRespons()

                    if (!msgCodeDB.equals(requestbeanDosign.msgCode)) {
                        //验证码校验不通过
                        return "验证码校验失败".getErrorRespons()
                    }

                }

                if (user == null) {

                    if (requestbeanDosign.userType == null) {
                        return "userType为空".getErrorRespons()
                    }

                    //注册云信accid和token
                    val registerWyyResult = Helper.registerAccIdToWyy(okHttpCli, requestbeanDosign.phoneNumber)

                    if (registerWyyResult.isSucc) {
                        val uid = UUID.randomUUID().toString()
                        user = User(
                                userId = uid,
                                phoneNumber = requestbeanDosign.phoneNumber,
                                password = "",
                                wyy_accid = registerWyyResult.accid,
                                wyy_token = registerWyyResult.accToken, userType = requestbeanDosign.userType, isBan = false, wOpenIdx = "", qOpenIdq = "")
                        //写入新用户
                        userRepository.saveAndFlush(user)
                        mBalanceRepository.saveAndFlush(Balance(userId = uid))
//                        return HttpResponse(Constant.Code.SUCCESSFUL_CODE, "success", null)
                    } else {
                        //写入失败
                        return "注册失败,${registerWyyResult.errorMsg},请重试".getErrorRespons()
                    }
                }

                //登录成功后的用户生成该用户的token
                val token = Helper.toMD5("${user.userId}${Random().nextInt(Int.MAX_VALUE)}")


                if (requestbeanDosign.cType == 0) {
                    //客户端登陆
                    //将生成的token写入redis
                    RedisUtil.set("${Constant.RedisKey.USER_TOKEN}${user.userId}", token)
                }

                if (requestbeanDosign.cType == 1) {
                    //服务端登陆
                    RedisUtil.insertServiceToken("${Constant.RedisKey.USER_TOKEN}${user.userId}", token)
                }



                httpServletResponse.addHeader("token", token)
                user.password = null
                if (user.address == null || user.address?.size == 0) {
                    user.address = arrayListOf()
                }
                if (user.indentity != null) {
                    user.indentity!!.idemyityCardNumber = user.indentity!!.idemyityCardNumber.toString().encrypt()
                    user.indentity!!.idemyityName = user.indentity!!.idemyityName.toString().encrypt()
                }
                user.token = token
                user.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(user.userId)?.verifyState ?: -1
                return HttpResponse(user)
            }

            1 -> {
                //密码登录
                if (requestbeanDosign.password.isEmpty()) {
                    return "密码不能为空".getErrorRespons()
                }
                val p = requestbeanDosign.password.decrypt() ?: return "安全校验不通过".getErrorRespons()
                requestbeanDosign.password = p
                val user_pwd_checked = userRepository.findUserByPhoneNumberAndPassword(requestbeanDosign.phoneNumber, requestbeanDosign.password)
                        ?: return "密码错误，请重试".getErrorRespons()

                //登录成功后的用户生成该用户的token
                val token = Helper.toMD5("${user_pwd_checked.userId}${Random().nextInt(Int.MAX_VALUE)}")


                if (requestbeanDosign.cType == 0) {
                    //客户端登陆
                    //将生成的token写入redis
                    RedisUtil.set("${Constant.RedisKey.USER_TOKEN}${user_pwd_checked.userId}", token)
                }

                if (requestbeanDosign.cType == 1) {
                    //服务端登陆
                    RedisUtil.insertServiceToken("${Constant.RedisKey.USER_TOKEN}${user_pwd_checked.userId}", token)
                }



                httpServletResponse.addHeader("token", token)
                user_pwd_checked.password = null
                if (user_pwd_checked.address == null || user_pwd_checked.address?.size == 0) {
                    user_pwd_checked.address = arrayListOf()
                }
                if (user_pwd_checked.indentity != null) {
                    user_pwd_checked.indentity!!.idemyityCardNumber = user_pwd_checked.indentity!!.idemyityCardNumber.toString().encrypt()
                    user_pwd_checked.indentity!!.idemyityName = user_pwd_checked.indentity!!.idemyityName.toString().encrypt()
                }
                user_pwd_checked.token = token
                user_pwd_checked.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(user_pwd_checked.userId)?.verifyState
                        ?: -1
                return HttpResponse(user_pwd_checked)
            }

            else -> {
                return "登录类型参数错误".getErrorRespons()
            }
        }
    }

    /**
     * 获取验证码
     * [phoneNumber] 电话号码
     */
    override fun getMsgCode(phoneNumber: String): HttpResponse<Any> {

        if (!Helper.checkPhoneNum(phoneNumber)) {
            //手机号码校验
            return "电话号码格式有误".getErrorRespons()
        }
        if (isRelease != true) {
            //debug模式
            return HttpResponse("")
        }
        //获取验证码
        val mMsgCodeResult = Helper.getMsgCode(okHttpCli, phoneNumber)

        if (mMsgCodeResult.isSucc) {
            //获取成功
            return HttpResponse("")
        } else {
            //获取失败
            return HttpResponse(Constant.Code.ERROR__DEFAULT, mMsgCodeResult.errorMsg, null)
        }
    }


    /**
     * 用户注册
     */
    override fun register(requestbeanRegister: RequestBean_Register, httpServletResponse: HttpServletResponse): HttpResponse<Any> {

//        //注册的用户类型不能为空
//        if (requestbeanRegister.userBusinessType == -1) {
//            return HttpResponse(Constant.Code.ERROR__DEFAULT, "A param userBusinessType can not be null", null)
//        }
        if (requestbeanRegister.userType == null) {
            return "用户类型不能为空".getErrorRespons()
        }

        if (isRelease != false) {
            //获取该用户发送成功的验证码
            val msgCode_DB = RedisUtil.getMsgCode(requestbeanRegister.phoneNumber)?.toString() ?: ""

            if (msgCode_DB.isEmpty()) {
                //用户未发送过验证码
                return "请发送验证码后再试".getErrorRespons()
            }
            if (!msgCode_DB.equals(requestbeanRegister.msgCode)) {
                //验证码不匹配
                return "验证码校验失败".getErrorRespons()
            }

        }

        if (requestbeanRegister.phoneNumber.isEmpty() || requestbeanRegister.password.isEmpty()) {
            //用户名密码为空
            return "用户名或密码为空".getErrorRespons()
        }

        val p = requestbeanRegister.password.decrypt() ?: return "安全校验不通过".getErrorRespons()
        requestbeanRegister.password = p
        if (requestbeanRegister.registerType == null) {
            return "注册类型不能为空".getErrorRespons()
        }

        //手机号码校验
        if (!Helper.checkPhoneNum(requestbeanRegister.phoneNumber)) {
            return "电话号码格式有误".getErrorRespons()
        }
        if ((requestbeanRegister.registerType == 1 || requestbeanRegister.registerType == 2) && requestbeanRegister.openId.isNullOrEmpty()) {
            return "绑定账号时openId不能为空".getErrorRespons()
        }

        if (isUserExist(requestbeanRegister.phoneNumber)) {
            //用户存在
            return "用户已存在".getErrorRespons()
        }

        var boundCountId: Long? = null
        if ((requestbeanRegister.boundCountId != null && mTownRepository.findTop1ByTownId(requestbeanRegister.boundCountId
                        ?: 0L) != null) || (requestbeanRegister.boundCountId != null && mCountyRepository.findTop1ByCountyId(requestbeanRegister.boundCountId
                        ?: 0L) != null)) {
            //判断有没有代理商绑定该区域
            mUser_WebRepository.findAll().forEach { userWeb ->
                val stringArray = userWeb.boundAreaListStr?.split(",")
                if (stringArray?.contains(requestbeanRegister.boundCountId.toString()) == true) {
                    boundCountId = requestbeanRegister.boundCountId
                    return@forEach
                }
            }
        }


        //注册云信accid和token
        val registerWyyResult = Helper.registerAccIdToWyy(okHttpCli, requestbeanRegister.phoneNumber)
        val uid = UUID.randomUUID().toString()

        if (registerWyyResult.isSucc) {
            val u = User(
                    boundCountId = boundCountId,
                    userId = uid,
                    phoneNumber = requestbeanRegister.phoneNumber,
                    password = requestbeanRegister.password,
                    wyy_accid = registerWyyResult.accid,
                    wyy_token = registerWyyResult.accToken, userType = requestbeanRegister.userType, isBan = false, wOpenIdx = if (requestbeanRegister.registerType == 1) requestbeanRegister.openId
                    ?: "" else "", qOpenIdq = if (requestbeanRegister.registerType == 2) requestbeanRegister.openId
                    ?: "" else "")
            //写入新用户
            userRepository.saveAndFlush(u)
            mBalanceRepository.saveAndFlush(Balance(userId = uid))
            if (requestbeanRegister.registerType == 1 || requestbeanRegister.registerType == 2) {
//                登录 0 微信  1QQ
//                注册 1 微信 2 QQ
                //第三方注册
                return doSignFromOtherClient(RequestBean_DoSign(2, "", "", "", requestbeanRegister.openId ?: "",
                        openIdType = requestbeanRegister.registerType!! - 1), httpServletResponse = httpServletResponse)
            }

            return HttpResponse(Constant.Code.SUCCESSFUL_CODE, "success", null)
        } else {
            //写入失败
            userRepository.deleteUserByUserId(uid)
            mBalanceRepository.deleteByUserId(uid)
            return "注册失败,${registerWyyResult.errorMsg},请重试".getErrorRespons()
        }
    }

    /**
     * 判断用户是否存在
     * [phoneNumber] 电话号码
     */
    fun isUserExist(phoneNumber: String): Boolean {
        val user = userRepository.findTop1ByPhoneNumber(phoneNumber)
        return user != null
    }

}
