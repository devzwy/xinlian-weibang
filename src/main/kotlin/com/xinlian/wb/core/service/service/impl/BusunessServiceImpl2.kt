package com.xinlian.wb.core.service.service.impl

import com.qiniu.util.Auth
import com.xinlian.wb.core.config.OkHttpCli
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IBusunessService
import com.xinlian.wb.core.service.service.IBusunessService2
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.tabs.*
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.decrypt
import com.xinlian.wb.util.ktx.encrypt
import com.xinlian.wb.util.ktx.getErrorRespons
import com.xinlian.wb.util.ktx.getErrorRespons2
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*


@Service
class BusunessServiceImpl2 : IBusunessService2 {
    private val logger = LoggerFactory.getLogger(BusunessServiceImpl2::class.java)

    /**
     * 大标签操作表
     */
    @Autowired
    lateinit var mSkillParentTabRepository: SkillParentTabRepository

    /**
     * 服务规格表
     */
    @Autowired
    lateinit var mSpecsTabRepository: SpecsTabRepository

    /**
     * 注入的 第一个子标签表操作对象
     */
    @Autowired
    lateinit var mSubSkillTabRepository: SubSkillTabRepository

    /**
     * 用户表
     */
    @Autowired
    lateinit var userRepository: UserRepository

    /**
     * 身份证认证的错误缓存表
     */
    @Autowired
    lateinit var mIdentityCardAuthCacheRepository: IdentityCardAuthCacheRepository

    @Autowired
    lateinit var okHttpCli: OkHttpCli


    /**
     * 跑腿标签
     */
    @Autowired
    lateinit var mRunErrandsTagRepository: RunErrandsTagRepository

    /**
     * 商户表
     */
    @Autowired
    lateinit var mMerchanAuthRepository: MerchanAuthRepository

    /**
     * 跑腿代办表
     */
    @Autowired
    lateinit var mUserRunErrandRepository: UserRunErrandRepository

    /**
     * 送货地址表
     */
    @Autowired
    lateinit var mAddressRepository: AddressRepository

    /**
     * 注入的 订单表操作对象
     */
    @Autowired
    lateinit var mOrderRepository: OrderRepository


    @Autowired
    lateinit var busunessService: IBusunessService


    @Autowired
    lateinit var mUserSkillRepository: UserSkillRepository

    /**
     * 订单评价表
     */
    @Autowired
    lateinit var mOrderCommRepository: OrderCommRepository

    /**
     * 注入的 技能留言表操作对象
     */
    @Autowired
    lateinit var mSkillMessageRepository: SkillMessageRepository


    /**
     * 注入的 User表操作对象
     */
    @Autowired
    lateinit var mUserRepository: UserRepository
    /**
     * 搜索技能
     */
    override fun searchSkills(mReqBeanSearchSkills: ReqBeanSearchSkills, token: String): HttpResponse<Any> {
        val skills = mUserSkillRepository.findAll("%${mReqBeanSearchSkills.keyWord}%",mReqBeanSearchSkills.lat.toFloat(),mReqBeanSearchSkills.lng.toFloat(),(mReqBeanSearchSkills.page - 1) * mReqBeanSearchSkills.number,mReqBeanSearchSkills.number)
        skills.forEach {
            //            it.isLiked = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(getUserIdByToken(token), it.user?.userId?:"")!=null
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

            it.user.also {
                it!!
                if (it.indentity != null) {
                    it.indentity!!.idemyityCardNumber = it.indentity!!.idemyityCardNumber.toString().encrypt()
                    it.indentity!!.idemyityName = it.indentity!!.idemyityName.toString().encrypt()
                }
                val b = mMerchanAuthRepository.findTop1ByUserId(it.userId)
                val state = b?.verifyState ?: -1
                it.merchanAuthState = state
                it.merchanAuthBean = b
            }

        }
        return HttpResponse(skills)
    }


    /**
     * 获取同城、跑腿列表
     */
    override fun getAllRunErrand(mReq_GetAllRun: Req_GetAllRun, token: String): HttpResponse<Any?> {
        if (mReq_GetAllRun.page == 0) return "请求页数必须从1开始".getErrorRespons2()
        var userRunErrandListDB = listOf<UserRunErrand>()

        //0 - 跑腿代办 1 - 同城配送 2 - 全部
        //1 待付款  -1 已取消 2 已支付  -2 支付失败 3 订单完成 4 待服务(跑腿代办、同城配送) 5已取待送达(跑腿代办、同城配送) 6 已评价(双方已评
        when (mReq_GetAllRun.type) {
            0 -> {
                userRunErrandListDB = mUserRunErrandRepository.findAll(b_type = 0, page = (mReq_GetAllRun.page - 1) * mReq_GetAllRun.number, number = mReq_GetAllRun.number)
//                        userRunErrandListDB.filter {
//                    it.bType == 0 && mOrderRepository.findTop1ByOrderBusinessId(it.id.toString()).state==2
//                }
            }
            1 -> {
                userRunErrandListDB = mUserRunErrandRepository.findAll(b_type = 1, page = (mReq_GetAllRun.page - 1) * mReq_GetAllRun.number, number = mReq_GetAllRun.number)
//                userRunErrandListDB = userRunErrandListDB.filter {
//                    it.bType == 1 && mOrderRepository.findTop1ByOrderBusinessId(it.id.toString()).state ==2
//                }
            }
            2 -> {
                userRunErrandListDB = mUserRunErrandRepository.findAll(page = (mReq_GetAllRun.page - 1) * mReq_GetAllRun.number, number = mReq_GetAllRun.number)
//                userRunErrandListDB = mUserRunErrandRepository.findAll(mReq_GetAllRun.page - 1, mReq_GetAllRun.number)
//                userRunErrandListDB = userRunErrandListDB.filter {
//                    mOrderRepository.findTop1ByOrderBusinessId(it.id.toString()).state ==2
//                }
            }
            else -> {
                return "type字段传入有误".getErrorRespons2()
            }
        }
        userRunErrandListDB.forEach {
            it.user = userRepository.findTop1ByUserId(it.userId ?: "")?.apply {
                this.indentity = null
                this.password = null
                this.address = null
            }
            it.businessAddressBean = mAddressRepository.findTop1ById(it.businessAddressId ?: 0)
            it.addresBean = mAddressRepository.findTop1ById(it.addresId ?: 0)
            it.wbOrder = mOrderRepository.findTop1ByOrderBusinessId(it.id.toString())
        }
        return HttpResponse(userRunErrandListDB)
    }

    /**
     * 获取商户列表
     */
    override fun getMerchantList(token: String, mReqBeanGetAllMerchantList: ReqBeanGetAllMerchantList): HttpResponse<Any?> {
        if (mReqBeanGetAllMerchantList.page < 1) return "page必须从1开始".getErrorRespons2()
        return HttpResponse(mMerchanAuthRepository.findAllByVerifyState(page = (mReqBeanGetAllMerchantList.page - 1) * mReqBeanGetAllMerchantList.number, number = mReqBeanGetAllMerchantList.number))
    }

    /**
     * 获取七牛云token
     */
    override fun getQNToken(): HttpResponse<Any?> {
        return HttpResponse(Auth.create(Constant.OtherKey.QN_ACCESSKEY, Constant.OtherKey.QN_SECRETKEY).uploadToken(Constant.OtherKey.QN_BUCKET))
    }

    /**
     * 获取订单详情
     */
    override fun getOrderDetail(wbOrder: WBOrder, token: String): HttpResponse<Any?> {
        val uid = Helper.getUserIdByToken(token)
        return HttpResponse(wbOrder.let {
            val order = mOrderRepository.findTop1ByOrderId(wbOrder.orderId) ?: return "订单不存在".getErrorRespons2()
            if (order.userIdFromBuy != null) {
                order.userFromBuy = userRepository.findTop1ByUserId(order.userIdFromBuy!!)
            }
            if (order.userIdFromServer != null) {
                order.userFromServer = userRepository.findTop1ByUserId(order.userIdFromServer!!)
            }
            order.address = mAddressRepository.findTop1ById(order.sAddressId.toLong() ?: 0)
            if (order.orderType == 0) {
                //技能
                order.orderBusinessBean = mUserSkillRepository.findTop1ById(order.orderBusinessId?.toLong() ?: 0)
            }
            if (order.orderType == 1) {
                //跑腿
                order.orderBusinessBean = mUserRunErrandRepository.findTop1ById(order.orderBusinessId?.toLong()
                        ?: 0)?.let {
                    it.businessAddressBean = mAddressRepository.findTop1ById(it.businessAddressId ?: 0)
                    it.addresBean = mAddressRepository.findTop1ById(it.addresId ?: 0)
                    it
                }
            }
            if (order?.orderType == 2) {
                //同城
                order?.orderBusinessBean = mUserRunErrandRepository.findTop1ById(order.orderBusinessId?.toLong()
                        ?: 0).let {
                    it?.businessAddressBean = mAddressRepository.findTop1ById(it?.businessAddressId ?: 0)
                    it?.addresBean = mAddressRepository.findTop1ById(it?.addresId ?: 0)
                    it
                }
            }
            //查询当前用户是否已评价
            if (order.commId != null) {
                val comm = mOrderCommRepository.findTop1ByOrderId(order.orderId)
                if (comm != null) {
                    //买方
                    if (uid.equals(order.userIdFromBuy) && comm.commTimeFormBuy != null) {
                        order.commed = true
                    } else order.commed = uid.equals(order.userFromServer) && comm.commTimeFormServer != null
                }
            } else {
                order.commed = false
            }

            //给发单和接单的用户实体赋值
            val userFromServer = userRepository.findTop1ByUserId(order.userIdFromServer)
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

            val userFromBuy = userRepository.findTop1ByUserId(order.userIdFromBuy ?: "")
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


            order.userFromServer = userFromServer
            order.userFromBuy = userFromBuy

            //给orderBusinessBean赋值
            if (order.orderType == 0) {
                //技能
                order.orderBusinessBean = mUserSkillRepository.findTop1ById(order.orderBusinessId?.toLong() ?: 0).let {
                    it?.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it?.s_tag ?: 0L)
                    it?.pTagBean = mSkillParentTabRepository.findTop1ByParentId(it?.p_tag ?: 0L).let {
                        it?.subSkills = null
                        it
                    }
                    it
                }
            }
            if (order.orderType == 1) {
                //跑腿
                order.orderBusinessBean = mUserRunErrandRepository.findTop1ById(order?.orderBusinessId?.toLong()
                        ?: 0)?.let {
                    it.businessAddressBean = mAddressRepository.findTop1ById(it.businessAddressId ?: 0)
                    it.addresBean = mAddressRepository.findTop1ById(it.addresId ?: 0)
                    it
                }
            }
            if (order.orderType == 2) {
                //同城
                order.orderBusinessBean = mUserRunErrandRepository.findTop1ById(order.orderBusinessId?.toLong()
                        ?: 0).let {
                    it?.businessAddressBean = mAddressRepository.findTop1ById(it?.businessAddressId ?: 0)
                    it?.addresBean = mAddressRepository.findTop1ById(it?.addresId ?: 0)
                    it
                }
            }
            order.specsBean = mSpecsTabRepository.findTopBySpecsId(order.specsId ?: 0)

            //

            order
        })
    }


    /**
     * 获取商户详情
     */
    override fun getMerchantDetail(mMerchanAuthTab: MerchanAuthTab, token: String): HttpResponse<Any> {
        if (mMerchanAuthTab.userId == null) return HttpResponse(mMerchanAuthRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "商户信息不存在".getErrorRespons())
        return HttpResponse(mMerchanAuthRepository.findTop1ByUserId(mMerchanAuthTab.userId!!)
                ?: return "商户信息不存在".getErrorRespons())
    }


    /**
     * 商户入住/修改商户资料
     */
    override fun merchantAuth(mMerchanAuthTab: MerchanAuthTab, token: String): HttpResponse<Any> {
        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        if (mMerchanAuthTab.merchanTitle.isNullOrEmpty()) return "店铺名称为空".getErrorRespons()
        if (mMerchanAuthTab.merchanUserName.isNullOrEmpty()) return "联系人姓名为空".getErrorRespons()
        if (mMerchanAuthTab.merchanPhoneNumber.isNullOrEmpty()) return "联系人电话为空".getErrorRespons()


        if (mMerchanAuthTab.idemyityName.isNullOrEmpty()) return "实名认证的姓名为空".getErrorRespons()
        if (mMerchanAuthTab.idemyityCardNumber.isNullOrEmpty()) return "实名认证的证件号码为空".getErrorRespons()
        mMerchanAuthTab.idemyityName = mMerchanAuthTab.idemyityName!!.decrypt() ?: return "证件实名信息校验失败".getErrorRespons()
        mMerchanAuthTab.idemyityCardNumber = mMerchanAuthTab.idemyityCardNumber!!.decrypt()
                ?: return "证件实名信息校验失败".getErrorRespons()

        if (mMerchanAuthTab.merchanPhotos.isNullOrEmpty()) return "至少上传一张门头照".getErrorRespons()
        if (mMerchanAuthTab.merchanBaseTagId == null || mSkillParentTabRepository.findTop1ByParentId(mMerchanAuthTab.merchanBaseTagId!!) == null) return "服务品类不存在".getErrorRespons()
        if (mMerchanAuthTab.serviceTime.isNullOrEmpty()) return "服务时间有误".getErrorRespons()
        if (mMerchanAuthTab.city_merchan.isNullOrEmpty()) return "店铺地址为空".getErrorRespons()
        if (mMerchanAuthTab.addressDetail_Merchan.isNullOrEmpty()) return "店铺详细地址为空".getErrorRespons()
        if (mMerchanAuthTab.addressInfo.isNullOrEmpty()) return "店铺介绍为空".getErrorRespons()
        if (mMerchanAuthTab.serverBusinessLicenseType == null) return "营业执照类型为空".getErrorRespons()
        if (mMerchanAuthTab.serverBusinessLicenseType == 0) {
            if (mMerchanAuthTab.serverBusinessLicenseName.isNullOrEmpty()) return "营业执照名称为空".getErrorRespons()
            if (mMerchanAuthTab.serverBusinessLicenseNumber.isNullOrEmpty()) return "营业执照社会信用代码或注册号为空".getErrorRespons()
            if (mMerchanAuthTab.serverBusinessLicenseEndTime == null) return "营业执照有效期截止时间为空".getErrorRespons()
            if (mMerchanAuthTab.serverBusinessLicensePhotos.isNullOrEmpty()) return "至少上传一张营业执照照片".getErrorRespons()
        }
        if (mMerchanAuthTab.serverBusinessLicenseType == 1) {
            if (mMerchanAuthTab.serverPermitLicenseNmae.isNullOrEmpty()) return "许可证名称为空".getErrorRespons()
            if (mMerchanAuthTab.serverPermitLicenseRange.isNullOrEmpty()) return "许可证经营范围为空".getErrorRespons()
            if (mMerchanAuthTab.serverPermitLicenseEndTime == null) return "许可证有效期截止时间为空".getErrorRespons()
            if (mMerchanAuthTab.serverPermitLicensePhotos.isNullOrEmpty()) return "至少上传一张许可证照片".getErrorRespons()

        }


        if (user.indentity != null && (mMerchanAuthTab.idemyityName != user.indentity!!.idemyityName || mMerchanAuthTab.idemyityCardNumber != user.indentity!!.idemyityCardNumber)) {
            return "当前用户实名信息已存在，请确定传入的身份证号与姓名与之匹配".getErrorRespons()
        }
        //实名认证
        if (!userAuth(user, mMerchanAuthTab.idemyityName!!, mMerchanAuthTab.idemyityCardNumber!!)) return "实名认证失败，请重试".getErrorRespons()
        //将认证成功的ID写入
        mMerchanAuthTab.authId = user.indentity!!.id
        if (mMerchanAuthRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) != null && mMerchanAuthTab.merchanId == null) {
            return "您已认证，请勿重新认证".getErrorRespons()
        }
        if (mMerchanAuthTab.merchanId != null && mMerchanAuthRepository.findTop1ByMerchanId(mMerchanAuthTab.merchanId!!) == null) {
            return "编辑的商户信息不存在".getErrorRespons()
        }
        mMerchanAuthTab.userId = user.userId
        mMerchanAuthRepository.saveAndFlush(mMerchanAuthTab)
        return HttpResponse("")
    }

    /**
     * 获取同城跑腿标签
     */
    override fun getRunErrandsTags(token: String): HttpResponse<Any> {
        return HttpResponse(mRunErrandsTagRepository.findAll())
    }


    /**0
     * 发布跑腿代办同城配送订单
     */
    override fun createRunErrand(mUserRunErrand: UserRunErrand, token: String): HttpResponse<Any> {
        val sendUser = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mUserRunErrand.bType == null) return "发单类型为空".getErrorRespons()
        if (mUserRunErrand.bType == 1 && (mUserRunErrand.businessAddressId == null || mAddressRepository.findTop1ById(mUserRunErrand.businessAddressId!!) == null)) return "同城配送订单发货地址编号不能为空".getErrorRespons()
        if (mUserRunErrand.bType == 1 && (mUserRunErrand.intraType == null)) return "同城配送订单取送类型不能为空".getErrorRespons()


        if (mUserRunErrand.tradeNames.isNullOrEmpty()) return "购买内容为空".getErrorRespons()

        if (mUserRunErrand.tempoDiServizio == null) return "送达时间为空".getErrorRespons()

        if (mUserRunErrand.sendLat == null) return "发单人经纬度不能为空".getErrorRespons()
        if (mUserRunErrand.sendLng == null) return "发单人经纬度不能为空".getErrorRespons()

        if (mUserRunErrand.addresId == null || mAddressRepository.findTop1ById(mUserRunErrand.addresId!!) == null) return "收获地址编号为空获不存在".getErrorRespons()
        if (mUserRunErrand.price == null) return "配送费为空".getErrorRespons()
        mUserRunErrand.userId = sendUser.userId
        val bId = mUserRunErrandRepository.saveAndFlush(mUserRunErrand)
        val mWBOrder = WBOrder(orderType = if (mUserRunErrand.bType == 0) 1 else if (mUserRunErrand.bType == 1) 2 else return "发单类型错误".getErrorRespons(), orderBusinessId = bId.id.toString(), number = 1, sTime = mUserRunErrand.tempoDiServizio!!, sAddressId = mUserRunErrand.addresId!!.toInt(),
                orderDec = if (mUserRunErrand.bType == 0) "跑腿代办备注:${mUserRunErrand.decInfo}" else if (mUserRunErrand.bType == 1) "同城配送备注:${mUserRunErrand.decInfo}" else return "发单类型错误".getErrorRespons(), price = mUserRunErrand.price!!)
        return busunessService?.createOrder(
                mWBOrder, token
        )!!
    }

    /**
     * 计算价格
     */
    override fun calculatePrice(mCPrice: CPrice, token: String): HttpResponse<Any> {
        if (mCPrice.bType == null || (mCPrice.bType != 0 && mCPrice.bType != 1)) return "操作类型错误".getErrorRespons()
        //发货/取货地址
        val sendAdd = mAddressRepository.findTop1ById(mCPrice.businessAddressId
                ?: -1)

        if (mCPrice.bType == 0 && sendAdd == null) return "同城配送的取货地址为空或不存在".getErrorRespons()

        //收获地址
        val address = mAddressRepository.findTop1ById(mCPrice.addresId ?: -1)

        if (address == null) return "收货地址为空或不存在".getErrorRespons()

        if (mCPrice.bTime == null) return "送达时间不能为空".getErrorRespons()

//        val time_h = try {
//            SimpleDateFormat("HH").format(mCPrice.bTime)
//        } catch (e: Exception) {
//            return "送达时间传入有误".getErrorRespons()
//        }
//        val TH = time_h.toString().toInt()


        //计算距离和价格
        var price = 0
        var deliveryTime = "尽快送达"

        when {
            mCPrice.bType == 0 -> {
                //同城配送
                val km = Helper.getDistance(sendAdd!!.addressLat, sendAdd.addressLng, address.addressLat, address.addressLng) / 1000.0 //转换为km

                when (km) {
                    in 0..2 -> price = 14
                    in 2..3 -> price = 17
                    else -> {
                        if (km <= 0) {
                            price = 12
                        } else {
                            //大于3公里
                            price = (17 + ((km - 3) * 2)).toInt()
                        }
                    }
                }
                //选择了距离 但是没选时间  尽快送
                if (mCPrice.bTime.toInt() == -1) {
                    return HttpResponse(RPrice(price = price, distance = String.format("%.1f", (km))))
                }


                //选择了距离 也选择了时间
                var time_h = -1
                try {
                    time_h = SimpleDateFormat("HH").format(mCPrice.bTime).toInt()
                } catch (e: Exception) {
                    return "收货时间传入有误".getErrorRespons()
                }
                //根据时间 增加金额
                when (time_h) {

                    in 0..7 -> {
                        price += 40
                    }
                    in 7..22 -> {
                        price += 10
                    }
                    in 22..24 -> {
                        price += 5
                    }

                }
                return HttpResponse(RPrice(price = price, distance = String.format("%.1f", (km))))
            }
            mCPrice.bType == 1 -> {
                //跑腿代办
                if (sendAdd == null) {
                    //跑腿代办未传入购买地址 表示就近购买
                    if (mCPrice.bTime.toInt() == -1) {
                        //没有填写收货时间
                        price = 12
                    } else {
                        var time_h = -1
                        try {
                            time_h = SimpleDateFormat("HH").format(mCPrice.bTime).toInt()
                        } catch (e: Exception) {
                            return "收货时间传入有误".getErrorRespons()
                        }
                        //根据时间 增加金额
                        when (time_h) {

                            in 0..7 -> {
                                price = 40
                            }
                            in 7..22 -> {
                                price = 20
                            }
                            in 22..24 -> {
                                price = 25
                            }

                        }
                    }
                    return HttpResponse(RPrice(price = price, distance = "尽快送达"))
                }

                val km = Helper.getDistance(sendAdd.addressLat, sendAdd.addressLng, address.addressLat, address.addressLng) / 1000.0 //转换为km

                when (km) {
                    in 0..2 -> price = 14
                    in 2..3 -> price = 17
                    else -> {
                        if (km <= 0) {
                            price = 12
                        } else {
                            //大于3公里
                            price = (17 + ((km - 3) * 2)).toInt()
                        }
                    }
                }
                //选择了距离 但是没选时间  尽快送
                if (mCPrice.bTime.toInt() == -1) {
                    return HttpResponse(RPrice(price = price, distance = String.format("%.1f", (km))))

                }


                //选择了距离 也选择了时间
                var time_h = -1
                try {
                    time_h = SimpleDateFormat("HH").format(mCPrice.bTime).toInt()
                } catch (e: Exception) {
                    return "收货时间传入有误".getErrorRespons()
                }
                //根据时间 增加金额
                when (time_h) {

                    in 0..7 -> {
                        price += 40
                    }
                    in 7..22 -> {
                        price += 20
                    }
                    in 22..24 -> {
                        price += 25
                    }

                }
                return HttpResponse(RPrice(price = price, distance = String.format("%.1f", (km))))

            }

            else -> return "传入参数有误".getErrorRespons()


        }
    }

    /**
     * 抢单 跑腿代办
     */
    override fun grabUserRunErrandSheet(orderId: WBOrder, token: String): HttpResponse<Any> {
        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (!user.isDispatcher) return "请认证为派送员后再试".getErrorRespons()
        val order = mOrderRepository.findTop1ByOrderId(orderId.orderId) ?: return "订单不存在".getErrorRespons()

//        if (order.orderType != 1 && order.orderType != 2) {
//            return "无法进行该操作，仅支持跑腿代办抢单和同城配送订单".getErrorRespons()
//        }
        if (order.state != 2) {
            return "订单已取消或被抢走，下次要抓紧哦".getErrorRespons()
        }
        if (order.userIdFromBuy.equals(user.userId)) {
            return "不能抢自己发布的订单".getErrorRespons()
        }

        order.userIdFromServer = user.userId
        order.state = 4
        order.getOrderTime = Date().time
        mOrderRepository.saveAndFlush(order)
        return HttpResponse("抢单成功")
    }

    /**
     * 取货
     */
    override fun takeDelivery(orderId: WBOrder, token: String): HttpResponse<Any> {
        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        val order = mOrderRepository.findTop1ByOrderId(orderId.orderId) ?: return "订单不存在".getErrorRespons()
//        if (order.orderType != 1 && order.orderType != 2) {
//            return "无法进行该操作，仅支持跑腿代办抢单和同城配送订单".getErrorRespons()
//        }
        if (order.state != 4) {
            return "订单异常".getErrorRespons()
        }
        order.state = 5
        order.takeDeliveryTime = Date().time
        order.transactionCode = (1000..9999).random()
        mOrderRepository.saveAndFlush(order)
        return HttpResponse("取货成功")
    }

    /**
     * 用户认证
     */
    private fun userAuth(user: User, idemyityName: String, idemyityCardNumber: String): Boolean {

        logger.info("用户身份认证")


        //首先在当前用户查找是否有实名信息
        if (user.indentity != null) {
            //已认证 无需重复认证
            logger.info("用户实名信息已存在于一对一表中")
            return true
        }

        logger.info("查询缓存表")

        //用户实名信息不存在时在失败的缓存表中查询
        val bean = mIdentityCardAuthCacheRepository.findTopByCardNumberAndUserRelName(idemyityCardNumber, idemyityName)
        if (bean != null) {
            logger.info("曾认证失败的缓存表中存在数据，返回认证失败信息")
            return false
        }

        //如果还不存在时在第三方平台查询，查询到的结果根据校验状态存入不同的表  成功时存入用户一对一的实名信息表中  失败时存入缓存表

        logger.info("查询第三方平台，实名认证")
        val beanAuthYy = Helper.checkCardNumber(okHttpCli, RequestBean_AuthCard(idemyityCardNumber, idemyityName))

        if (beanAuthYy.code == 400100 && beanAuthYy.success == true) {
            logger.info("三方回传认证成功")
            //认证成功
            val ideBean = IdentityCardAuthenticationTab(idemyityCardNumber = idemyityCardNumber, idemyityName = idemyityName)

            user.indentity = ideBean
            ideBean.user = user

            userRepository.save(user)
            logger.info("认证信息写入用户一对一实名表成功")

            return true
        } else if (beanAuthYy.code == 400111) {
            logger.info("三方回传认证失败,网络异常")
            //认证失败 beanAuthYy.message  写入缓存表
            return false
        } else {
            //认证失败 beanAuthYy.message  写入缓存表
            mIdentityCardAuthCacheRepository.save(IdentityCardAuthCacheTab(userRelName = idemyityName, cardNumber = idemyityCardNumber))
            logger.info("认证失败信息写入失败缓存表成功")
            return false
        }
    }

}
