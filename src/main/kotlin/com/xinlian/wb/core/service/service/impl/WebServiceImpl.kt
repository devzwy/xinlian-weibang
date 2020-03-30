package com.xinlian.wb.core.service.service.impl

import cn.jpush.api.push.model.PushModel.gson
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IWebService
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.repositorys_web.*
import com.xinlian.wb.jdbc.tabs.*
import com.xinlian.wb.jdbc.tabs_web.*
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.redis.LogBean
import com.xinlian.wb.util.ApiTimer
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.encrypt
import com.xinlian.wb.util.ktx.getErrorRespons
import org.hibernate.query.internal.NativeQueryImpl
import org.hibernate.transform.Transformers
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import kotlin.random.Random


@Service
open class WebServiceImpl : IWebService {


    /**
     * 注入的 用户技能表操作对象
     */
    @Autowired
    lateinit var mUserSkillRepository: UserSkillRepository


    @Autowired
    lateinit var mWebBalanceNoteRepository: WebBalanceNoteRepository

    /**
     * 注入的 订单表操作对象
     */
    @Autowired
    lateinit var mOrderRepository: OrderRepository

    /**
     * 用户表
     */
    @Autowired
    lateinit var userRepository: UserRepository


    /**
     * 奖品表
     */
    @Autowired
    lateinit var mLuckDrawTabRepository: LuckDrawTabRepository

    /**
     * 公告栏f
     */
    @Autowired
    lateinit var mShowWebBoradRepository: ShowWebBoradRepository


    /**
     * 代理商表
     */
    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    /**
     * web首页侧边数据
     */
    @Autowired
    lateinit var mWebTabRepository: WebTabRepository

    /**
     * 省 表
     */
    @Autowired
    lateinit var mProvinceRepository: ProvinceRepository

    /**
     * 市 表
     */
    @Autowired
    lateinit var mCityRepository: CityRepository


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
     * 注册码
     */
    @Autowired
    lateinit var mRegisterCodeRepository: RegisterCodeRepository

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
     * 用户余额表
     */
    @Autowired
    lateinit var mBalanceRepository: BalanceRepository

    /**
     * 用户余额流水表
     */
    @Autowired
    lateinit var mBalanceNotesRepository: BalanceNotesRepository

    /**
     * 分成比例
     */
    @Autowired
    lateinit var mCommissionAllocationRepository: CommissionAllocationRepository

    @Autowired
    lateinit var mWithDrawalRepository: WithDrawalRepository

    private val logger = LoggerFactory.getLogger(WebServiceImpl::class.java)

    /**
     * 大标签操作表
     */
    @Autowired
    lateinit var mSkillParentTabRepository: SkillParentTabRepository

    @Autowired
    lateinit var mIdentityCardAuthenticationRepository: IdentityCardAuthenticationRepository


    @PersistenceContext
    lateinit var mEntityManager: EntityManager


    /**
     * 用户充值流水记录
     */
    @Transactional
    override fun userTopUpWater(mRequest_AddBalanceNoto: Request_AddBalanceNoto, token: String): HttpResponse<Any> {
        val webUserDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "权限不足".getErrorRespons()
        if (mRequest_AddBalanceNoto.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        when (webUserDB.userId) {
            "admin" -> {
                //管理员
                val list = mBalanceNotesRepository.findAll(mRequest_AddBalanceNoto.page - 1, mRequest_AddBalanceNoto.number)
                list.forEach {
                    it.user = userRepository.findTop1ByUserId(it.userId)
                }
                return HttpResponse(list)
            }
            else -> {
                //代理商
                var stringBuilder = StringBuffer()
                var aInt = 0
                stringBuilder.append("SELECT * FROM balance_notes WHERE b_type=2 And (user_id=")
                webUserDB.boundAreaListStr!!.split(",").forEach { b ->

                    userRepository.findAll().filter {
                        b.contains(it.boundCountId.toString())
                    }.forEach {
                        aInt++
                        stringBuilder.append("'${it.userId}'" + " OR user_id=")
                    }
                }
                if (aInt == 0) {
                    logger.info("当前代理商没有服务者或服务者没有充值记录")
                    return HttpResponse(arrayListOf<BalanceNotes>())
                }
                stringBuilder = StringBuffer(stringBuilder.substring(0, stringBuilder.length - 11)).append(")")
                stringBuilder.append("ORDER BY create_time ASC limit ${mRequest_AddBalanceNoto.page - 1}, ${mRequest_AddBalanceNoto.number}")
                val query = mEntityManager.createNativeQuery(stringBuilder.toString(), BalanceNotes::class.java)
                query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.TO_LIST)
                val aa = Gson().toJson(query.resultList)
                val b = "[${aa.replace("[", "").replace("]", "")}]"
                val list: List<BalanceNotes> = gson.fromJson(b, object : TypeToken<List<BalanceNotes>>() {}.getType())
                return HttpResponse(list)
            }
        }
    }

    /**
     * 分成流水记录查询
     */
    override fun dividedWaterSearch(mRequest_DivWater: Request_DivWater, token: String): HttpResponse<Any> {
        val webUserDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "权限不足".getErrorRespons()
        if (mRequest_DivWater.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        when (webUserDB.userId) {
            "admin" -> {
                //管理员

                val waters = if (mRequest_DivWater.state == 2) {
                    val list = mWebBalanceNoteRepository.findAll(page = mRequest_DivWater.page - 1, number = mRequest_DivWater.number)
                    list.forEach {
                        it.userBean = mUser_WebRepository.findTop1ByUserId(it.userId)
                        it.toOrFromUserBean = if (it.bFType == 0) userRepository.findTop1ByUserId(it.toOrFromUserId) else mUser_WebRepository.findTop1ByUserId(it.toOrFromUserId)
                    }
                    list
                } else {
                    val list = mWebBalanceNoteRepository.findAllByBType(mRequest_DivWater.state, page = mRequest_DivWater.page - 1, number = mRequest_DivWater.number)
                    list.forEach {
                        it.userBean = mUser_WebRepository.findTop1ByUserId(it.userId)
                        it.toOrFromUserBean = if (it.bFType == 0) userRepository.findTop1ByUserId(it.toOrFromUserId) else mUser_WebRepository.findTop1ByUserId(it.toOrFromUserId)
                    }
                    list
                }
                return HttpResponse(waters)
            }
            else -> {
                //代理商
                val waters = if (mRequest_DivWater.state == 2) {
                    val list = mWebBalanceNoteRepository.findAllByUserId(userId = webUserDB.userId, page = mRequest_DivWater.page - 1, number = mRequest_DivWater.number)
                    list.forEach {
                        it.userBean = mUser_WebRepository.findTop1ByUserId(it.userId)
                        it.toOrFromUserBean = if (it.bFType == 0) userRepository.findTop1ByUserId(it.toOrFromUserId) else mUser_WebRepository.findTop1ByUserId(it.toOrFromUserId)
                    }
                    list
                } else {
                    val list = mWebBalanceNoteRepository.findAllByUserIdAndBType(userId = webUserDB.userId, bType = mRequest_DivWater.state, page = mRequest_DivWater.page - 1, number = mRequest_DivWater.number)
                    list.forEach {
                        it.userBean = mUser_WebRepository.findTop1ByUserId(it.userId)
                        it.toOrFromUserBean = if (it.bFType == 0) userRepository.findTop1ByUserId(it.toOrFromUserId) else mUser_WebRepository.findTop1ByUserId(it.toOrFromUserId)
                    }
                    list
                }
                return HttpResponse(waters)
            }
        }
    }

    /**
     * 提现成功
     */
    override fun markWithdrawal(mReqBeanMarkWithdrawal: ReqBeanMarkedWithdrawal, token: String): HttpResponse<Any> {
        mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token)) ?: return "没有操作权限".getErrorRespons()
        if (mReqBeanMarkWithdrawal.id == null) return "必填参数为空".getErrorRespons()
        if (mReqBeanMarkWithdrawal.state == null) return "必填参数为空".getErrorRespons()

        if (mReqBeanMarkWithdrawal.state == 2 && mReqBeanMarkWithdrawal.errMsg == null) return "提现失败的原因不能为空".getErrorRespons()

        return HttpResponse(mWithDrawalRepository.saveAndFlush(mWithDrawalRepository.findTop1ById(mReqBeanMarkWithdrawal.id)?.apply {
            state = mReqBeanMarkWithdrawal.state
            errMsg = mReqBeanMarkWithdrawal.errMsg
        } ?: return "记录不存在".getErrorRespons()))
    }

    override fun addBalance(mAddBalanceBean: AddBalanceBean, token: String): HttpResponse<Any> {
        if (mAddBalanceBean.price == null || mAddBalanceBean.price!! <= 0) return "添加的金额为空".getErrorRespons()

        if (mAddBalanceBean.userId == null) {
            //给管理员自己添加金额  没有充值记录
            val uw = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                    ?: return "非法操作,权限不足".getErrorRespons()
            uw.balance = uw.balance!!.toFloat() + (mAddBalanceBean.price!!.toFloat())
            mUser_WebRepository.saveAndFlush(uw)
            return HttpResponse("管理员余额变动成功")
        }
        //给用户充值金额

        val u_b = mBalanceRepository.findTop1ByUserId(mAddBalanceBean.userId!!) ?: return "用户不存在".getErrorRespons()
        u_b.balance = (u_b.balance ?: 0.0) + (mAddBalanceBean.price!!.toFloat())
        mBalanceRepository.saveAndFlush(u_b)

        mBalanceNotesRepository.saveAndFlush(
                BalanceNotes(
                        userId = mAddBalanceBean.userId!!,
                        bType = 2,
                        orderId = "",
                        orderDec = "管理员发放红包奖励",
                        price = mAddBalanceBean.price!!.toFloat(), payType = 0))
        return HttpResponse("用户余额变动成功")
    }

    /**
     * 查询收获码
     */
    override fun getTransactionCode(mRequestBean_CancleOrder: RequestBean_CancleOrder, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons()
        if (mRequestBean_CancleOrder.orderId.isEmpty()) return "参数不能为空".getErrorRespons()
        if (!user.userId.equals("admin"))
            return "操作失败，没有权限".getErrorRespons()
        val order = mOrderRepository.findTop1ByOrderId(mRequestBean_CancleOrder.orderId)
                ?: return "订单不存在".getErrorRespons()
        logger.info("查询的订单类型:${order.orderType}")
        if (order.orderType != 1 && order.orderType != 2)
            return "当前订单没有收获码".getErrorRespons()//不是跑腿代办订单，没收获码

        return HttpResponse(order.transactionCode ?: return "当前订单还未生成收获码".getErrorRespons())
    }

    /**
     * 商户审核
     */
    override fun verMerchant(mRequest_Web_Merchan2: Request_Web_Merchan2, token: String): HttpResponse<Any> {
        if (mRequest_Web_Merchan2.merchanId == null) return "商户信息不存在".getErrorRespons()
        if (mRequest_Web_Merchan2.bType == null) return "操作类型有误".getErrorRespons()
        val mBena = mMerchanAuthRepository.findTop1ByMerchanId(mRequest_Web_Merchan2.merchanId!!)
                ?: return "商户信息不存在".getErrorRespons()
        if (mRequest_Web_Merchan2.bType == 0) {
            mBena.verifyState = 2
            mBena.verifyTime = Date().time
        }
        if (mRequest_Web_Merchan2.bType == 1) {
            if (mRequest_Web_Merchan2.errMsg.isNullOrEmpty()) {
                return "拒绝原因为空".getErrorRespons()
            }
            mBena.verifyState = 1
            mBena.verifyDec = mRequest_Web_Merchan2.errMsg!!
        }
        mBena.verifyUserId = Helper.getUserIdByToken(token)
        mMerchanAuthRepository.saveAndFlush(mBena)
        return HttpResponse("")

    }

    /**
     * 获取类型 0-获取全部商户 1-获取未审核商户 2-获取审核通过用户 3-获取审核被拒用户
     */
    override fun getMerchantList(mRequest_Web_Merchan: Request_Web_Merchan, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mRequest_Web_Merchan.bType == null) {
            return "筛选类型不能为空".getErrorRespons()
        }

        //管理员可获取所有服务商户列表
        if (user.userId.equals("admin")) {
            when (mRequest_Web_Merchan.bType) {
                0 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        true
                    })
                }
                1 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState == 0
                    })
                }
                2 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState == 2
                    })
                }
                3 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState == 1
                    })
                }
                else -> {
                    return "未知的筛选类型".getErrorRespons()
                }

            }
        } else {
            //代理商代理的区域

            when (mRequest_Web_Merchan.bType) {
                0 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        !user.boundAreaListStr!!.contains(userRepository.findTop1ByUserId(it.userId!!)!!.boundCountId.toString())
                    })
                }
                1 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState != 0 && !user.boundAreaListStr!!.contains(userRepository.findTop1ByUserId(it.userId!!)!!.boundCountId.toString())
                    })
                }
                2 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState != 2 && !user.boundAreaListStr!!.contains(userRepository.findTop1ByUserId(it.userId!!)!!.boundCountId.toString())
                    })
                }
                3 -> {
                    return HttpResponse(mMerchanAuthRepository.findAll().filter {
                        val identity = getAuthBean(it)
                        it.merchanBaseTagBean = mSkillParentTabRepository.findTop1ByParentId(it.merchanBaseTagId!!).apply {
                            this?.subSkills = null
                        }
                        it.idemyityCardNumber = identity?.idemyityCardNumber?.encrypt() ?: ""
                        it.idemyityName = identity?.idemyityName?.encrypt() ?: ""
                        it.verifyState != 1 && !user.boundAreaListStr!!.contains(userRepository.findTop1ByUserId(it.userId!!)!!.boundCountId.toString())
                    })
                }
                else -> {
                    return "未知的筛选类型".getErrorRespons()
                }

            }
        }
        //代理商可获取代理区域的服务商户列表
    }

    private fun getAuthBean(mMerchanAuthTab: MerchanAuthTab): IdentityCardAuthenticationTab? = mIdentityCardAuthenticationRepository.findTop1ByUser(userRepository.findTop1ByUserId(mMerchanAuthTab.userId!!)!!)

    /**
     * 删除注册码
     */
    override fun deleteRegisterCode(mRequestBean_RegisterCodeDetail: RequestBean_RegisterCodeDetail, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mRequestBean_RegisterCodeDetail.registerId.isNullOrEmpty()) {
            return "缺少必填参数".getErrorRespons()
        }
        val list = mRegisterCodeRepository.findAllByCreatUserId(user.userId)
        if (list == null || list.size == 0) {
            return "您还没有生成注册码".getErrorRespons()
        }
        list.forEach {
            val a = it
            mRequestBean_RegisterCodeDetail.registerId.split(",").forEach {
                if (it.isNotEmpty() && mRegisterCodeRepository.findTop1ByRegisterId(it) == null) return "注册码格式有误或不存在".getErrorRespons()
                if (it.isNotEmpty() && mRequestBean_RegisterCodeDetail.registerId == a.registerId) {

                    val registerCode = mRegisterCodeRepository.findTop1ByRegisterId(mRequestBean_RegisterCodeDetail.registerId)!!
                    if (registerCode.registerCodeType == 0) { //一级代理商
                        val a: Array<String> = registerCode.boundAreaListSrt.split(",").toTypedArray()
                        for (i in a.indices) { //根据绑定的区 打开标记
                            if (!a[i].isEmpty()) {
                                val bb = mCountyRepository.findTop1ByCountyId(java.lang.Long.valueOf(a[i]))
                                bb!!.isAgent = false
                                mCountyRepository.saveAndFlush(bb)
                                logger.info("打开" + bb + "区域的已代理标记")
                            }
                        }
                    } else if (registerCode.registerCodeType == 1) { //二级代理商
                        //根据绑定的区 打开标记
                        val a: Array<String> = registerCode.boundAreaListSrt.split(",").toTypedArray()
                        for (i in a.indices) { //根据绑定的区 打开标记
                            if (!a[i].isEmpty()) {
                                val cc = mTownRepository.findTop1ByTownId(java.lang.Long.valueOf(a[i]))
                                cc!!.isAgent = false
                                mTownRepository.saveAndFlush(cc)
                                logger.info("打开" + cc + "区域的已代理标记")
                            }
                        }
                    }

                    mRegisterCodeRepository.deleteByRegisterId(mRequestBean_RegisterCodeDetail.registerId)
                }
            }
        }
        return HttpResponse("")
    }

    /**
     *获取用户列表
     */
    override fun getAllUsers(mRequestBean_GetAllSkill: RequestBean_GetAllSkill, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mRequestBean_GetAllSkill.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }


        val pageable = PageRequest.of(mRequestBean_GetAllSkill.page - 1, mRequestBean_GetAllSkill.number);
        val users = userRepository.findAll(pageable)

        if (user.userType == 0) {
            //管理员返回所有用户
            return HttpResponse(UsersBean(userRepository.findAll().size, users.content))
        } else {
            val uList = arrayListOf<User>()
            user.boundAreaListStr?.split(",")?.forEach {
                val id = it
                if (it.isNotEmpty()) {
                    users.content.forEach {
                        if (it.boundCountId == id.toLong()) {
                            uList.add(it)
                        }
                    }
                }
            }
            return HttpResponse(UsersBean(userRepository.findAll().size, uList))
        }
    }

    /**
     * 获取代理商列表
     */
    override fun getAgentList(mRequestBean_List: RequestBean_List, token: String): HttpResponse<Any> {

        if (mRequestBean_List.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }

        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        val pageable = PageRequest.of(mRequestBean_List.page - 1, mRequestBean_List.number);
        val list = mUser_WebRepository.findAll(pageable).toMutableList()

        if (user.userType == 0) {

            list.filter {
                if (it.userId.equals("admin")) {
                    true
                } else {
                    val sb = StringBuffer()
                    for (a in it.boundAreaListStr!!.split(",")) {
                        if (a.isNotEmpty()) {
                            //根据代理商类别查询城市表或者地区表
                            sb.append(if (it.userType == 1) mCountyRepository.findTop1ByCountyId(a.toLong())?.name else if (it.userType == 2) mTownRepository.findTop1ByTownId(a.toLong())!!.name else "").append(",")
                        }
                    }
                    it.boundAreaListBean = if (sb.length > 0) sb.toString().substring(0, sb.toString().length - 1) else sb.toString()

                    it.userType != 0
                }

            }
            return HttpResponse(AgentListBean(mUser_WebRepository.findAll().size, list))
        }
        //管理员获取所有代理商列表

        else if (user.userType == 1) {
            list.filter {
                if (it.userId.equals("admin")) {
                    true
                } else {
                    val sb = StringBuffer()
                    for (a in it.boundAreaListStr!!.split(",")) {
                        if (a.isNotEmpty()) {
                            //根据代理商类别查询城市表或者地区表
                            sb.append(if (it.userType == 1) mCountyRepository.findTop1ByCountyId(a.toLong())?.name else if (it.userType == 2) mTownRepository.findTop1ByTownId(a.toLong())!!.name else "").append(",")
                        }
                    }
                    it.boundAreaListBean = if (sb.length > 0) sb.toString().substring(0, sb.toString().length - 1) else sb.toString()
                    !(it.userType == 2)
                }
            }
            //一级代理商获取二级代理商列表
            return HttpResponse(AgentListBean(mUser_WebRepository.findAll().size, list))
        } else return "没有权限".getErrorRespons()
    }

    /**
     * 修改个人资料
     */
    override fun updateUserInfo(userWeb: User_Web, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (userWeb.phoneNumber.isNotEmpty()) {
            //电话号码
            user.phoneNumber = userWeb.phoneNumber
        }
        if (userWeb.userName != null && userWeb.userName!!.isNotEmpty()) {
            //用户名
            user.userName = userWeb.userName
        }

        if (userWeb.sex != null) {
            //性别
            user.sex = userWeb.sex
        }

        if (userWeb.userLogo != null && userWeb.userLogo!!.isNotEmpty()) {
            //用户头像
            user.userLogo = userWeb.userLogo
        }

        if (userWeb.realName.isNotEmpty()) {
            //真实姓名
            user.realName = userWeb.realName
        }
        mUser_WebRepository.saveAndFlush(user)
        return HttpResponse("")
    }

    /**
     * 删除封禁代理商
     */
    override fun delOrBanAgentUser(userWeb: RquestBean_DelAgent, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (user.userType != 0) return "权限不足".getErrorRespons()
        if (userWeb.userId.isNotEmpty()) {
            val uAgent = mUser_WebRepository.findTop1ByUserId(userWeb.userId)
                    ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

            if (userWeb.bType == 0) {
                //删除
                return "无法删除代理商".getErrorRespons()
            } else if (userWeb.bType == 1) {
                //封禁
                uAgent.isBan = true
                mUser_WebRepository.saveAndFlush(uAgent)
                return HttpResponse("")
            } else if (userWeb.bType == 2) {
                //解封
                uAgent.isBan = false
                mUser_WebRepository.saveAndFlush(uAgent)
                return HttpResponse("")
            } else {
                return "参数错误".getErrorRespons()
            }


        }
        return "操作失败".getErrorRespons()
    }

    /**
     * 删除用户
     */
    override fun deleteOrBanUser(mRequestBean_DeleteOrBan: RequestBean_DeleteOrBan, token: String): HttpResponse<Any> {
//        val uid = Helper.getUserIdByToken(token)
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (user.userType != 0) return "权限不足".getErrorRespons()
        val dbUser = userRepository.findTop1ByPhoneNumber(mRequestBean_DeleteOrBan.phoneNumber)
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        if (mRequestBean_DeleteOrBan.doType == 0) {
            //封禁
            dbUser.isBan = true
            userRepository.save(dbUser)
            //清除token
            RedisUtil.del("${Constant.RedisKey.USER_TOKEN}${dbUser.userId}")
            return HttpResponse("")

        } else if (mRequestBean_DeleteOrBan.doType == 1) {
            //删除用户
            //清除token
            RedisUtil.del("${Constant.RedisKey.USER_TOKEN}${dbUser.userId}")
            //清除余额表
            mBalanceRepository.deleteByUserId(dbUser.userId)
            //清除用户
            userRepository.delete(dbUser)
            return HttpResponse("")
        } else if (mRequestBean_DeleteOrBan.doType == 2) {
            //解封
            dbUser.isBan = false
            userRepository.save(dbUser)
            return HttpResponse("")

        } else {
            return "未知的操作类型".getErrorRespons()
        }
    }

    /**
     * 获取首页菜单
     */
    override fun getWebHomeTab(token: String): HttpResponse<Any> {
        val webUser = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        val tabs = mWebTabRepository.findAllByPermission(webUser.userType).filter {
            if (it.parentTabId == null) {
                it.subTabList = mWebTabRepository.findAllByParentTabId(it.id ?: 0)
                true
            } else {
                false
            }
        }
        return HttpResponse(tabs)
    }

    /**
     * 修改首页菜单
     */
    override fun updateWebHomeTab(mre: RequestBean_DeleteWebTab, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (user.userType != 0) return "权限不足".getErrorRespons()
        if (mre.tabId == null || mre.title == null) {
            return "必填参数为空".getErrorRespons()
        }
        val tab = mWebTabRepository.findTop1ById(mre.tabId!!)
                ?: return "菜单不存在".getErrorRespons()
        tab.title = mre.title!!
        tab.url = mre.url ?: ""
        if (mre.parentTabId != null) {
            tab.parentTabId = mre.parentTabId
        }
        mWebTabRepository.saveAndFlush(tab)
        return HttpResponse("")
    }

    /**
     * 删除首页菜单
     */
    override fun deleteWebHomeTab(mre: RequestBean_DeleteWebTab, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (user.userType != 0) return "权限不足".getErrorRespons()
        if (mre.tabId == null) {
            return "必填参数为空".getErrorRespons()
        }

        mWebTabRepository.deleteAllByIdOrParentTabId(mre.tabId
                ?: return "菜单不存在".getErrorRespons(), mre.tabId)

        return HttpResponse("")
    }

    /**
     * 添加首页侧边栏
     */
    override fun addWebHomeTab(webTabTitleBean: WebTabTitleBean, token: String): HttpResponse<Any> {
        val user_web = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (user_web.userType > 0) {
            return "没有操作权限".getErrorRespons()
        }
        if (webTabTitleBean.permission == -1) {
            return "权限为空".getErrorRespons()
        }
//        if (webTabTitleBean.url.isEmpty()) {
//            return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "跳转路径为空", data = null)
//        }
        if (webTabTitleBean.title.isEmpty()) {
            return "显示标题为空".getErrorRespons()
        }
        if (webTabTitleBean.parentTabId != null && mWebTabRepository.findTop1ById(webTabTitleBean.parentTabId
                        ?: 0) == null) {
            return "父菜单不存在".getErrorRespons()
        }

        mWebTabRepository.saveAndFlush(webTabTitleBean)

        return HttpResponse("")

    }

    /**
     * 获取首页菜单详情
     */
    override fun getHomeTabDetail(mRequestBean_DeleteWebTab: RequestBean_DeleteWebTab, token: String): HttpResponse<Any> {
        mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (mRequestBean_DeleteWebTab.tabId == null) {
            return "菜单不存在".getErrorRespons()
        }

        val web = mWebTabRepository.findTop1ById(mRequestBean_DeleteWebTab.tabId!!)
                ?: return "菜单不存在".getErrorRespons()
        web.subTabList = mWebTabRepository.findAllByParentTabId(mRequestBean_DeleteWebTab.tabId!!)
        return HttpResponse(web)
    }

    /**
     * 获取注册码详细数据
     */
    override fun getRegisterCodeDetail(mRequestBean_RegisterCodeDetail: RequestBean_RegisterCodeDetail, token: String): HttpResponse<Any?> {
        if (mRequestBean_RegisterCodeDetail.registerId.isEmpty()) {
            return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "注册码ID为空", data = null)
        }
        return HttpResponse(mRegisterCodeRepository.findTop1ByRegisterId(mRequestBean_RegisterCodeDetail.registerId).also {
            val regB = it!!
            val list = regB.boundAreaList?.toMutableList() ?: arrayListOf()
            regB.boundAreaListSrt.split(",").forEach {
                if (regB.registerCodeType == 0 && it.isNotEmpty()) {
                    //一级注册码
                    val b = mCountyRepository.findTop1ByCountyId(it.toLong())!!
                    list.add(BoundAreaBean(b.name, b.countyId))
                }
                if (regB.registerCodeType == 1 && it.isNotEmpty()) {
                    //二级注册码
                    val b = mTownRepository.findTop1ByTownId(it.toLong())!!
                    list.add(BoundAreaBean(b.name, b.countyId))
                }

            }
            it.boundAreaList = list

        })
    }

    /**
     * 获取有效注册码
     */
    override fun getRegisterCode(token: String?): HttpResponse<Any> {
        val list = mRegisterCodeRepository.findAllByCreatUserId(Helper.getUserIdByToken(token!!))
                ?: arrayListOf<RegisterCode>()
        list.filter {

            val regB = it
            val list = regB.boundAreaList?.toMutableList() ?: arrayListOf()
            regB.boundAreaListSrt.split(",").forEach {
                if (regB.registerCodeType == 0 && it.isNotEmpty()) {
                    //一级注册码
                    val b = mCountyRepository.findTop1ByCountyId(it.toLong())!!
                    list.add(BoundAreaBean(b.name, b.countyId))
                }
                if (regB.registerCodeType == 1 && it.isNotEmpty()) {
                    //二级注册码
                    val b = mTownRepository.findTop1ByTownId(it.toLong())!!
                    list.add(BoundAreaBean(b.name, b.countyId))
                }

            }
            it.boundAreaList = list


            false
        }
        return HttpResponse(list)

    }

    /**
     * 获取省市区信息
     */
    @ApiTimer
//    @Cacheable(value = arrayOf("AA"), key = "'key_deptList'")
    override fun getCityList(token: String?): HttpResponse<Any> {
        val userWeb = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token!!))
        if (userWeb?.userType == 0) {
            //管理员返回所有省市区信息
            val list = arrayListOf<Province>()
            val startTime = Date().time
            val dbProvinces = mProvinceRepository.findAll()
            for (pro in dbProvinces.withIndex()) {

                val citys = mCityRepository.findAllByProvinceId(pro.value.provinceId)!!
                for (city in citys.withIndex()) {
                    val countiess = mCountyRepository.findAllByCityId(city.value.cityId)
                    citys[city.index].countyList = countiess
                }
                pro.value.cityList = citys
                list.add(pro.value)
            }
            logger.info("查询耗时:${Date().time - startTime} ms")
            return HttpResponse(list)
        }
        if (userWeb?.userType == 1) {
            //查询该代理商代理的区域城市信息
            val list = arrayListOf<County>()
            userWeb.boundAreaListStr?.split(",")?.forEach {
                //查询城市下所有的区
                if (it.isNotEmpty()) {
                    val county = mCountyRepository.findTop1ByCountyId(it.toLong())
                    val towns = mTownRepository.findAllByCountyId(it.toLong())
                    county?.townList = towns
                    list.add(county!!)
                }
            }
            return HttpResponse(list)
        }
        return "没有获取权限".getErrorRespons()

    }

    /**
     * 生成注册码
     */
    override fun creatRegisterCode(mRequestBean_RegisterCode: RequestBean_RegisterCode, token: String): HttpResponse<Any> {
        val webUser = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
//        if (webUser.userType == 0 && mRequestBean_RegisterCode.bType == -1) {
//            return HttpResponse(code = Constant.Code.ERROR__DEFAULT, message = "参数为空", data = null)
//        }
        if (mRequestBean_RegisterCode.boundAreaListSrt.isEmpty()) {
            return "绑定区域为空".getErrorRespons()
        }

        if (webUser.userType == 2) {
            return "二级代理商无法生成注册码".getErrorRespons()
        }


        if (webUser.userType == 0) {
            //管理员生成一级代理商注册码

            val reqBoundCountStr = mRequestBean_RegisterCode.boundAreaListSrt.split(",")

            reqBoundCountStr.forEach {
                if (it.isNotEmpty()) {
                    val county = mCountyRepository.findTop1ByCountyId(it.toLong())
                    if (county == null || county.isAgent) {
                        return "代理的城市信息不存在，或该地区已存在同级别代理商".getErrorRespons()
                    }
                }
            }
//
//                //生成一级代理商注册码
            val registerId = UUID.randomUUID().toString()
            mRegisterCodeRepository.saveAndFlush(RegisterCode(registerId = registerId, creatUserId = webUser.userId,
                    boundAreaListSrt = mRequestBean_RegisterCode.boundAreaListSrt, registerCodeType = 0))
//                //标记当前城市为已代理
            reqBoundCountStr.forEach {
                if (it.isNotEmpty()) {
                    val county = mCountyRepository.findTop1ByCountyId(it.toLong())
                    county!!.isAgent = true
                    mCountyRepository.saveAndFlush(county)
                }
            }
            RedisUtil.set(Constant.RedisKey.REGISTERCODE + "$registerId", "$registerId", Constant.finalParams.REGISTER_CODE_EXPIRETIME)
            return HttpResponse("")
        } else if (webUser.userType == 1) {
            if (mRequestBean_RegisterCode.proportion == null) {
                return "与代理商的分成比例未设置".getErrorRespons()
            }
            //生成二级代理商注册码
            return creat2Agent(webUser, mRequestBean_RegisterCode)
        } else {
            return "生成失败,检查必填字段".getErrorRespons()
        }


    }

    private fun creat2Agent(webUser: User_Web, mRequestBean_RegisterCode: RequestBean_RegisterCode): HttpResponse<Any> {

        //获取当前一级代理商代理的区域
        if (webUser.boundAreaListStr.isNullOrEmpty()) {
            return "代理商未代理任何区域，无法开设二级代理".getErrorRespons()
        }

        val sb = StringBuffer()
        //查询当前用户绑定的区里面的小镇
        webUser.boundAreaListStr?.split(",")?.forEach {
            if (it.isNotEmpty()) {
                mTownRepository.findAllByCountyId(it.toLong())?.forEach {
                    sb.append(it.townId).append(",")
                }
            }
        }
        //检查传入的需要开通第二级区域ID是否包括在一级代理商的名下
        mRequestBean_RegisterCode.boundAreaListSrt.split(",").forEach {

            if (it.isNotEmpty()) {
                if (!sb.contains(it)) {
                    //如果有一个城市ID没包括则返回失败
                    return "您无法为该地区添加二级代理商".getErrorRespons()
                }
                val town = mTownRepository.findTop1ByTownId(it.toLong())
                if (town == null || town.isAgent) {
                    return "代理的城市信息不存在或该地区已存在同级别代理商".getErrorRespons()
                }
            }
        }
        //生成二级代理商注册码
        val registerId = UUID.randomUUID().toString()
        mRegisterCodeRepository.saveAndFlush(RegisterCode(registerId = registerId, creatUserId = webUser.userId,
                boundAreaListSrt = mRequestBean_RegisterCode.boundAreaListSrt,
                proportion = mRequestBean_RegisterCode.proportion, registerCodeType = 1))
        //标记当前小镇已代理
        mRequestBean_RegisterCode.boundAreaListSrt.split(",").forEach {
            if (it.isNotEmpty()) {
                val town = mTownRepository.findTop1ByTownId(it.toLong())
                town!!.isAgent = true
                mTownRepository.saveAndFlush(town)
            }
        }
        RedisUtil.set(Constant.RedisKey.REGISTERCODE + "$registerId", "$registerId", Constant.finalParams.REGISTER_CODE_EXPIRETIME)
        return HttpResponse("")
    }

    /**
     * 删除公告栏
     */
    override fun deleteShowBoradContent(token: String): HttpResponse<Any> {
        val uwe = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
        if (mShowWebBoradRepository.findAll().size == 0) {
            return "还未发布公告栏，请发布后再试".getErrorRespons()
        }
        mShowWebBoradRepository.deleteAll()
        RedisUtil.insertLog(LogBean(createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()), logLever = 0, exceptionName = "自定义日志", exceptionContent = "用户${uwe?.realName}删除了公告栏", lineNumber = "0", exceptionState = 0))
        return HttpResponse("")
    }

    /**
     * 发布公告栏
     */
    override fun sendShowBoradContent(mShowWebBoradTab: ShowWebBoradTab, token: String): HttpResponse<Any> {

        val userWeb = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)
        if (userWeb.userType != 0) {
            return "没有操作权限".getErrorRespons()
        }
        if (mShowWebBoradTab.title.isEmpty()) {
            return "标题为空".getErrorRespons()
        }
        if (mShowWebBoradTab.content.isEmpty()) {
            return "内容为空".getErrorRespons()
        }
        mShowWebBoradTab.creatUserId = userWeb.userId
        mShowWebBoradTab.id = 0
        mShowWebBoradRepository.saveAndFlush(mShowWebBoradTab)

        return HttpResponse("")
    }


    /**
     * 获取首页数据
     */
    override fun getHomeData(token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)

        val webUser = mUser_WebRepository.findTop1ByUserId(uid)
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        //昨日新增用户数
        var upDayAddUserNumber = 0


        //昨日总用户数
        var upDayUserNumber = 0


        //昨日订单数
        var upDayOrderNumber = 0

        //昨日订单总额
        var upDayOrderAllPrice = 0.0f

        var userTop10 = listOf<User>()

        var orderTop10 = listOf<WBOrder>()

        if (webUser.userType == 0) {
            //系统管理员
            //查询昨日新增用户数
            userRepository.findAll().forEach {

                if (Helper.getTimeType(it.createTime ?: 0) == 1) upDayAddUserNumber++
                if (Helper.getTimeType(it.createTime ?: 0) == 1 || Helper.getTimeType(it.createTime ?: 0) == -1) {
                    //昨日总用户
                    upDayUserNumber++
                }
            }
            //查询昨日订单数
            mOrderRepository.findAll().forEach {
                if (Helper.getTimeType(it.orderCreateTime ?: 0) == 1) {
                    upDayOrderAllPrice += it.price
                    upDayOrderNumber++
                }
            }
            userTop10 = userRepository.findTop10ByOrderByCreateTime() ?: arrayListOf()
            orderTop10 = mOrderRepository.findTop10ByOrderByOrderCreateTime() ?: arrayListOf()
            orderTop10.forEach {
                it.orderBusinessBean = if (it.orderType == 0) mUserSkillRepository.findTop1ById(it.orderBusinessId?.toLong()
                        ?: 0) else if (it.orderType == 1) mUserRunErrandRepository.findTop1ById(it.orderBusinessId?.toLong()
                        ?: 0) else null
            }
        }

        if (webUser.userType == 1) {
            //一级代理商
        }
        if (webUser.userType == 2) {
            //二级代理商
        }

//        val tabs = mWebTabRepository.findAllByPermission(webUser.userType).filter {
//            it.subTabList = mWebSubTabRepository.findAllByParentTabId(it.id!!)
//            true
//        }


        //查询最新注册的前10个用户
        //查询最新订单前10个
        return HttpResponse(WebHomeDataBean(upDayAddUserNumber = upDayAddUserNumber,
                upDayUserNumber = upDayUserNumber,
                upDayOrderNumber = upDayOrderNumber,
                upDayOrderAllPrice = upDayOrderAllPrice,
                upDayCanUseBalance = 0.0f,//预留
                upDayAllGetedBalance = 0.0f,//预留
                boardShowDec = mShowWebBoradRepository.findTop1ById(0),//公告栏
                userTop10 = userTop10,
                orderTop10 = orderTop10))


        //查询昨日总计收入 预留
        //昨日可用余额


    }


    /**
     * 登录
     */
    override fun login(userWeb: Request_Web_Register, httpServletResponse: HttpServletResponse): HttpResponse<Any> {
        if (userWeb.phoneNumber.isEmpty()) {
            return "电话号码不能为空".getErrorRespons()
        }
        if (userWeb.pwd.isEmpty()) {
            return "密码不能为空".getErrorRespons()
        }
        val u = mUser_WebRepository.findTopByPhoneNumberAndPwd(userWeb.phoneNumber, userWeb.pwd)
                ?: return "账号密码不匹配".getErrorRespons()

        if (u.isBan == true) {
            return "已被管理员禁止登录".getErrorRespons()
        }
        //登录成功后的用户生成该用户的token
        val token = Helper.toMD5("${u.userId}${java.util.Random().nextInt(Int.MAX_VALUE)}")

        //将生成的token写入redis
        RedisUtil.set("${Constant.RedisKey.USER_TOKEN}${u.userId}", token)

        httpServletResponse.addHeader("token", token)
        u.token = token
        return HttpResponse(u)
    }

    /**
     * 注册代理商
     */
    override fun register(userWeb: Request_Web_Register): HttpResponse<Any> {
        if (userWeb.pwd.isEmpty()) {
            //密码不能为空
            return "密码不能为空".getErrorRespons()
        }
        if (userWeb.phoneNumber.isEmpty()) {
            //电话号码不能为空
            return "电话号码不能为空".getErrorRespons()
        }
        if (mUser_WebRepository.findTop1ByPhoneNumber(userWeb.phoneNumber) != null) {
            return "用户已存在".getErrorRespons()
        }
        if (userWeb.realName.isEmpty()) {
            //真实姓名
            return "真实姓名不能为空".getErrorRespons()
        }
        val registerCodeBean = mRegisterCodeRepository.findTop1ByRegisterId(userWeb.registerId)
        if (userWeb.registerId.isEmpty() || registerCodeBean == null) {
            return "注册码不存在或已失效".getErrorRespons()
        }

        //一级代理商注册时 无需传入分成比例 默认与公司三七分
        if (userWeb.proportion.isNullOrEmpty()) {
            return "区域分成比例未设置".getErrorRespons()
        }
        val userId = UUID.randomUUID().toString()
        //查询公司与一级代理商的分成比例
        //一级代理商注册时 与上级的分成比例设置为分成表中配置的比例
        //二级代理商注册时用注册码中指定的分成比例
        val newUser = User_Web(
                superAgentId = registerCodeBean.creatUserId,
                boundAreaListStr = registerCodeBean.boundAreaListSrt,
                proportion = if (registerCodeBean.registerCodeType == 0) (100.0 - mCommissionAllocationRepository.findTop1ById(0)!!.agentCommissionAllocation).toFloat() else (100.0 - registerCodeBean.proportion!!).toFloat(),
                userId = userId,
                phoneNumber = userWeb.phoneNumber,
                pwd = userWeb.pwd,
                userName = userWeb.userName,
                sex = userWeb.sex,
                userLogo = userWeb.userLogo,
                userType = registerCodeBean.registerCodeType + 1,
                realName = userWeb.realName)


        if (registerCodeBean.registerCodeType == 0) {
            //一级代理商注册 区表写入代理人信息 以及与用户的分成比例信息
            //地区编号集合
            val list_dq = arrayListOf<Long>()
            registerCodeBean.boundAreaListSrt.split(",").forEach {
                if (it.isNotEmpty()) {
                    list_dq.add(it.toLong())
                }
            }
            //用户传入的地区编号集合
            val list_id = arrayListOf<Long>()
            //用户传入的分成比例集合
            val pro = arrayListOf<Long>()

            userWeb.proportion!!.split(",").forEach {
                if (it.isNotEmpty()) {
                    val aa = it.split("-")
                    try {
                        list_id.add(aa[0].toLong())
                        pro.add(aa[1].toLong())
                    } catch (e: Exception) {
                        return "分成比例格式有误".getErrorRespons()
                    }
                }
            }

            list_id.forEach {
                if (!list_dq.contains(it)) {
                    return "分成比例格式有误".getErrorRespons()
                }
            }

            userWeb.proportion!!.split(",").forEach {
                if (it.isNotEmpty()) {
                    val aa = it.split("-")
                    val county = mCountyRepository.findTop1ByCountyId(aa[0].toLong())
                    county?.agentId = userId
                    county?.proportionForUser = aa[1].toFloat()
                    mCountyRepository.saveAndFlush(county)
                }
            }
        }
        if (registerCodeBean.registerCodeType == 1) {
            //二级代理商注册 镇区表写入代理人信息 以及与用户的分成比例信息

            //地区编号集合
            val list_dq = arrayListOf<Long>()
            registerCodeBean.boundAreaListSrt.split(",").forEach {
                if (it.isNotEmpty()) {
                    list_dq.add(it.toLong())
                }
            }
            //用户传入的地区编号集合
            val list_id = arrayListOf<Long>()
            //用户传入的分成比例集合
            val pro = arrayListOf<Long>()

            userWeb.proportion!!.split(",").forEach {
                if (it.isNotEmpty()) {
                    val aa = it.split("-")
                    list_id.add(aa[0].toLong())
                    pro.add(aa[1].toLong())
                }
            }

            list_id.forEach {
                if (!list_dq.contains(it)) {
                    return "分成比例格式有误".getErrorRespons()
                }
            }

            userWeb.proportion!!.split(",").forEach {
                if (it.isNotEmpty()) {
                    val aa = it.split("-")
                    try {
                        val town = mTownRepository.findTop1ByTownId(aa[0].toLong())
                        town?.agentId = userId
                        town?.proportionForUser = aa[1].toFloat()
                        mTownRepository.saveAndFlush(town)
                    } catch (e: Exception) {
                        return "分成比例格式有误".getErrorRespons()
                    }

                }
            }
        }
        //注册代理商
        mUser_WebRepository.saveAndFlush(newUser)
        //删除redis中存储的数据 防止过期回调
        RedisUtil.del(Constant.RedisKey.REGISTERCODE + registerCodeBean.registerId)
        //删除注册码
        mRegisterCodeRepository.delete(registerCodeBean)

        return HttpResponse("")
    }

    /**
     * 根据电话号码查询验证码
     */
    override fun getMsgCodeByPhoneNumber(mRequestBean_GetMsgCode: RequestBean_GetMsgCode, token: String): HttpResponse<Any> {
        if (mRequestBean_GetMsgCode.phoneNumber.isEmpty()) {
            return "电话号码为空".getErrorRespons()
        }
        val msgCode = RedisUtil.getMsgCode(mRequestBean_GetMsgCode.phoneNumber)
        if (msgCode != null && msgCode.toString().isNotEmpty()) {
            return HttpResponse(msgCode.toString().toInt())
        } else {
            return "该用户未获取验证码".getErrorRespons()
        }
    }


    /**
     * 抽奖
     */
    override fun getLuckFrawNumber(): HttpResponse<Any> {
        val list = mLuckDrawTabRepository.findAll()
        val aa = list.get(getResult(list)).luckDrawName
        if (!aa.equals("谢谢参与")) {
            logger.info("中奖一次：奖品是$aa")
        }


        return HttpResponse(aa)
    }

    private fun getResult(arr: List<LuckDrawTab>): Int {
        var leng = 0
        for (a in arr) {
            leng += a.luckDrawProbability  //获取总数
        }
        for (i in arr.indices) {
            val random = Random.nextInt(leng)       //获取 0-总数 之间的一个随随机整数
            if (random < arr[i].luckDrawProbability) {
                return i                                  //如果在当前的概率范围内,得到的就是当前概率
            } else {
                leng -= arr[i].luckDrawProbability                                 //否则减去当前的概率范围,进入下一轮循环
            }
        }
        return leng

    }

    /**
     * 标记对应日志为已修复
     */
    override fun fixLogByLogId(mRequestBean_DeleteLog: RequestBean_DeleteLog): HttpResponse<Any> {
        val logList = RedisUtil.getLogs()
        if (mRequestBean_DeleteLog.delType == 0) {
            logList.forEach {
                it.exceptionState = 1
            }
        } else {
            logList.forEach {
                if (it.id.equals(mRequestBean_DeleteLog.logId)) {
                    it.exceptionState = 1
                }
            }
        }
        RedisUtil.set(Constant.RedisKey.LOGTAG, Gson().toJson(logList))
        return HttpResponse("")
    }

    /**
     * web端删除日志接口
     */
    override fun deleteLog(mRequestBean_DeleteLog: RequestBean_DeleteLog): HttpResponse<Any> {
        val logList = RedisUtil.getLogs()
        when (mRequestBean_DeleteLog.delType) {
            0 -> {
                //删除全部日志
                RedisUtil.del(Constant.RedisKey.LOGTAG)
                return HttpResponse("")
            }
            1 -> {
                //删除已修复日志
                val arrayList = arrayListOf<LogBean>()
                logList.forEach {
                    if (it.exceptionState != 1) {
                        arrayList.add(it)
                    }
                }
                RedisUtil.set(Constant.RedisKey.LOGTAG, Gson().toJson(arrayList))
                return HttpResponse("")
            }
            2 -> {
                val arrayList = arrayListOf<LogBean>()
                logList.forEach {
                    if (!it.id.equals(mRequestBean_DeleteLog.logId.trim())) {
                        arrayList.add(it)
                    }
                }
                RedisUtil.set(Constant.RedisKey.LOGTAG, Gson().toJson(arrayList))
                //删除指定日志
                return HttpResponse("")
            }

            else -> {
                return "未知的操作类型".getErrorRespons()
            }
        }
    }

    /**
     * 获取崩溃日志列表
     */
    override fun getLogList(): HttpResponse<Any> {
        return HttpResponse(RedisUtil.getLogs() ?: arrayListOf<LogBean>())
    }


    /**
     * 获取web首页数据
     */
    override fun getWebViewInfo(token: String): HttpResponse<Any> {
        val skills = mUserSkillRepository.findAll()
        val skillsNumber = skills.size
        var skillsucess = 0
        return HttpResponse(ResponseBean_Web_Home(userRepository.findAll().size, skillsNumber, skillsucess, RedisUtil.getLogs()?.size
                ?: 0, if (skills.size > 8) skills.subList(0, 6) else skills))
    }


}
