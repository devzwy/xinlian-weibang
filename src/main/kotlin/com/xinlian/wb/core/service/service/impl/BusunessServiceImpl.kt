package com.xinlian.wb.core.service.service.impl

import com.egzosn.pay.ali.bean.AliTransactionType
import com.egzosn.pay.spring.boot.core.MerchantPayServiceManager
import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder
import com.google.gson.Gson
import com.xinlian.wb.core.config.OkHttpCli
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IBusunessService
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.repositorys_web.CountyRepository
import com.xinlian.wb.jdbc.repositorys_web.TownRepository
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.repositorys_web.WebBalanceNoteRepository
import com.xinlian.wb.jdbc.tabs.*
import com.xinlian.wb.jdbc.tabs_web.WebBalanceNoteTab
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.Helper.getUserIdByToken
import com.xinlian.wb.util.ktx.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Service
@Configuration
open class BusunessServiceImpl : IBusunessService {

    private val logger = LoggerFactory.getLogger(BusunessServiceImpl::class.java)

    /**
     * 注入的 Banner表操作对象
     */
    @Autowired
    lateinit var mBannerRepository: BannerRepository


    /**
     * 注入的 User表操作对象
     */
    @Autowired
    lateinit var mUserRepository: UserRepository


    /**
     * 注入的 首页动态表操作对象
     */
    @Autowired
    lateinit var mDynamicRepository: DynamicRepository

    /**
     * 注入的 第一个子标签表操作对象
     */
    @Autowired
    lateinit var mSubSkillTabRepository: SubSkillTabRepository

    /**
     * 注入的 用户技能表操作对象
     */
    @Autowired
    lateinit var mUserSkillRepository: UserSkillRepository

    /**
     * 注入的 收藏用户表操作对象
     */
    @Autowired
    lateinit var mLikeTabRepository: LikeTabRepository

    /**
     * 注入的 收藏技能表操作对象
     */
    @Autowired
    lateinit var mLikeSkillTabRepository: LikeSkillTabRepository

    /**
     * 注入的 技能留言表操作对象
     */
    @Autowired
    lateinit var mSkillMessageRepository: SkillMessageRepository

    /**
     * 注入的 技能留言的回复表
     */
    @Autowired
    lateinit var mSkillMessageRepository2: SkillMessage2Repository

    /**
     * 用户表
     */
    @Autowired
    lateinit var userRepository: UserRepository

    /**
     * 注入的 订单表操作对象
     */
    @Autowired
    lateinit var mOrderRepository: OrderRepository


    /**
     * 用户余额表
     */
    @Autowired
    lateinit var mBalanceRepository: BalanceRepository


    /**
     * 余额流水表
     */
    @Autowired
    lateinit var mBalanceNotesRepository: BalanceNotesRepository

    /**
     * 送货地址表
     */
    @Autowired
    lateinit var mAddressRepository: AddressRepository


    @Autowired
    lateinit var payManager: MerchantPayServiceManager

    @Autowired
    lateinit var mUserLocationAndDeviceIdTabRepository: UserLocationAndDeviceIdTabRepository

    /**
     * 新连与用户或代理商的分成比例
     */
    @Autowired
    lateinit var mCommissionAllocationRepository: CommissionAllocationRepository


    /**
     * 代理商和管理员流水
     */
    @Autowired
    lateinit var mWebBalanceNoteRepository: WebBalanceNoteRepository


    @Value("\${release}")
    private val isRelease: Boolean? = null

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
     * 代理商和管理员表
     */
    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    /**
     * 订单评价表
     */
    @Autowired
    lateinit var mOrderCommRepository: OrderCommRepository

    /**
     * 服务规格表
     */
    @Autowired
    lateinit var mSpecsTabRepository: SpecsTabRepository

    /**
     * 大标签操作表
     */
    @Autowired
    lateinit var mSkillParentTabRepository: SkillParentTabRepository


    /**
     * 跑腿代办表
     */
    @Autowired
    lateinit var mUserRunErrandRepository: UserRunErrandRepository


    /**
     * 跑腿代办表
     */
    @Autowired
    lateinit var mMerchanAuthRepository: MerchanAuthRepository

    @Autowired
    lateinit var okHttpCli: OkHttpCli

    /**
     * 更新用户地理位置和token
     */
    override fun updataUserLocationAndDeviceId(reqBean: UserLocationAndDeviceIdTab, token: String): HttpResponse<Any> {
        val uid = getUserIdByToken(token)

        val uAddressAndDeviceIdBean = mUserLocationAndDeviceIdTabRepository.findTop1ByUserId(uid)
                ?: UserLocationAndDeviceIdTab(userId = uid)
        if (reqBean.deviceId != null && reqBean.deviceId!!.isNotEmpty()) {
            //写入 deviceId
            uAddressAndDeviceIdBean.deviceId = reqBean.deviceId
            mUserLocationAndDeviceIdTabRepository.saveAndFlush(uAddressAndDeviceIdBean)
        }
        if (reqBean.lat != null && reqBean.lat != 999.0 && reqBean.lng != null && reqBean.lng != 999.0) {
            //写入 lat
            uAddressAndDeviceIdBean.lat = reqBean.lat
            uAddressAndDeviceIdBean.lng = reqBean.lng
            mUserLocationAndDeviceIdTabRepository.saveAndFlush(uAddressAndDeviceIdBean)
            val u_db = mUserRepository.findTop1ByUserId(uid)
            if (u_db!!.boundCountId == null) {
                //绑定代理商
                Helper.getCityInformation(okHttpCli, reqBean.lng, reqBean.lat).let { boundCityBean ->
                    //判断有没有代理商绑定该区域
                    mUser_WebRepository.findAll().forEach { userWeb ->
                        val stringArray = userWeb.boundAreaListStr?.split(",")
                        stringArray?.forEach { boundID ->
                            if (boundID.isNotEmpty() && boundID.toLong() == boundCityBean?.town_id) {
                                u_db.boundCountId = boundCityBean!!.town_id
                                mUserRepository.saveAndFlush(u_db)
                                return HttpResponse("")
                            }
                            if (boundID.isNotEmpty() && boundID.toLong() == boundCityBean?.county_id) {
                                u_db.boundCountId = boundCityBean!!.county_id
                                mUserRepository.saveAndFlush(u_db)
                                return HttpResponse("")
                            }

                        }
                    }
                }
            }
        }
        return HttpResponse("")
    }

    /**
     * 获取订单列表
     */
    override fun getOrderList(mReq_GetMyOrderList: Req_GetMyOrderList, token: String): HttpResponse<Any> {
        val uid = getUserIdByToken(token)
        //区分是服务端还是客户端v\
        val clientTokem = uid + "|" + RedisUtil.get("${Constant.RedisKey.USER_TOKEN}${uid}")?.toString()?.trim()
        val isServicePerson = !clientTokem.equals(token.trim())
        //服务端 查询我服务的订单
        //客户端  查询我购买的订单
        if (mReq_GetMyOrderList.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }


        var allOrder: List<WBOrder>

        when (mReq_GetMyOrderList.state) {
            -1 -> {
//                val pageable = PageRequest.of(mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number, Sort.Order.desc("orderCreateTime").withProperties("orderCreateTime"));
                //查询自己的 区分服务端和客户端
                allOrder = if (isServicePerson) {
                    mOrderRepository.findAllByUserIdFromServer(uid, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                } else {
                    mOrderRepository.findAll(uid, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                }
                //查询别人的 不区分客户端和服务端
                if (mReq_GetMyOrderList.userId != null) {
                    if (userRepository.findTop1ByUserId(mReq_GetMyOrderList.userId) == null) return "用户ID传入有误".getErrorRespons()
                    if (mReq_GetMyOrderList.buyOrServers == -1) return "请传入区分买卖方字段".getErrorRespons()
                    if (mReq_GetMyOrderList.buyOrServers == 0) {
                        allOrder = mOrderRepository.findAll(mReq_GetMyOrderList.userId, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                    } else if (mReq_GetMyOrderList.buyOrServers == 1) {
                        allOrder = mOrderRepository.findAllByUserIdFromServer(mReq_GetMyOrderList.userId, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                    } else {
                        return "区分买卖方字段传入错误".getErrorRespons()
                    }
//                    allOrder = mOrderRepository.findAllByUserIdFromBuyOrUserIdFromServer(mReq_GetMyOrderList.userId, mReq_GetMyOrderList.userId, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                }
            }
            else -> {
//                allOrder = mOrderRepository.findAllByState(mReq_GetMyOrderList.state)
                //查询自己的 区分服务端和客户端
                allOrder = if (isServicePerson) {
                    mOrderRepository.findAllByUserIdFromServer(uid, mReq_GetMyOrderList.state, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                } else {
                    mOrderRepository.findAll(uid, mReq_GetMyOrderList.state, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                }
                //查询别人的 不区分客户端和服务端
                if (mReq_GetMyOrderList.userId != null) {
                    if (userRepository.findTop1ByUserId(mReq_GetMyOrderList.userId) == null) return "用户ID传入有误".getErrorRespons()
                    if (mReq_GetMyOrderList.buyOrServers == 0) {
                        allOrder = mOrderRepository.findAll(mReq_GetMyOrderList.userId, mReq_GetMyOrderList.state, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                    } else if (mReq_GetMyOrderList.buyOrServers == 1) {
                        allOrder = mOrderRepository.findAllByUserIdFromServer(mReq_GetMyOrderList.userId, mReq_GetMyOrderList.state, mReq_GetMyOrderList.page - 1, mReq_GetMyOrderList.number)
                    } else {
                        return "区分买卖方字段传入错误".getErrorRespons()
                    }
                }
            }
        }
        allOrder.forEach {
            logger.info("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it.orderCreateTime)}")
            val userFromServer = mUserRepository.findTop1ByUserId(it.userIdFromServer)
            if (userFromServer?.indentity != null) {
                userFromServer.indentity!!.idemyityCardNumber = userFromServer.indentity!!.idemyityCardNumber.toString().encrypt()
                userFromServer.indentity!!.idemyityName = userFromServer.indentity!!.idemyityName.toString().encrypt()
            }
            val uMAuth = mMerchanAuthRepository.findTop1ByUserId(userFromServer?.userId ?: "")
            if (uMAuth != null) {
                //当前用户已通过商户认证
                userFromServer?.merchanAuthBean = uMAuth
                //0 -等待审核  1-审核拒绝 2-审核成功
                userFromServer?.merchanAuthState = uMAuth.verifyState ?: 0
            }


            val userFromBuy = mUserRepository.findTop1ByUserId(it.userIdFromBuy ?: "")
            if (userFromBuy?.indentity != null) {
                userFromBuy.indentity!!.idemyityCardNumber = userFromBuy.indentity!!.idemyityCardNumber.toString().encrypt()
                userFromBuy.indentity!!.idemyityName = userFromBuy.indentity!!.idemyityName.toString().encrypt()
            }
            val uMAuth2 = mMerchanAuthRepository.findTop1ByUserId(userFromBuy?.userId ?: "")
            if (uMAuth2 != null) {
                //当前用户已通过商户认证
                userFromBuy?.merchanAuthBean = uMAuth2
                //0 -等待审核  1-审核拒绝 2-审核成功
                userFromBuy?.merchanAuthState = uMAuth2.verifyState ?: 0
            }


            it.userFromServer = userFromServer
            it.userFromBuy = userFromBuy
            //查询当前用户是否已评价
            if (it.commId != null) {
                val comm = mOrderCommRepository.findTop1ByOrderId(it.orderId)
                if (comm != null) {
                    //买方
                    if (uid.equals(it.userIdFromBuy) && comm.commTimeFormBuy != null) {
                        it.commed = true
                    } else it.commed = uid.equals(it.userIdFromServer) && comm.commTimeFormServer != null
                }
            } else {
                it.commed = false
            }
            it.address = mAddressRepository.findTop1ById(it.sAddressId.toLong())

            if (it.orderType == 0) {
                //技能
                it.orderBusinessBean = mUserSkillRepository.findTop1ById(it.orderBusinessId?.toLong() ?: 0).let {
                    it?.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it?.s_tag ?: 0L)
                    it?.pTagBean = mSkillParentTabRepository.findTop1ByParentId(it?.p_tag ?: 0L).let {
                        it?.subSkills = null
                        it
                    }
                    it
                }
            }
            if (it.orderType == 1) {
                //跑腿
                it.orderBusinessBean = mUserRunErrandRepository.findTop1ById(it?.orderBusinessId?.toLong() ?: 0)?.let {
                    it.businessAddressBean = mAddressRepository.findTop1ById(it.businessAddressId ?: 0)
                    it.addresBean = mAddressRepository.findTop1ById(it.addresId ?: 0)
                    it
                }
            }
            if (it.orderType == 2) {
                //同城
                it.orderBusinessBean = mUserRunErrandRepository.findTop1ById(it.orderBusinessId?.toLong() ?: 0).let {
                    it?.businessAddressBean = mAddressRepository.findTop1ById(it?.businessAddressId ?: 0)
                    it?.addresBean = mAddressRepository.findTop1ById(it?.addresId ?: 0)
                    it
                }
            }
            it.specsBean = mSpecsTabRepository.findTopBySpecsId(it.specsId ?: 0)
        }
        return HttpResponse(allOrder)
    }

    @Autowired
    lateinit var mDemandRepository: DemandRepository

    /**
     * 支付
     */
    override fun pay(mRequestBean_Pay: RequestBean_Pay, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
        val uid = getUserIdByToken(token)
        val orderBean = mOrderRepository.findTop1ByOrderId(orderId = mRequestBean_Pay.orderId)
                ?: return "订单不存在".getErrorRespons()
        if (orderBean.price != mRequestBean_Pay.price.toFloat()) {
            return "支付金额与订单金额不一致".getErrorRespons()
        }
        if (orderBean.state == -1) {
            return "订单已取消，无法支付".getErrorRespons()
        }

        if (orderBean.state!! >= 2) {
            return "订单已支付，请勿重复操作".getErrorRespons()
        }
        var subTitle = ""
        if (orderBean.orderType == 0) {
            val userSkill = mUserSkillRepository.findTop1ById(orderBean.orderBusinessId?.toLong() ?: 0)
//            val subSkillTab = mSubSkillTabRepository.findOneBySubTagId(userSkill?.s_tag ?: 0)!!
            subTitle = userSkill?.title ?: "技能服务"
        }
        if (orderBean.orderType == 1 || orderBean.orderType == 2) {
            val userRunErrand = mUserRunErrandRepository.findTop1ById(orderBean.orderBusinessId!!.toLong())
            subTitle = userRunErrand?.tradeNames
                    ?: if (orderBean.orderType == 1) "跑腿代办" else if (orderBean.orderType == 2) "同城配送" else "未知的交易类型"
        }

        val finalPrice = orderBean.price + mRequestBean_Pay.addPrice
        when (mRequestBean_Pay.payType) {
            0 -> {
                val payPswDB = mBalanceRepository.findTop1ByUserId(uid)?.payPsw
                if (payPswDB == null) return "支付密码未设置".getErrorRespons()
                if (mRequestBean_Pay.password == null) return "余额支付缺少支付密码".getErrorRespons()
                val psw = mRequestBean_Pay.password.decrypt() ?: return "支付密码校验失败".getErrorRespons()
                if (!psw.trim().equals(payPswDB.trim())) return "支付密码校验失败".getErrorRespons()
                //余额支付
                val mBalance = mBalanceRepository.findTop1ByUserId(uid)
                if (mBalance!!.balance!! >= finalPrice && mBalance.balance!! - mBalance.freeBalance!! >= finalPrice) {
                    //剩余余额
                    val a = mBalance.balance!! - finalPrice
                    mBalance.balance = a
                    mBalanceRepository.saveAndFlush(mBalance)
                    //当前付款的对象为技能 在技能表中查询


                    //记录当前用户流水
                    mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = uid, bType = 1,
                            orderId = mRequestBean_Pay.orderId,
                            orderDec = subTitle,
                            price = finalPrice, payType = 2))
                    //记录公司入账流水
                    mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                            userId = "admin",
                            bType = 1,
                            price = finalPrice.toFloat(),
                            toOrFromUserId = uid,
                            orderId = mRequestBean_Pay.orderId,
                            noteDec = if (orderBean.orderType == 0) "购买技能付款" else if (orderBean.orderType == 1) "跑腿代办付款" else if (orderBean.orderType == 2) "跑腿代办付款" else "未知类型付款",
                            bFType = 0))
                    val mUser_Web = mUser_WebRepository.findTop1ByUserId("admin")!!
                    mUser_Web.balance = mUser_Web.balance!! + finalPrice
                    mUser_WebRepository.saveAndFlush(mUser_Web)
//                    mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = userSkill?.user?.userId ?: "", bType = 0,
//                            orderId = mRequestBean_Pay.orderId,
//                            orderDec = subSkillTab.subTitle,
//                            price = mRequestBean_Pay.price.toFloat(), payType = 2))

                    //更新订单状态
                    orderBean.state = 2
                    orderBean.orderPayTime = Date().time
                    orderBean.addPrice = mRequestBean_Pay.addPrice
                    orderBean.payment = 0
                    mOrderRepository.saveAndFlush(orderBean)
                    //删除redis中存储的数据 防止过期回调
                    RedisUtil.del(Constant.RedisKey.ORDER_REDIS_KEY + orderBean.orderId)
                    if (orderBean?.buyFromDemand == true) {
                        //需求发布方的购买技能 标注需求为服务中状态
                        val demand = mDemandRepository.findTop1ById(orderBean.demandId!!)
                        demand?.demandState = 1
                        mDemandRepository.saveAndFlush(demand)
                        //删除redis中存储的数据 防止需求过期回调
                        RedisUtil.del("${Constant.RedisKey.DEMAND_KEY}${demand?.id}")
                    }
                    logger.info("当前订单剩余支付时间${RedisUtil.getExpire(orderBean.orderId).toInt() / 60.0}分钟")
                } else {
                    return "余额不足".getErrorRespons()
                }
                if (orderBean.orderType == 1) {
                    PushData(pType = 0, title = "您有新的订单", content = "${user!!.userName}发布了一个跑腿代办订单，快来看看吧", bussContent = "${mUserRunErrandRepository.findTop1ByUserId(uid)?.id}").toJsonStr().pushMessageAll()
                }

                if (orderBean.orderType == 2) {
                    PushData(pType = 1, title = "您有新的订单", content = "${user!!.userName}发布了一个同城配送订单，快来看看吧", bussContent = "${mUserRunErrandRepository.findTop1ByUserId(uid)?.id}").toJsonStr().pushMessageAll()
                }

                return HttpResponse(ResponseBean_Pay(null, null))
            }
            1 -> {
                //微信支付
                val body = "${orderBean.orderId}}"
                val str = try {
                    Gson().toJson(payManager.getOrderInfo(MerchantPayOrder("2", AliTransactionType.APP.name, "购买服务:${subTitle}", body, if (isRelease != false) BigDecimal(finalPrice.toString()) else BigDecimal(0.01), orderBean.orderId)))
                } catch (e: Exception) {
                    return "订单已支付，请勿重复操作".getErrorRespons()
                }
                val bean = Gson().fromJson<WeChatPayResultBean>(str, WeChatPayResultBean::class.java)
                logger.info("生成微信支付参数：$str")
                return HttpResponse(ResponseBean_Pay(alipayStr = null, weChatPayResult = bean))
            }
            2 -> {
                //支付宝支付
                val body = "${orderBean.orderId}}"
                val aaaa = try {
                    payManager.getOrderInfo(MerchantPayOrder("1", AliTransactionType.APP.name, "购买服务:${subTitle}", body, if (isRelease != false) BigDecimal(finalPrice.toString()) else BigDecimal(0.01), orderBean.orderId))
                } catch (e: Exception) {
                    return "订单已支付，请勿重复操作".getErrorRespons()
                }
                val bbb = Helper.buildOrderParam(aaaa)
                logger.info("生成支付宝支付参数：$bbb")
                return HttpResponse(ResponseBean_Pay(alipayStr = "$bbb", weChatPayResult = null))
            }
            else -> {
                return "未知的支付方式".getErrorRespons()
            }

        }


    }

    /**
     * 查询用户余额
     */
    override fun getBalance(token: String): HttpResponse<Any> {
        val uid = getUserIdByToken(token)
        var allPrice = 0.0
        mBalanceNotesRepository.findAllByUserIdAndBType(uid).forEach {
            if (it.price != null)
                allPrice += it.price ?: 0.0f
        }
        val balance = mBalanceRepository.findTop1ByUserId(uid)!!
        balance.allFromPrice = allPrice
        balance.isSetPsw = balance.payPsw != null
        return HttpResponse(balance)
    }

    /**
     * 生成订单 0 技能订单 由app端调用  1、2为跑腿代办和同城配送  由内部调用
     */
    override fun createOrder(mRequestBean_CreateOrder: WBOrder, token: String): HttpResponse<Any> {

        var ob: WBOrder? = null
        var userIdFromServer: String? = null

        //查询传入的地址索引 是否存在该地址
        if (mAddressRepository.findTop1ById(mRequestBean_CreateOrder.sAddressId.toLong()) == null) return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "地址不存在", data = null)
        if (mRequestBean_CreateOrder.price <= 0) {
            return "金额不能为空或等于0".getErrorRespons()
        }

        if (mRequestBean_CreateOrder.orderType == 0) {
            //生成技能订单 查询技能是否存在
            val skill = try {
                mUserSkillRepository.findTop1ById(mRequestBean_CreateOrder.orderBusinessId?.toLong() ?: 0)
                        ?: return "操作对象不存在".getErrorRespons()
            } catch (e: Exception) {
                return "操作对象不存在".getErrorRespons()
            }
            if (mRequestBean_CreateOrder.specsId == null) {
                return "购买的技能规格不能为空".getErrorRespons()
            }
            val specsTabBean = mSpecsTabRepository.findTopBySpecsId(mRequestBean_CreateOrder.specsId!!)
                    ?: return "技能规格不存在".getErrorRespons()
            if ((specsTabBean.specsPrice!!) * (mRequestBean_CreateOrder.number) != mRequestBean_CreateOrder.price) {
                return "支付金额与所选规格不匹配".getErrorRespons()
            }
            userIdFromServer = skill.user?.userId
            if (userIdFromServer.isNullOrEmpty()) {
                return "用户ID为空".getErrorRespons()
            }
            if (userIdFromServer.equals(Helper.getUserIdByToken(token))) return "无法购买自己的技能".getErrorRespons()
            val orderBean = WBOrder(orderId = "wb-${Date().time}", state = 1, orderType = mRequestBean_CreateOrder.orderType, orderBusinessId = mRequestBean_CreateOrder.orderBusinessId,
                    number = mRequestBean_CreateOrder.number, sTime = mRequestBean_CreateOrder.sTime, sAddressId = mRequestBean_CreateOrder.sAddressId,
                    orderDec = mRequestBean_CreateOrder.orderDec, price = mRequestBean_CreateOrder.price, specsId = mRequestBean_CreateOrder.specsId, userIdFromBuy = Helper.getUserIdByToken(token), userIdFromServer = userIdFromServer, buyFromDemand = mRequestBean_CreateOrder.buyFromDemand,
                    demandId = mRequestBean_CreateOrder.demandId)
            ob = mOrderRepository.saveAndFlush(orderBean)

            //写入redis
            RedisUtil.set(Constant.RedisKey.ORDER_REDIS_KEY + orderBean.orderId, "", Constant.finalParams.ORDER_EXPIRETIME)
        }

        if (mRequestBean_CreateOrder.orderType == 1 || mRequestBean_CreateOrder.orderType == 2) {
            //跑腿代办或同城配送
            val userRunErrand = mUserRunErrandRepository.findTop1ById(mRequestBean_CreateOrder.orderBusinessId?.toLong()
                    ?: return "订单不存在".getErrorRespons()) ?: return "订单不存在".getErrorRespons()
//            if (userRunErrand.sendLat==null || userRunErrand.sendLng==null){
//                return "发单人经纬度不能为空".getErrorRespons()
//            }
//            if (userRunErrand.addresId==null||mAddressRepository.findTop1ById(userRunErrand.addresId!!.toLong())==null){
//                return "收获地址不存在或为空".getErrorRespons()
//            }

            val orderBean = WBOrder(orderId = "wb-${Date().time}", state = 1, orderType = mRequestBean_CreateOrder.orderType, orderBusinessId = mRequestBean_CreateOrder.orderBusinessId,
                    number = mRequestBean_CreateOrder.number, sTime = mRequestBean_CreateOrder.sTime, sAddressId = mRequestBean_CreateOrder.sAddressId,
                    orderDec = mRequestBean_CreateOrder.orderDec, price = mRequestBean_CreateOrder.price, userIdFromBuy = userRunErrand!!.userId)
            ob = mOrderRepository.saveAndFlush(orderBean)

            //写入redis
            RedisUtil.set(Constant.RedisKey.ORDER_REDIS_KEY + orderBean.orderId, "", Constant.finalParams.ORDER_EXPIRETIME)
        }


        return HttpResponse(ob!!)
    }

    /**
     * 技能留言
     */
    override fun addSkillMessage(mRequestBean_AddSkillMessage: RequestBean_AddSkillMessage, token: String): HttpResponse<Any> {

        val thisUserId = getUserIdByToken(token)

        if (mRequestBean_AddSkillMessage.content == null || mRequestBean_AddSkillMessage.content?.isEmpty() ?: true) {
            return "留言内容不能为空".getErrorRespons()
        }

        if (mRequestBean_AddSkillMessage.skillId == null) {
            return "技能ID不存在".getErrorRespons()
        }

        //即将进行留言的技能
        val userSkill = mUserSkillRepository.findTop1ById(mRequestBean_AddSkillMessage.skillId?.toLong() ?: 0)

        if (userSkill == null) {
            return "技能不存在".getErrorRespons()
        }

        if (mRequestBean_AddSkillMessage.parentId == null || mRequestBean_AddSkillMessage.parentId == -1) {
            mSkillMessageRepository.saveAndFlush(SkillMessageTab(userId = thisUserId, content = mRequestBean_AddSkillMessage.content
                    ?: "", skillId = userSkill.id?.toString()))
        } else {
            //留言回复
            val m = mSkillMessageRepository.findTop1ById(mRequestBean_AddSkillMessage.parentId?.toLong() ?: 0)
            if (m == null) {
                return "未找到该留言，无法回复".getErrorRespons()
            }
            val aa2 = SkillMessage2Tab(userId = thisUserId, content = mRequestBean_AddSkillMessage.content
                    ?: "", skillMessageTab = m)
            mSkillMessageRepository2.saveAndFlush(aa2)
            val aaaa = m.subMessageList?.toMutableList()
            aaaa?.add(aa2)
            m.subMessageList = aaaa
            mSkillMessageRepository.saveAndFlush(m)
        }
        val skill = mUserSkillRepository.findTop1ById(mRequestBean_AddSkillMessage.skillId?.toLong() ?: 0)!!
        skill.user?.password = null
        skill.user?.indentity = null
        val l = mSkillMessageRepository.findAllBySkillIdOrderByMessageCreateTimeDesc(skill.id?.toString() ?: "")
        l.forEach {
            it.user = mUserRepository.findTop1ByUserId(it.userId!!)!!
            it.subMessageList?.forEach {
                it.user = mUserRepository.findTop1ByUserId(it.userId!!)!!
            }
        }
        skill.messageList = l
        return HttpResponse("")
    }

    /**
     * 获取收藏的技能列表
     */
    override fun getLikedSkills(token: String,mReqBeanGetLikedSkills:ReqBeanGetLikedSkills): HttpResponse<Any> {
        if (mReqBeanGetLikedSkills.page<1)return "page必须从1开始".getErrorRespons()
        val thisUserId = getUserIdByToken(token)
        val l = ArrayList<UserSkill>()
        logger.info(mReqBeanGetLikedSkills.toString())
        mLikeSkillTabRepository.findAllByUserId(thisUserId,mReqBeanGetLikedSkills.lat,mReqBeanGetLikedSkills.lng,(mReqBeanGetLikedSkills.page - 1) * mReqBeanGetLikedSkills.number,mReqBeanGetLikedSkills.number).forEach {
            val us = mUserSkillRepository.findTop1ById(it.userSkillId?.toLong() ?: 0)
            us?.liked_skill = true
            us?.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(thisUserId, us?.user?.userId ?: "") != null
            us?.distance = it.distance
//            us?.user = null
            if (us?.isBan == false)
                l.add(us!!)
        }

        l.forEach {
            it.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(getUserIdByToken(token), it.user?.userId
                    ?: "") != null
            it.liked_skill = mLikeSkillTabRepository.findTop1ByUserIdAndUserSkillId(getUserIdByToken(token), it.id!!) != null
            it.specsList = mSpecsTabRepository.findAllBySkillId(it.id!!)
            it.user.let { u ->
                u!!
                if (u.indentity != null) {
                    u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                    u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
                }
                //var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
                val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
                val state = b?.verifyState ?: -1
                u.merchanAuthState = state
                u.merchanAuthBean = b
            }
            val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
            st.subSkills = null
            it.pTagBean = st

            it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                    ?: return "标签查询失败".getErrorRespons()
        }
        return HttpResponse(l)
    }

    /**
     * 收藏取消收藏技能
     */
    override fun likeOrUnUnLikeSkill(mRequestBean_LikeOrDisLikeSkill: RequestBean_LikeOrDisLikeSkill, token: String): HttpResponse<Any> {
//        mLikeSkillTabRepository
        //校验参数
        if (mRequestBean_LikeOrDisLikeSkill.skillId == null || mRequestBean_LikeOrDisLikeSkill.skillId?.isEmpty() == true) {
            return "技能ID不能为空".getErrorRespons()
        }
        //当前用户的ID
        val thisUserId = getUserIdByToken(token)
        //即将收藏的技能
        val willLikedSkill = mUserSkillRepository.findTop1ById(mRequestBean_LikeOrDisLikeSkill.skillId?.toLong() ?: 0)
                ?: return "找不到技能".getErrorRespons()
        if (willLikedSkill.isBan) return "该技能被管理员禁止显示".getErrorRespons()
        val ll = mLikeSkillTabRepository.findTop1ByUserIdAndUserSkillId(thisUserId, willLikedSkill.id!!)
        when (mRequestBean_LikeOrDisLikeSkill.doType) {
            0 -> {
                //收藏技能

                if (ll != null) {
                    //已经存在
                    return "已收藏，请勿重复收藏".getErrorRespons()
                } else {
                    mLikeSkillTabRepository.saveAndFlush(LikeSkillTab(userId = thisUserId, userSkillId = willLikedSkill.id,lat = willLikedSkill.lat,lng = willLikedSkill.lng))
                }
            }
            1 -> {
                //取消收藏技能
                if (ll != null) {
                    //取消
                    mLikeSkillTabRepository.delete(ll)
                } else {
                    //提示技能不存在
                    return "未收藏，请收藏后再取消".getErrorRespons()
                }
            }
            else -> {
                return "操作类型有误".getErrorRespons()
            }

        }
//        mLikeSkillTabRepository.saveAndFlush(LikeSkillTab(userId = thisUserId, userSkills = alreadyLikedSkills))
        return HttpResponse("")
    }


    /**
     * 获取技能详情
     */
    override fun getSkillDetail(requestSkillDetailBean: RequestBean_GetSkillDetail, token: String): HttpResponse<Any> {
        if (requestSkillDetailBean.skillId == -1L) {
            return "技能ID为空".getErrorRespons()
        }
        val skill = mUserSkillRepository.findTop1ById(requestSkillDetailBean.skillId)
                ?: return "技能不存在".getErrorRespons()
        if (skill.isBan) return "该技能被管理员禁止显示".getErrorRespons()
        skill.user.let { u ->
            u!!
            if (u.indentity != null) {
                u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
            }
            //var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
            val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
            val state = b?.verifyState ?: -1
            u.merchanAuthState = state
            u.merchanAuthBean = b
        }
//        if (skill.user?.indentity != null) {
//            skill.user?.indentity!!.idemyityCardNumber = skill.user?.indentity!!.idemyityCardNumber.toString().encrypt()
//            skill.user?.indentity!!.idemyityName = skill.user?.indentity!!.idemyityName.toString().encrypt()
//        }

        skill.liked_skill = mLikeSkillTabRepository.findTop1ByUserIdAndUserSkillId(getUserIdByToken(token), skill.id!!) != null
        val l = mSkillMessageRepository.findAllBySkillIdOrderByMessageCreateTimeDesc(skill.id?.toString() ?: "")
        l.forEach {
            it.user = mUserRepository.findTop1ByUserId(it.userId!!)!!
            it.subMessageList?.forEach {
                val u = mUserRepository.findTop1ByUserId(it.userId!!)!!
                if (u.indentity != null) {
                    u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                    u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
                }
                //var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
                val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
                val state = b?.verifyState ?: -1
                u.merchanAuthState = state
                u.merchanAuthBean = b
                it.user = u
            }
        }
        skill.messageList = l
        skill.specsList = mSpecsTabRepository.findAllBySkillId(requestSkillDetailBean.skillId)
        val st = mSkillParentTabRepository.findTop1ByParentId(skill.p_tag ?: 0) ?: return "标签查询失败".getErrorRespons()
        st.subSkills = null
        skill.pTagBean = st

        skill.sTagBean = mSubSkillTabRepository.findOneBySubTagId(skill.s_tag ?: 0) ?: return "标签查询失败".getErrorRespons()

        val commList = mOrderCommRepository.findAllBySkillId(requestSkillDetailBean.skillId)
        commList.forEach {
            it.user = mUserRepository.findTop1ByUserId(it.userIdFromBuy.toString()!!)!!
        }
        skill.commList = commList

        return HttpResponse(skill)
    }

    /**
     * 发布技能
     */
    override fun sendSkill(userSkill: UserSkill, token: String): HttpResponse<Any> {
        //参数判空
        if (userSkill.imgsUrl.isEmpty()) {
            return "图片不能为空".getErrorRespons()
        }
        if (userSkill.p_tag == null || mSkillParentTabRepository.findTop1ByParentId(userSkill.p_tag) == null || userSkill.s_tag == null || mSubSkillTabRepository.findOneBySubTagId(userSkill.s_tag) == null) {
            return "技能标签为空或不存在".getErrorRespons()
        }
        if (userSkill.specsList.isNullOrEmpty()) {
            return "最少添加一个服务规格".getErrorRespons()
        }
//        if (userSkill.subTagsStr.isEmpty()) {
//            return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "subTagsStr为空", data = null)
//        }

//        if (userSkill.price == 0F) {
//            return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "传入金额有误", data = null)
//        }

        if (userSkill.serviceDec.isEmpty()) {
            return "技能说明不能为空".getErrorRespons()
        }

        if (userSkill.title.isEmpty()) {
            return "名称不能为空".getErrorRespons()
        }
        if (userSkill.lat == 999.0 || userSkill.lng == 999.0) {
            return "经纬度不能为空".getErrorRespons()
        }

        val user = mUserRepository.findTop1ByUserId(getUserIdByToken(token))!!
        if (user.indentity == null) return "服务者认证未通过".getErrorRespons()
        userSkill.user = user
        if (userSkill.id != null) {
            logger.info("修改技能")
            if (mUserSkillRepository.findTop1ById(userSkill.id!!) == null) return "修改的技能不存在".getErrorRespons()
        }
        val sk = mUserSkillRepository.saveAndFlush(userSkill)
        //写入对应标签
        userSkill.specsList!!.forEach {
            val sT = it.specsTitle
            val sP = it.specsPrice
            val sU = it.unit


            if (sT == null || sP == null || sU == null) {
                return "规格传入有误,规格标题和对应的价格以及单位不能为空".getErrorRespons()
            }
            if (it.specsId != null) {
                val specsTabBean = mSpecsTabRepository.findTopBySpecsId(it.specsId!!)
                        ?: return "修改的技能规格不存在".getErrorRespons()
                specsTabBean.specsTitle = sT
                specsTabBean.specsPrice = sP
                specsTabBean.unit = sU
                mSpecsTabRepository.saveAndFlush(specsTabBean)
                logger.info("技能规格修改成功")
            } else {
                mSpecsTabRepository.saveAndFlush(SpecsTabBean(skillId = sk.id,
                        specsTitle = sT,
                        unit = sU,
                        specsPrice = sP,
                        createUserId = user!!.userId))
            }
        }

        val dyn = UserDynamic()

        dyn.bName = userSkill.title
        dyn.bType = 0
        dyn.userId = user?.userId ?: ""
        mDynamicRepository.saveAndFlush(dyn)
        return HttpResponse(mUserSkillRepository.findTop1ById(userSkill.id!!)!!.let {
            it.specsList = mSpecsTabRepository.findAllBySkillId(it.id ?: 0L)
            it
        })
    }

    /**
     * 获取用户发布的技能列表
     */
    override fun getSkills(token: String, mRequestBean_GetSkills: RequestBean_GetSkills): HttpResponse<Any> {

        val user = mUserRepository.findTop1ByUserId(if (mRequestBean_GetSkills.userId?.isEmpty() == true) getUserIdByToken(token) else mRequestBean_GetSkills.userId!!)?:return "用户不存在".getErrorRespons()
        if (mRequestBean_GetSkills.number==0)return "page必须从1开始".getErrorRespons()
        user.let { u ->
            u!!
            if (u.indentity != null) {
                u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
            }
            //var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
            val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
            val state = b?.verifyState ?: -1
            u.merchanAuthState = state
            u.merchanAuthBean = b
        }

        val skills = mUserSkillRepository.findAllByUser(user.userId,mRequestBean_GetSkills.lat,mRequestBean_GetSkills.lng,(mRequestBean_GetSkills.page - 1) * mRequestBean_GetSkills.number,mRequestBean_GetSkills.number)
        skills.forEach {
            //            it.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(getUserIdByToken(token), it.user?.userId?:"")!=null
            it.user = user
            it.isLiked = null
            it.liked_skill = null
            it.specsList = mSpecsTabRepository.findAllBySkillId(it.id!!)
            val l = mSkillMessageRepository.findAllBySkillIdOrderByMessageCreateTimeDesc(it.id?.toString() ?: "")
            l.forEach {
                it.user = mUserRepository.findTop1ByUserId(it.userId!!)!!
                it.subMessageList?.forEach {
                    val u = mUserRepository.findTop1ByUserId(it.userId!!)!!
                    if (u.indentity != null) {
                        u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                        u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
                    }
                    val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
                    val state = b?.verifyState ?: -1
                    u.merchanAuthState = state
                    u.merchanAuthBean = b
                    it.user = u
                }
            }
            it.messageList = l
            it.specsList = mSpecsTabRepository.findAllBySkillId(it.id!!.toLong())
            val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0) ?: return "标签查询失败".getErrorRespons()
            st.subSkills = null
            it.pTagBean = st

            it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0) ?: return "标签查询失败".getErrorRespons()

        }
        return HttpResponse(skills)
    }

    /**
     * 获取全部技能列表
     */
    override fun getAllSkills(token: String, mRequestBean_GetAllSkill: RequestBean_GetAllSkill): HttpResponse<Any> {
        //当前查询的用户
        val user = mUserRepository.findTop1ByUserId(getUserIdByToken(token))
        if (mRequestBean_GetAllSkill.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        if (mRequestBean_GetAllSkill.subTagId == -1) {
            //传入经纬度做排序用
            val aa = mUserSkillRepository.findAll(mRequestBean_GetAllSkill.lat, mRequestBean_GetAllSkill.lng, (mRequestBean_GetAllSkill.page - 1) * mRequestBean_GetAllSkill.number, mRequestBean_GetAllSkill.number)
            aa.forEach {
                it.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(getUserIdByToken(token), it.user?.userId
                        ?: "") != null
                it.liked_skill = mLikeSkillTabRepository.findTop1ByUserIdAndUserSkillId(getUserIdByToken(token), it.id!!) != null
                it.specsList = mSpecsTabRepository.findAllBySkillId(it.id!!)
                it.user.let { u ->
                    u!!
                    if (u.indentity != null) {
                        u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                        u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
                    }
                    //var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
                    val b = mMerchanAuthRepository.findTop1ByUserId(u.userId)
                    val state = b?.verifyState ?: -1
                    u.merchanAuthState = state
                    u.merchanAuthBean = b
                }
                val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
                        ?: return "标签查询失败".getErrorRespons()
                st.subSkills = null
                it.pTagBean = st

                it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                        ?: return "标签查询失败".getErrorRespons()
            }
            return HttpResponse(aa)
        } else {
            val ss = mUserSkillRepository.findAllBySubTagId(mRequestBean_GetAllSkill.subTagId!!, mRequestBean_GetAllSkill.lat, mRequestBean_GetAllSkill.lng, (mRequestBean_GetAllSkill.page - 1) * mRequestBean_GetAllSkill.number, mRequestBean_GetAllSkill.number)

            ss.forEach {
                it.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(getUserIdByToken(token), it.user?.userId
                        ?: "") != null
                it.specsList = mSpecsTabRepository.findAllBySkillId(it.id!!)
                val st = mSkillParentTabRepository.findTop1ByParentId(it.p_tag ?: 0)
                        ?: return "标签查询失败".getErrorRespons()
                st.subSkills = null
                it.pTagBean = st

                it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                        ?: return "标签查询失败".getErrorRespons()
            }
            return HttpResponse(ss)
        }
    }


    /**
     * 获取动态
     */
    override fun getDynamic(): HttpResponse<Any> {

        val list = ArrayList<ResponseBean_Dynamic>()
        mDynamicRepository.findAll().forEach {
            val u = mUserRepository.findTop1ByUserId(it.userId)

            list.add(ResponseBean_Dynamic(it.userId, it.bType, it.bName, u?.userLogo, u?.userName))
        }
        return HttpResponse(list)
    }


    /**
     * 订单评价
     */
    override fun orderComm(mRequestBean_OrderComm: RequestBean_OrderComm, token: String): HttpResponse<Any> {
        val user = mUserRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mRequestBean_OrderComm.orderId.isEmpty()) {
            return "订单ID为空".getErrorRespons()
        }
        if (mRequestBean_OrderComm.commStar == -1) {
            return "评价星级为空".getErrorRespons()
        }
        if (mRequestBean_OrderComm.content.isEmpty()) {
            return "评价内容为空".getErrorRespons()
        }
        val order = mOrderRepository.findTop1ByOrderId(mRequestBean_OrderComm.orderId)
                ?: return "订单不存在".getErrorRespons()

        val commTab = mOrderCommRepository.findTop1ByOrderId(mRequestBean_OrderComm.orderId)
                ?: OrderCommTab(orderId = mRequestBean_OrderComm.orderId)
        if (order.state != 3) {
            return "订单当前状态无法进行该操作".getErrorRespons()
        }
        if (order.userIdFromBuy.equals(user.userId) && commTab.commTimeFormBuy == null) {
            //买方评价
            commTab.buyCommContent = mRequestBean_OrderComm.content
            commTab.commStarFromBuy = mRequestBean_OrderComm.commStar
            commTab.imgUrls = mRequestBean_OrderComm.imgUrls
            commTab.skillId = mRequestBean_OrderComm.skillId
            commTab.userIdFromBuy = mRequestBean_OrderComm.userIdFromBuy
            commTab.commTimeFormBuy = Date().time
            mOrderCommRepository.saveAndFlush(commTab)

        } else if (order.userIdFromServer.equals(user.userId) && commTab.commTimeFormServer == null) {
            //卖方评价
            commTab.serverCommContent = mRequestBean_OrderComm.content
            commTab.commStarFromServer = mRequestBean_OrderComm.commStar
            commTab.imgUrls = mRequestBean_OrderComm.imgUrls
            commTab.skillId = mRequestBean_OrderComm.skillId
            commTab.userIdFromBuy = mRequestBean_OrderComm.userIdFromBuy
            commTab.commTimeFormServer = Date().time
            mOrderCommRepository.saveAndFlush(commTab)
        } else {
            return "请勿重复评价".getErrorRespons()
        }
        //评价完成后标记最新订单状态
        val commTab_DB = mOrderCommRepository.findTop1ByOrderId(mRequestBean_OrderComm.orderId)!!
        order.state = if (commTab_DB.commTimeFormBuy != null && commTab_DB.commTimeFormServer != null) 6 else 3
        order.commId = commTab_DB.id
        mOrderRepository.saveAndFlush(order)
        //删除redis中存储的数据 防止过期回调
        RedisUtil.del(Constant.RedisKey.ORDER_REDIS_KEY_COMM + commTab_DB.orderId)
        return HttpResponse("")
    }

    /**
     * 完成订单
     */
    override fun finishOrder(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any> {
        if (mRequestBean_CancleOrder.orderId.isEmpty()) {
            return "订单不存在".getErrorRespons()
        }


        val orderBean = mOrderRepository.findTop1ByOrderId(mRequestBean_CancleOrder.orderId)
                ?: return "订单不存在".getErrorRespons()

        if (orderBean.orderType == 0) {
            //技能订单
            when (orderBean.state) {
                1, -1, -2, 3 -> {
                    return "无法进行该操作".getErrorRespons()
                }
            }
        }
        if (orderBean.orderType == 1 || orderBean.orderType == 2) {
            //跑腿订单
            if (orderBean.state != 5) {
                return "无法进行该操作".getErrorRespons()
            }
            if (orderBean.transactionCode != mRequestBean_CancleOrder.tCode) {
                return "收获码错误，请联系收获方获取正确的收获码".getErrorRespons()
            }
        }

        //分成
        //查询该订单对应的服务商信息
        val user_server = mUserRepository.findTop1ByUserId(orderBean.userIdFromServer)!!
        //查询该服务商绑定的区域ID
        val countyId = user_server.boundCountId
        //根据区域ID查询代理商
        val county = mCountyRepository.findTop1ByCountyId(countyId ?: 0)
        val town = mTownRepository.findTop1ByTownId(countyId ?: 0)

        //代理商ID
        val agentId = county?.agentId ?: town?.agentId
        //获取订单对应的技能实体
        var dec = ""
        if (orderBean.orderType == 0) {
            val skill = mUserSkillRepository.findTop1ById(orderBean.orderBusinessId!!.toLong())!!
            //获取订单对应的标签实体
            dec = skill.title
        }
        if (orderBean.orderType == 1) {
            val userRunErrand = mUserRunErrandRepository.findTop1ById(orderBean.orderBusinessId?.toLong()!!)
            dec = "跑腿代办${userRunErrand?.tradeNames}"
        }

        if (orderBean.orderType == 2) {
            val userRunErrand = mUserRunErrandRepository.findTop1ById(orderBean.orderBusinessId?.toLong()!!)
            dec = "同城配送${userRunErrand?.tradeNames}"
        }

        val admin = mUser_WebRepository.findTop1ByUserId("admin")!!
        val serverBalance = mBalanceRepository.findTop1ByUserId(orderBean.userIdFromServer)!!
        //如果代理商不存在 该区域没有代理商时
        if (agentId == null || agentId.isEmpty()) {
            //直接跟公司分
            //获取设置的与未绑定代理商的用户的分成比例
            val unBoundAgentUserCommis = mCommissionAllocationRepository.findTop1ById(0)?.unBoundAgentUserCommissionAllocation!!
            //分成的金额
            val xlM = orderBean.price * unBoundAgentUserCommis / 100.0

            //记录公司出账
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = "admin",
                    bType = 0,
                    price = orderBean.price,
                    toOrFromUserId = orderBean.userIdFromServer,
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "支出给服务者",
                    bFType = 0
            ))
            //公司出张
            admin.balance = admin.balance!! - orderBean.price
            mUser_WebRepository.saveAndFlush(admin)


            //记录服务者入账流水
            mBalanceNotesRepository.saveAndFlush(BalanceNotes(
                    userId = orderBean.userIdFromServer,
                    bType = 0,
                    orderId = mRequestBean_CancleOrder.orderId,
                    orderDec = dec,
                    price = orderBean.price
            ))

            //服务者入账
            serverBalance.balance = serverBalance.balance!! + orderBean.price
            mBalanceRepository.saveAndFlush(serverBalance)

            //从服务者账户扣除 进公司账
            //记录服务者出账流水
            mBalanceNotesRepository.saveAndFlush(BalanceNotes(
                    userId = orderBean.userIdFromServer,
                    bType = 1,
                    orderId = mRequestBean_CancleOrder.orderId,
                    orderDec = dec,
                    price = xlM.toFloat(),
                    notesDec = "公司抽成"
            ))
            //服务者出账
            serverBalance.balance = (serverBalance.balance!! - xlM).toDouble()
            mBalanceRepository.saveAndFlush(serverBalance)

            //记录公司入账流水
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = "admin",
                    bType = 1,
                    price = xlM.toFloat(),
                    toOrFromUserId = orderBean.userIdFromServer,
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "用户分成",
                    bFType = 0
            ))
            //公司入账
            admin.balance = (admin.balance!! + xlM).toFloat()
            mUser_WebRepository.saveAndFlush(admin)
            orderBean.state = 3
            orderBean.compilerOrderTime = Date().time
            mOrderRepository.saveAndFlush(orderBean)
            //开启超时未评价自动好评
            RedisUtil.set(Constant.RedisKey.ORDER_REDIS_KEY_COMM + orderBean.orderId, "", Constant.finalParams.ORDER_COMM_EXPIRETIME)

            if (orderBean.orderType == 0 && orderBean?.buyFromDemand == true) {
                //需求发布方的购买技能 标注需求为服务中状态
                val demand = mDemandRepository.findTop1ById(orderBean.demandId!!)
                demand?.demandState = 2
                mDemandRepository.saveAndFlush(demand)
            }
            return HttpResponse("")
        } else {
            //有代理商 跟代理商分成

            //用户分到代理商的金额
            val a3 = county?.proportionForUser ?: town?.proportionForUser!!
            val aa = (a3 / 100.0).toString().toFloat()
            val agentM = (orderBean.price) * aa

            //公司与代理商的分成比例
            val agentUserCommis = mCommissionAllocationRepository.findTop1ById(0)?.agentCommissionAllocation!!

            //计算后 服务者得
//            val user_get_m = orderBean.price - agentM
//            //计算后 代理商可得
//            val agent_get_m = agentM * (unBoundAgentUserCommis / 100.0)
//            //计算后 公司得
//            val xl_get_m = agentM - agent_get_m

            //记录公司出账
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = "admin",
                    bType = 0,
                    price = orderBean.price,
                    toOrFromUserId = orderBean.userIdFromServer,
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "支出给服务者",
                    bFType = 0
            ))
            //公司出张
            admin.balance = admin.balance!! - orderBean.price
            mUser_WebRepository.saveAndFlush(admin)

            //记录服务者入账流水
            mBalanceNotesRepository.saveAndFlush(BalanceNotes(
                    userId = orderBean.userIdFromServer,
                    bType = 0,
                    orderId = mRequestBean_CancleOrder.orderId,
                    orderDec = dec,
                    price = orderBean.price
            ))

            //服务者入账
            serverBalance.balance = serverBalance.balance!! + orderBean.price
            mBalanceRepository.saveAndFlush(serverBalance)

            //记录服务者出账流水 分给代理商
            mBalanceNotesRepository.saveAndFlush(BalanceNotes(
                    userId = orderBean.userIdFromServer,
                    bType = 1,
                    orderId = mRequestBean_CancleOrder.orderId,
                    orderDec = dec,
                    price = agentM.toFloat(),
                    notesDec = "代理商抽成"
            ))

            //服务者出账
            serverBalance.balance = (serverBalance.balance!! - agentM).toDouble()
            mBalanceRepository.saveAndFlush(serverBalance)

            //代理商入账流水
            //记录代理商入账流水
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = agentId,
                    bType = 1,
                    price = agentM.toFloat(),
                    toOrFromUserId = orderBean.userIdFromServer,
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "代理区域收入分成",
                    bFType = 0
            ))
            //代理商入账
            val agentUser = mUser_WebRepository.findTop1ByUserId(agentId)!!
            agentUser.balance = (agentUser.balance!! + agentM.toFloat())
            mUser_WebRepository.saveAndFlush(agentUser)


            val xl = agentM * (agentUserCommis / 100.0f) //公司得
            //记录代理商出账流水 与公司分
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = agentId,
                    bType = 0,
                    price = xl.toFloat(),
                    toOrFromUserId = "admin",
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "新连分成",
                    bFType = 1
            ))
            //代理商出账
            agentUser.balance = (agentUser.balance!! - xl.toFloat())
            mUser_WebRepository.saveAndFlush(agentUser)


            //跟公司分 37开

            //记录公司入账流水 37开
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                    userId = "admin",
                    bType = 1,
                    price = xl.toFloat(),
                    toOrFromUserId = agentId,
                    orderId = mRequestBean_CancleOrder.orderId,
                    noteDec = "代理商分成",
                    bFType = 1
            ))
            //公司入账
            admin.balance = (admin.balance!! + xl.toFloat())
            mUser_WebRepository.saveAndFlush(admin)

            if (agentUser.superAgentId != null && !agentUser.superAgentId.equals("admin") && agentUser.superAgentId!!.isNotEmpty() && agentUser.proportion != null) {
                //与其他代理商分
                val ccc = agentM - xl //代理商实际所得
                //总收入 - 自己可得 = 上级可得
                //上级可得
                val eee = ccc - ccc * (agentUser.proportion!! / 100.0f)

                //代理商出账
                //记录代理商出账流水 与上级分
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = agentId,
                        bType = 0,
                        price = eee.toFloat(),
                        toOrFromUserId = agentUser.superAgentId!!,
                        orderId = mRequestBean_CancleOrder.orderId,
                        noteDec = "上级代理商分成",
                        bFType = 1
                ))
                //代理商出账
                agentUser.balance = (agentUser.balance!! - eee.toFloat())
                mUser_WebRepository.saveAndFlush(agentUser)

                //上级代理商入账流水
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = agentUser.superAgentId!!,
                        bType = 1,
                        price = eee.toFloat(),
                        toOrFromUserId = agentUser.userId,
                        orderId = mRequestBean_CancleOrder.orderId,
                        noteDec = "代理区域收入分成",
                        bFType = 1
                ))
                //上级代理商入账
                val upAgentUser = mUser_WebRepository.findTop1ByUserId(agentUser.superAgentId!!)!!
                upAgentUser.balance = (upAgentUser.balance!! + eee.toFloat())
                mUser_WebRepository.saveAndFlush(upAgentUser)
                orderBean.state = 3
                orderBean.compilerOrderTime = Date().time
                mOrderRepository.saveAndFlush(orderBean)
                RedisUtil.set(Constant.RedisKey.ORDER_REDIS_KEY_COMM + orderBean.orderId, "", Constant.finalParams.ORDER_COMM_EXPIRETIME)
                if (orderBean.orderType == 0 && orderBean?.buyFromDemand == true) {
                    //需求发布方的购买技能 标注需求为服务中状态
                    val demand = mDemandRepository.findTop1ById(orderBean.demandId!!)
                    demand?.demandState = 2
                    mDemandRepository.saveAndFlush(demand)
                }
                return HttpResponse("")
            }
            orderBean.state = 3
            orderBean.compilerOrderTime = Date().time
            mOrderRepository.saveAndFlush(orderBean)
            //开启超时自动好评
            RedisUtil.set(Constant.RedisKey.ORDER_REDIS_KEY_COMM + orderBean.orderId, "", Constant.finalParams.ORDER_COMM_EXPIRETIME)
            if (orderBean.orderType == 0 && orderBean?.buyFromDemand == true) {
                //需求发布方的购买技能 标注需求为服务中状态
                val demand = mDemandRepository.findTop1ById(orderBean.demandId!!)
                demand?.demandState = 2
                mDemandRepository.saveAndFlush(demand)
            }
            return HttpResponse("")
        }

    }

    /**
     * 取消订单
     */
    override fun cancleOrder(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any> {
        if (mRequestBean_CancleOrder.orderId.isEmpty()) {
            return "订单号为空".getErrorRespons()
        }
        val user = mUserRepository.findTop1ByUserId(getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        val wbOrder = mOrderRepository.findTop1ByOrderId(mRequestBean_CancleOrder.orderId)
                ?: return "订单不存在".getErrorRespons()

        if (wbOrder.state == -1) {
            //订单已取消，不能重复操作
            return "订单已取消，不能重复操作".getErrorRespons()
        }
        if (wbOrder.state!! >= 2 && (wbOrder.state != 3 || wbOrder.state != 6)) {
            //已支付 确定是否要退款
            var subTitle = ""
            if (wbOrder.orderType == 0) {
                subTitle = mUserSkillRepository.findTop1ById(wbOrder.orderBusinessId?.toLong()
                        ?: 0)?.title!!
            }
            if (wbOrder.orderType == 1 || wbOrder.orderType == 2) {
                val userRunErrand = mUserRunErrandRepository.findTop1ById(wbOrder.orderBusinessId!!.toLong())
                subTitle = userRunErrand?.tradeNames
                        ?: if (wbOrder.orderType == 1) "跑腿代办" else if (wbOrder.orderType == 2) "同城配送" else "未知的交易类型"
            }




            if (wbOrder.userIdFromBuy == user.userId) {
                //技能服务 买方退款
                //买方取消 扣除手续费
                val commis = mCommissionAllocationRepository.findTop1ById()!!.cancleOrderCommissionAllocation / 100.0
                //买方扣除的手续费 存储进公司账号
                val buyCommis = wbOrder.price * commis

                //记录公司支出流水
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = "admin",
                        bType = 0,
                        price = (wbOrder.price - buyCommis).toFloat(),
                        toOrFromUserId = user.userId,
                        orderId = wbOrder.orderId,
                        noteDec = "订单退款",
                        bFType = 0
                ))
                //记录公司余额
                val u_w = mUser_WebRepository.findTop1ByUserId("admin")
                u_w?.balance = u_w?.balance!! - (wbOrder.price - buyCommis).toFloat()
                mUser_WebRepository.saveAndFlush(u_w)


                //记录买方收入流水
                mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = user.userId, bType = 0, orderId = mRequestBean_CancleOrder.orderId,
                        price = wbOrder.price, notesDec = "${subTitle}订单退款收入${wbOrder.price}元",
                        orderDec = subTitle))

                //记录买方支出流水
                mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = user.userId, bType = 1, orderId = mRequestBean_CancleOrder.orderId,
                        price = buyCommis.toFloat(), notesDec = "${subTitle}订单扣除手续费${buyCommis}元",
                        orderDec = subTitle, payType = 2))

                //给买方充值扣除手续费后的余额
                val balance = mBalanceRepository.findTop1ByUserId(wbOrder.userIdFromBuy!!)!!
                balance.balance = balance.balance!! + ((wbOrder.price - buyCommis).toFloat())
                mBalanceRepository.saveAndFlush(balance)

                //记录公司收入流水
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = "admin",
                        bType = 1,
                        price = buyCommis.toFloat(),
                        toOrFromUserId = user.userId,
                        orderId = wbOrder.orderId,
                        noteDec = "退款手续费",
                        bFType = 0
                ))
            } else if (wbOrder.userIdFromServer == user.userId) {
                //订单已支付 发起退款
                //卖方取消 不扣除手续费
                //记录公司支出流水 \
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = "admin",
                        bType = 0,
                        price = wbOrder.price,
                        toOrFromUserId = wbOrder.userIdFromBuy!!,
                        orderId = wbOrder.orderId,
                        noteDec = "订单退款",
                        bFType = 0
                ))
                //公司出账
                val u_w = mUser_WebRepository.findTop1ByUserId("admin")
                u_w?.balance = u_w?.balance!! - wbOrder.price
                mUser_WebRepository.saveAndFlush(u_w)

                //记录买方收入流水
                mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = wbOrder.userIdFromBuy!!, bType = 0, orderId = mRequestBean_CancleOrder.orderId,
                        price = wbOrder.price, notesDec = "${subTitle}订单退款收入${wbOrder.price}元",
                        orderDec = subTitle))


                //给买方充值余额
                val balance = mBalanceRepository.findTop1ByUserId(wbOrder.userIdFromBuy!!)!!
                balance.balance = balance.balance!! + (wbOrder.price)
                mBalanceRepository.saveAndFlush(balance)
//                when(wbOrder.orderType){
//                    0 ->{
//                        //技能取消
//
//                    }
//                    1 ->{
//                        //跑腿取消
//                        val balance = mBalanceRepository.findTop1ByUserId(user.userId)!!
//                        balance.balance = balance.balance!! + (wbOrder.price)
//                        mBalanceRepository.saveAndFlush(balance)
//                    }
//                    2 ->{
//                        //同城取消
//                    }
//
//                }


            } else {
                return "退款失败,退款双方异常".getErrorRespons()
            }

        }

        wbOrder.state = -1
        mOrderRepository.saveAndFlush(wbOrder)

        if (wbOrder.orderType == 0 && wbOrder?.buyFromDemand == true) {
            //需求发布方的购买技能 标注需求为服务中状态
            val demand = mDemandRepository.findTop1ById(wbOrder.demandId!!)
            demand?.demandState = -1
            mDemandRepository.saveAndFlush(demand)
        }
        return HttpResponse("")
    }


    /**
     * 获取轮播图
     */
    override fun getBanner(mRequestBean_GetBanner: RequestBean_GetBanner): HttpResponse<Any> {
        val list = mBannerRepository.findAll()
        list.forEach {
            if (it.businessValue.equals("")) {
                it.businessValue = null
            }
        }
        //PageRequest.of(1, 3, Sort.Direction.DESC, "id")
        return HttpResponse(list)
    }


}
