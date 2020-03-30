package com.xinlian.wb.core.service.service.impl

import com.egzosn.pay.ali.bean.AliTransactionType
import com.egzosn.pay.spring.boot.core.MerchantPayServiceManager
import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder
import com.google.gson.Gson
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IOtherService
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.repositorys_web.WebBalanceNoteRepository
import com.xinlian.wb.jdbc.tabs.Demand
import com.xinlian.wb.jdbc.tabs.UserSkill
import com.xinlian.wb.jdbc.tabs.WithDrawalRepository
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant.RedisKey.DEMAND_KEY
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.decrypt
import com.xinlian.wb.util.ktx.getErrorRespons
import com.xinlian.wb.util.ktx.pushMessageAll
import com.xinlian.wb.util.ktx.toJsonStr
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
class OtherServiceImpl : IOtherService {
    private val logger = LoggerFactory.getLogger(OtherServiceImpl::class.java)


    @Autowired
    lateinit var payManager: MerchantPayServiceManager


    @Value("\${release}")
    private val isRelease: Boolean? = null


    @Autowired
    lateinit var mUserRepository: UserRepository

    @Autowired
    lateinit var mDemandRepository: DemandRepository

    @Autowired
    lateinit var mSkillParentTabRepository: SkillParentTabRepository

    @Autowired
    lateinit var mSubSkillTabRepository: SubSkillTabRepository

    @Autowired
    lateinit var mWithDrawalRepository: WithDrawalRepository

    @Autowired
    lateinit var mBalanceRepository: BalanceRepository

    @Autowired
    lateinit var mOrderRepository: OrderRepository

    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    /**
     * 注入的 用户技能表操作对象
     */
    @Autowired
    lateinit var mUserSkillRepository: UserSkillRepository


    /**
     * 余额流水表
     */
    @Autowired
    lateinit var mBalanceNotesRepository: BalanceNotesRepository

    @Autowired
    lateinit var mUserLocationAndDeviceIdTabRepository: UserLocationAndDeviceIdTabRepository

    @Autowired
    lateinit var mUserRunErrandRepository: UserRunErrandRepository

    @Autowired
    lateinit var mMerchanAuthRepository: MerchanAuthRepository

    @Autowired
    lateinit var mSystemNotificationRepository: SystemNotificationRepository

    @Autowired
    lateinit var mWebBalanceNoteRepository: WebBalanceNoteRepository


    /**
     * 获取系统通知
     */
    override fun getSystemNotifications(mReq_GetSystemNotification: Req_GetSystemNotification, token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)
        if (mReq_GetSystemNotification.page == 0) return "请求页数必须从1开始".getErrorRespons()
        val list = mSystemNotificationRepository.findAll(uid, mReq_GetSystemNotification.page - 1, mReq_GetSystemNotification.number)
        val list2 = mSystemNotificationRepository.findAll(uid, mReq_GetSystemNotification.page - 1, mReq_GetSystemNotification.number)
        list.forEach {
            it.user = mUserRepository.findTop1ByUserId(userId = it.userId)
        }
        list2.forEach {
            if (it.isRead == false) {
                //标记已读
                it.isRead = true
                mSystemNotificationRepository.saveAndFlush(it)
            }
        }
        return HttpResponse(list)
    }

    /**
     * 获取全部派送员和商户列表
     */
    override fun getAllDispatcherAndMerchants(mReqtgetAllDispatcherAndMerchants: ReqtgetAllDispatcherAndMerchants, token: String): HttpResponse<Any> {
        if (mReqtgetAllDispatcherAndMerchants.lat == 999.0 || mReqtgetAllDispatcherAndMerchants.lng == 999.0) return "经纬度传入有误".getErrorRespons()
        val list_ = arrayListOf<RespBeanNearUserBean>()
        val list = mUserRepository.findAll().filter {
            val mBean = mMerchanAuthRepository.findTop1ByUserId(it.userId)
            it.merchanAuthState = mBean?.verifyState ?: 0
            it.isDispatcher || (mBean != null && mBean.verifyState == 2)
        }.forEach {
            val lat = mUserLocationAndDeviceIdTabRepository.findTop1ByUserId(it.userId)?.lat
            val lng = mUserLocationAndDeviceIdTabRepository.findTop1ByUserId(it.userId)?.lng
            if ((lat != null && lat != 999.0) && (lng != null && lng != 999.0))
                list_.add(RespBeanNearUserBean(it, lat, lng))
        }
        list_.forEach {
            it.user.indentity = null
            if (it.user.merchanAuthState == 2) {
                it.user.merchanAuthBean = mMerchanAuthRepository.findTop1ByUserId(it.user.userId)
            }
        }
        return HttpResponse(list_)
    }

    /**
     * 根据订单ID获取周围服务者或派送员
     */
    override fun getNearUserList(mRequestBean_getNearUserBean: RequestBean_getNearUserBean, token: String): HttpResponse<Any> {
        val retData = arrayListOf<RespBeanNearUserBean>()
        if (mRequestBean_getNearUserBean.demandId != -1L) {
            //需求订单 查询所有的技能 筛选出技能标签和需求标签对应的技能用户ID
            val demandDBBean = mDemandRepository.findTop1ById(mRequestBean_getNearUserBean.demandId)
                    ?: return "需求不存在".getErrorRespons()
            val uSkill = mUserSkillRepository.findAll().filter { userSkill ->
                return@filter userSkill.s_tag == demandDBBean.s_tag
            }.filter {
                val l = mUserLocationAndDeviceIdTabRepository.findTop1ByUserId(it.user!!.userId)
                (l?.lat != null && l.lat != 999.0) && (l.lng != null && l.lng != 999.0)
            }
            uSkill.forEach {
                val l = mUserLocationAndDeviceIdTabRepository.findTop1ByUserId(it.user!!.userId)!!
                if (Helper.getDistance(demandDBBean.lat, demandDBBean.lng, l.lat, l.lng) / 1000.0 <= 30) {
                    retData.add(RespBeanNearUserBean(mUserRepository.findTop1ByUserId(it.user!!.userId)!!.apply {
                        this.password = null
                        this.address = null
                        this.indentity = null
                        this.token = token
                        this.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(this.userId)?.verifyState ?: -1
                    }, l.lat!!, l.lng!!))
                }
            }
            return HttpResponse(retData)

        }
        val orderDBBean = mOrderRepository.findTop1ByOrderId(mRequestBean_getNearUserBean.orderId ?: "")
                ?: return "订单不存在".getErrorRespons()
//        if (orderDBBean.orderType==0){
//            //技能订单 返回服务者列表
//
//            //根据技能ID查询技能实体
//            val skillDBBean = mUserSkillRepository.findTop1ById(orderDBBean.orderBusinessId?.toLong()?:0)?:return "技能查询失败".getErrorRespons()
//            //根据技能实体中对应的tag类型查询对应的服务者或者商户发布的技能包含该tag的用户实体
//            skillDBBean.s_tag
//            //根据用户id在经纬度表中查询该用户最新的经纬度信息
//
//            //将用户经纬度与该技能中经纬度做计算，筛选出小于等于30km的数据
//
//            //将筛选出的经纬度信息和用户实体组装成新的bean加入返回集合
//
//            //返回数据
//
//        }
        if (orderDBBean.orderType == 1 || orderDBBean.orderType == 2) {
            //跑腿代办同城配送订单 返回派送员列表

            //根据跑腿代办或同城配送ID查询出对应的实体
            val ure = mUserRunErrandRepository.findTop1ById(orderDBBean.orderBusinessId?.toLong() ?: -1)
                    ?: return "业务订单不存在".getErrorRespons()
            //获取跑腿或同城订单的发单经纬度
            val locationList = mUserLocationAndDeviceIdTabRepository.findAll().filter { l ->
                (l?.lat != null && l.lat != 999.0) && (l.lng != null && l.lng != 999.0)
            }

            locationList.forEach {
                if (Helper.getDistance(ure.sendLat, ure.sendLng, it.lat, it.lng) / 1000.0 <= 30) {
                    //使用筛选出的用户ID查询出用户实体
                    val userDBBean = mUserRepository.findTop1ByUserId(it.userId!!)!!
                    //筛选出已经认证为派送员的用户实体
                    if (userDBBean.isDispatcher) {
                        //将筛选出的用户实体和经纬度信息组成新的bean加入返回集合
                        userDBBean.password = null
                        userDBBean.address = null
                        userDBBean.indentity = null
                        userDBBean.token = token
                        userDBBean.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(userDBBean.userId)?.verifyState
                                ?: -1
                        retData.add(RespBeanNearUserBean(userDBBean, it.lat!!, it.lng!!))
                    }
                }
            }
            return HttpResponse(retData)
        }
        return "操作失败，传入需求ID或订单类型找不到".getErrorRespons()

    }

    /**
     * 派送员认证
     */
    override fun dispatcherCertification(token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)
        val user = mUserRepository.findTop1ByUserId(uid)!!
        if (user.isDispatcher) return "请勿重复认证".getErrorRespons()
        if (user.indentity == null && user.merchanAuthState != 2) return "实名认证未完成".getErrorRespons()
        user.isDispatcher = true
        mUserRepository.saveAndFlush(user)
        return HttpResponse("认证成功")
    }

    /**
     * 重置支付密码
     */
    override fun reSetPassword(mRequestBean_SetPsw: RequestBean_SetPsw, token: String): HttpResponse<Any> {
        if (mRequestBean_SetPsw.password.isEmpty() || mRequestBean_SetPsw.newPassword?.isEmpty() ?: true) return "新密码或旧密码不能为空".getErrorRespons()
        val uid = Helper.getUserIdByToken(token)
        val psd_old = mBalanceRepository.findTop1ByUserId(uid)!!
        val psw = try {
            mRequestBean_SetPsw.password.decrypt()
        } catch (e: Exception) {
            return "密码校验失败".getErrorRespons()
        }
        val psw_new = try {
            mRequestBean_SetPsw.newPassword!!.decrypt()
        } catch (e: Exception) {
            return "密码校验失败".getErrorRespons()
        }
        if (psw == null || psw_new == null) return "密码校验失败".getErrorRespons()

        if (psd_old.payPsw?.isNotEmpty() ?: false) {
            //已经设置支付密码 重置密码
            if (psd_old?.payPsw?.equals(psw) == true) {
                //设置
                psd_old?.payPsw = psw_new
                mBalanceRepository.saveAndFlush(psd_old)
//                RedisUtil.delServiceToken("${Constant.RedisKey.USER_TOKEN}${uid}")
//                RedisUtil.del("${Constant.RedisKey.USER_TOKEN}${uid}")
//                logger.info("密码重置成功，清除token")
                return HttpResponse("支付密码重置成功")
            } else {
                return "旧密码校验失败，请重试".getErrorRespons()
            }
        } else {
            return "密码未设置，请设置后再重置".getErrorRespons()
        }
    }

    /**
     * 设置支付密码
     */
    override fun setPassword(mRequestBean_SetPsw: RequestBean_SetPsw, token: String): HttpResponse<Any> {
        if (mRequestBean_SetPsw.password.isEmpty()) return "新密码不能为空".getErrorRespons()
        val psw = try {
            mRequestBean_SetPsw.password.decrypt()
        } catch (e: Exception) {
            return "密码校验失败".getErrorRespons()
        }
        if (psw == null) return "密码校验失败".getErrorRespons()
        val uid = Helper.getUserIdByToken(token)
        val psd_old = mBalanceRepository.findTop1ByUserId(uid)!!
        if (psd_old.payPsw?.isNotEmpty() ?: false) {
            return "密码已设置，请勿重新设置".getErrorRespons()
        } else {
            psd_old?.payPsw = psw
            mBalanceRepository.saveAndFlush(psd_old)
            return HttpResponse("密码设置成功")
        }
    }

    /**
     * 获取余额流水
     */
    override fun getBalanceRecord(mRequestBean_GetBalanceRe: RequestBean_GetBalanceRe, token: String): HttpResponse<Any> {
        if (mRequestBean_GetBalanceRe.bType != 0 && mRequestBean_GetBalanceRe.bType != 1 && mRequestBean_GetBalanceRe.bType != 2) return "请求参数bType字段传入有误".getErrorRespons()
        if (mRequestBean_GetBalanceRe.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }

        when (mRequestBean_GetBalanceRe.bType) {
            0 -> {
                //查询全部流水
                return HttpResponse(mBalanceNotesRepository.findAll(Helper.getUserIdByToken(token), (mRequestBean_GetBalanceRe.page - 1) * mRequestBean_GetBalanceRe.number, mRequestBean_GetBalanceRe.number).toMutableList())
            }

            1 -> {
                //查询收入流水
                return HttpResponse(mBalanceNotesRepository.findAll(Helper.getUserIdByToken(token), 0, (mRequestBean_GetBalanceRe.page - 1) * mRequestBean_GetBalanceRe.number, mRequestBean_GetBalanceRe.number).toMutableList())
            }

            2 -> {
                //查询支出流水
                return HttpResponse(mBalanceNotesRepository.findAll(Helper.getUserIdByToken(token), 1, (mRequestBean_GetBalanceRe.page - 1) * mRequestBean_GetBalanceRe.number, mRequestBean_GetBalanceRe.number).toMutableList())
            }
            else -> {
                return "请求参数bType字段传入有误".getErrorRespons()
            }
        }
    }

    /**
     * 余额充值
     */
    override fun addBalance(mAddBalanceBean: AddBalanceBean, token: String): HttpResponse<Any> {
        if (mAddBalanceBean.doType == null) return "操作类型错误".getErrorRespons()
        if (mAddBalanceBean.price == null || mAddBalanceBean.price!! <= 0.0f) return "充值金额不能为0".getErrorRespons()
        val outTradNo = "${Helper.getUserIdByToken(token)}_${mAddBalanceBean.price.toString()}_${Date().time}"
        when (mAddBalanceBean.doType) {
            0 -> {
                //支付宝支付
                val mMerchantPayOrder = MerchantPayOrder("1",
                        AliTransactionType.APP.name,
                        "余额充值",
                        "充值余额",
                        if (isRelease != false) BigDecimal(mAddBalanceBean.price.toString()) else BigDecimal(0.01),
                        Date().time.toString())
                mMerchantPayOrder.addition = outTradNo
                val aaa = payManager.getOrderInfo(
                        mMerchantPayOrder)
                val bbb = Helper.buildOrderParam(aaa)
                logger.info("生成支付宝支付参数：$bbb")
                return HttpResponse(ResponseBean_Pay(alipayStr = bbb, weChatPayResult = null))
            }

            1 -> {
                //微信支付
                val mMerchantPayOrder = MerchantPayOrder("2", AliTransactionType.APP.name, "余额充值", "充值余额", if (isRelease != false) BigDecimal(mAddBalanceBean.price.toString()) else BigDecimal(0.01), Date().time.toString())
                mMerchantPayOrder.addition = outTradNo
                val str = try {
                    Gson().toJson(payManager.getOrderInfo(mMerchantPayOrder))
                } catch (e: Exception) {
                    return "生成订单失败，请重试".getErrorRespons()
                }
                val bean = Gson().fromJson<WeChatPayResultBean>(str, WeChatPayResultBean::class.java)
                logger.info("生成微信支付参数：$str")
                return HttpResponse(ResponseBean_Pay(alipayStr = null, weChatPayResult = bean))
            }
            else -> {
                return "支付类型错误".getErrorRespons()
            }
        }
    }

    /**
     * 发布需求
     */
    override fun releaseRequirements(mDemand: Demand, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "用户不存在".getErrorRespons()
        //校验必填参数
        if (mDemand.p_tag == null || mSkillParentTabRepository.findTop1ByParentId(parentId = mDemand.p_tag!!) == null) return "大类ID不能为空或不存在".getErrorRespons()
        if (mDemand.s_tag == null || mSubSkillTabRepository.findOneBySubTagId(mDemand.s_tag!!) == null) return "小类ID不能为空或不存在".getErrorRespons()
        if (mDemand.serviceTime == null) return "服务时间戳为空".getErrorRespons()
        if (mDemand.expiryDate == null || (mDemand.expiryDate != 0 && mDemand.expiryDate != 1 && mDemand.expiryDate != 2)) return "服务有效期为空或填写错误".getErrorRespons()
        if (mDemand.genderRequirements == null || (mDemand.genderRequirements != -1 && mDemand.genderRequirements != 0 && mDemand.genderRequirements != 1)) return "性别要求为空或填写错误".getErrorRespons()
        if (mDemand.serviceMode == null || (mDemand.serviceMode != 0 && mDemand.serviceMode != 1 && mDemand.serviceMode != 2 && mDemand.serviceMode != 3)) return "服务方式为空或填写错误".getErrorRespons()
        if (mDemand.demandDescribe == null || mDemand.demandDescribe!!.isEmpty()) return "需求描述为空".getErrorRespons()
        if (mDemand.lat == null || mDemand.lng == null) return "经纬度不能为空".getErrorRespons()

        //发布或者修改需求   只有状态在待接单下才能修改
        if (mDemand.id != null) {
            if (mDemand.demandState != 0) return "当前状态下无法修改需求内容".getErrorRespons()
            val userAllDemands = mDemandRepository.findAll(userId = user.userId)
                    ?: return "修改的需求不存在".getErrorRespons()
            val willUpdataeDemand = mDemandRepository.findTop1ById(mDemand.id!!) ?: return "修改的需求不存在".getErrorRespons()
            userAllDemands.forEach {
                if (it.id == willUpdataeDemand.id) {
                    //修改逻辑
                    mDemand.registrationSkills = willUpdataeDemand.registrationSkills //将已经报名的技能ID复制过去
                    mDemand.userId = willUpdataeDemand.userId
                    mDemandRepository.saveAndFlush(mDemand)
                    return HttpResponse("需求修改成功")
                }
            }
            return "只能修改自己发布的需求".getErrorRespons()
        }
        //发布逻辑
        mDemand.userId = user.userId
        mDemand.demandState = 0
        val d = mDemandRepository.saveAndFlush(mDemand)

        if (mDemand.expiryDate != 2) {
            var expTime = 0
            if (mDemand.expiryDate == 0) {
                //一天过期
                expTime = 86400
            }
            if (mDemand.expiryDate == 1) {
                //七天过期
                expTime = 604800
            }
            RedisUtil.set("$DEMAND_KEY${d.id}", "", expTime.toLong())
        }
        PushData(pType = 2, title = "您有新的订单", content = "${user!!.userName}发布了一个${mSubSkillTabRepository.findOneBySubTagId(d.s_tag!!)?.subTitle}的需求，快来看看吧", bussContent = "${d.id}").toJsonStr().pushMessageAll()
        return HttpResponse("需求发布成功")
    }

    /**
     * 报名需求
     */
    override fun signUpRequirements(mSignDemandBean: SignDemandBean, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "用户不存在".getErrorRespons()

        //即将报名的需求实体
        val willSignUpDemand = mDemandRepository.findTop1ById(mSignDemandBean.demandId)
                ?: return "需求ID为空或需求不存在".getErrorRespons()
        if (willSignUpDemand.demandState != 0) return "该需求已在服务中...".getErrorRespons()
        //即将拿来报名的技能实体
        val willSignUpSkill = mUserSkillRepository.findTop1ById(mSignDemandBean.skillId)
                ?: return "技能不存在".getErrorRespons()

        if (willSignUpDemand.userId.equals(user.userId)) return "不能报名自己的需求".getErrorRespons()

        //判断应邀的技能与需求需要的技能类别是否一致
        if (willSignUpDemand.s_tag != willSignUpSkill.s_tag) return "应邀类别不匹配，请选择其他类别".getErrorRespons()


        //已报名的技能列表字符串
        val signedSkills = willSignUpDemand.registrationSkills

        //查询是否已经报名
        if (signedSkills?.contains(willSignUpSkill.id.toString()) == true) return "不能重复报名".getErrorRespons()

        when (willSignUpDemand.genderRequirements) {
            0 -> {
                //限制为女
                if (user.sex != 0) return "应邀的需求性别要求女".getErrorRespons()
            }
            1 -> {
                //限制为男
                if (user.sex != 1) return "应邀的需求性别要求男".getErrorRespons()
            }
        }
        willSignUpDemand.registrationSkills = if (signedSkills == null) willSignUpSkill.id.toString() else "${signedSkills},${willSignUpSkill.id}"
//        willSignUpDemand.registrationUsers = if (signedUsers == null) user.userId else {
//            signedUsers.split(",").forEach {
//                if (it.isNotEmpty()) {
//                    if (it.equals(user.userId))
//                }
//            }
//            "${willSignUpDemand.registrationUsers},${user.userId}"
//        }
        mDemandRepository.saveAndFlush(willSignUpDemand)
        return HttpResponse("报名成功")
    }

    /**
     * 需求详情
     */
    override fun requirementsDetails(mDemand: Demand, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "用户不存在".getErrorRespons()
        if (mDemand.id == null) return "需求ID为空".getErrorRespons()

        //查询需求实体
        val demand = mDemandRepository.findTop1ById(mDemand.id!!) ?: return "需求不存在".getErrorRespons()
        if (demand.isBan) return "该需求已被管理员禁止显示".getErrorRespons()
        //给需求发布者的用户实体赋值
        demand.user = mUserRepository.findTop1ByUserId(demand.userId!!).apply {
            this?.indentity = null
        }

        val signedSkillList = arrayListOf<UserSkill>()
        if (demand.registrationSkills?.isNotEmpty() == true) {
            //需求已经有技能报名时 索引技能实体列表返回
            demand.registrationSkills!!.split(",").forEach {
                if (it.isNotEmpty()) {
                    signedSkillList.add(mUserSkillRepository.findTop1ById(it.toLong())!!)
                }
            }
            demand.registrationSkillList = signedSkillList
        }

//        if (demand.registrationUsers != null && demand.registrationUsers!!.isNotEmpty()) {
//            //已经有人报名
//            demand.registrationUsers!!.split(",").forEach {
//                if (it.isNotEmpty()) {
//                    l.add(mUserRepository.findTop1ByUserId(it)!!.apply {
//                        indentity = null
//                    })
//                }
//            }
//            demand.registrationUserList = l
//        }
        val st = mSkillParentTabRepository.findTop1ByParentId(demand.p_tag ?: 0) ?: return "标签查询失败".getErrorRespons()
        st.subSkills = null
        demand.pTagBean = st

        demand.sTagBean = mSubSkillTabRepository.findOneBySubTagId(demand.s_tag ?: 0)
                ?: return "标签查询失败".getErrorRespons()
        return HttpResponse(demand)
    }

    /**
    获取我发布的需求列表
     */
    override fun getMyRequirements(mDemand: RequestBean_GetDemands, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "用户不存在".getErrorRespons()
        if (mDemand.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        //没有page 和 number时默认拉取全部数据
        val a = mDemandRepository.findAll(userId = user.userId, page = (mDemand.page - 1) * mDemand.number, number = mDemand.number)
        //组装数据
        a.forEach {
            it.user = mUserRepository.findTop1ByUserId(it.userId!!)
            val signedSkillList = arrayListOf<UserSkill>()
            if (it.registrationSkills?.isNotEmpty() == true) {
                //需求已经有技能报名时 索引技能实体列表返回
                it.registrationSkills!!.split(",").forEach { skillId ->
                    if (skillId.isNotEmpty()) {
                        signedSkillList.add(mUserSkillRepository.findTop1ById(skillId.toLong())!!)
                    }
                }
                it.registrationSkillList = signedSkillList
            }
            val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
            st.subSkills = null
            it.pTagBean = st
            it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
        }
        return HttpResponse(a)

    }

    /**
    获取全部需求列表
     */
    override fun getAllRequirements(mRequestBean_GetDemands: RequestBean_GetDemands, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "用户不存在".getErrorRespons()

        if (mRequestBean_GetDemands.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        var list = listOf<Demand>()

//        if (mRequestBean_GetDemands.lat == null || mRequestBean_GetDemands.lng == null) {
//            //不排序
//            val pageable = PageRequest.of(mRequestBean_GetDemands.page - 1, mRequestBean_GetDemands.number);
//
//            list = mDemandRepository.findAll(pageable).content.filter {
//                it.user = mUserRepository.findTop1ByUserId(it.userId!!)
//                val signedSkillList = arrayListOf<UserSkill>()
//                if (it.registrationSkills?.isNotEmpty() == true) {
//                    //需求已经有技能报名时 索引技能实体列表返回
//                    it.registrationSkills!!.split(",").forEach { skillId ->
//                        if (skillId.isNotEmpty()) {
//                            signedSkillList.add(mUserSkillRepository.findTop1ById(skillId.toLong())!!)
//                        }
//                    }
//                    it.registrationSkillList = signedSkillList
//                }
//                val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
//                        ?: return "标签查询失败".getErrorRespons()
//                st.subSkills = null
//                it.pTagBean = st
//
//                it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
//                        ?: return "标签查询失败".getErrorRespons()
//                !it.isBan
//            }
//        } else {
        //使用距离排序
        list = mDemandRepository.findAll(mRequestBean_GetDemands.lat, mRequestBean_GetDemands.lng, (mRequestBean_GetDemands.page - 1) * mRequestBean_GetDemands.number, mRequestBean_GetDemands.number).filter {
            it.user = mUserRepository.findTop1ByUserId(it.userId!!)
            val signedSkillList = arrayListOf<UserSkill>()
            if (it.registrationSkills?.isNotEmpty() == true) {
                //需求已经有技能报名时 索引技能实体列表返回
                it.registrationSkills!!.split(",").forEach { skillId ->
                    if (skillId.isNotEmpty()) {
                        signedSkillList.add(mUserSkillRepository.findTop1ById(skillId.toLong())!!)
                    }
                }
                it.registrationSkillList = signedSkillList
            }
            val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
            st.subSkills = null
            it.pTagBean = st

            it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
            !it.isBan
//            }
        }

        return HttpResponse(list)
    }

}
