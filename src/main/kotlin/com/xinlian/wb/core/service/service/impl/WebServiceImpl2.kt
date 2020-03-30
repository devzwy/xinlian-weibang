package com.xinlian.wb.core.service.service.impl

import com.egzosn.pay.common.bean.DefaultCountryCode
import com.egzosn.pay.common.bean.DefaultCurType
import com.egzosn.pay.common.bean.TransferOrder
import com.egzosn.pay.spring.boot.core.MerchantPayServiceManager
import com.google.gson.Gson
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IWebService2
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.tabs.SystemNotification
import com.xinlian.wb.jdbc.tabs.WithDrawalRepository
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.getErrorRespons
import com.xinlian.wb.util.ktx.sendNotification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WebServiceImpl2 : IWebService2 {

    private val logger = LoggerFactory.getLogger(WebServiceImpl2::class.java)

    @Autowired
    lateinit var mDemandRepository: DemandRepository

    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    @Autowired
    lateinit var mUserRepository: UserRepository

    @Autowired
    lateinit var mParentTabRepository: SkillParentTabRepository

    @Autowired
    lateinit var mSubSkillTabRepository: SubSkillTabRepository

    @Autowired
    lateinit var mUserSkillRepository: UserSkillRepository

    @Autowired
    lateinit var mSystemNotificationRepository: SystemNotificationRepository


    @Autowired
    lateinit var mWithDrawalRepository: WithDrawalRepository

    @Autowired
    lateinit var payManager: MerchantPayServiceManager

    @Autowired
    lateinit var mBalanceRepository: BalanceRepository

    /**
     * 提现审核
     */
    override fun withdrawalAudit(mWithdrawalAuditBean: WithdrawalAuditBean, token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)

        if (!uid.equals("admin")) return "权限不足".getErrorRespons()

        val withDrawalDB = mWithDrawalRepository.findTop1ById(mWithdrawalAuditBean.withdrawalId ?: -1)
                ?: return "提现记录不存在".getErrorRespons()

        if (mWithdrawalAuditBean.bType == -1) return "操作类型错误".getErrorRespons()

        if (mWithdrawalAuditBean.bType == 0) {

            if (withDrawalDB.state == 1) return "请勿重复操作".getErrorRespons()
            if (withDrawalDB.state == 2) return "该记录已被拒绝，请重新发起提现申请".getErrorRespons()

            //同意
            val mTransferOrder = TransferOrder()
            mTransferOrder.amount = withDrawalDB.price.toBigDecimal()
            mTransferOrder.payeeAccount = withDrawalDB.aliaAccount
            mTransferOrder.payerName = "上海新连网络科技有限公司" //付款人名称
            mTransferOrder.payeeName = withDrawalDB.alipayUserName //首款人名称
            mTransferOrder.remark = "用户提现" //备注
            mTransferOrder.curType = DefaultCurType.CNY //币种
            mTransferOrder.countryCode = DefaultCountryCode.CHN //付款人名称


            val map = payManager.transfer("1", mTransferOrder)
            val alipayTrBean = Gson().fromJson(map.get("alipay_fund_trans_toaccount_transfer_response").toString(), AlipayTrBean::class.java)
            if (alipayTrBean.code.equals("10000")) {
                //转账成功
                withDrawalDB.state = 1
                mWithDrawalRepository.saveAndFlush(withDrawalDB)
                return "转账成功，耐心等待到账".getErrorRespons()
            } else {
                return (alipayTrBean.sub_msg ?: "提现发生未知错误").getErrorRespons()
            }

        } else if (mWithdrawalAuditBean.bType == 1) {
            //拒绝
            if (mWithdrawalAuditBean.errorMsg == null) return "拒绝提现申请时拒绝的原因不能为空".getErrorRespons()
            if (withDrawalDB.state == 2) return "请勿重复操作".getErrorRespons()
            //返还余额
            val user = mUserRepository.findTop1ByUserId(withDrawalDB.userId)
            val user_web = mUser_WebRepository.findTop1ByUserId(withDrawalDB.userId)

            if (user != null) {
                //返还用户余额
                val b = mBalanceRepository.findTop1ByUserId(withDrawalDB.userId)
                b?.balance = (b?.balance ?: 0.0) + withDrawalDB.price
                mBalanceRepository.saveAndFlush(b)
            }
            if (user_web != null) {
                //返还代理商余额
                user_web.balance = (user_web.balance ?: 0.0f) + (withDrawalDB.price.toFloat())
                mUser_WebRepository.saveAndFlush(user_web)
            }
            withDrawalDB.state = 2
            withDrawalDB.errMsg = mWithdrawalAuditBean.errorMsg
            mWithDrawalRepository.saveAndFlush(withDrawalDB)
            return "提现申请已拒绝".getErrorRespons()
        } else {
            return "传入的操作类型错误".getErrorRespons()
        }
    }

    /**
     * 禁用/启用标签
     */
    override fun controllerSubTag(mSubTagControllerBean: SubTagControllerBean, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有权限操作".getErrorRespons()
        if (!user.userId.equals("admin")) return "没有操作权限".getErrorRespons()
        val subTag = mSubSkillTabRepository.findOneBySubTagId(mSubTagControllerBean.subTagId)
        if (mSubTagControllerBean.subTagId == -1L || subTag == null) return "标签id传入有误".getErrorRespons()
        when (mSubTagControllerBean.doType) {
            0 -> {
                //开启标签展示
                if (subTag.tagIsShow) return "该标签已在展示中，请勿重新操作".getErrorRespons()
                subTag.tagIsShow = true
                mSubSkillTabRepository.saveAndFlush(subTag)
                return HttpResponse("操作成功")
            }
            1 -> {
                //关闭
                if (!subTag.tagIsShow) return "该标签已关闭展示，请勿重新操作".getErrorRespons()
                subTag.tagIsShow = false
                mSubSkillTabRepository.saveAndFlush(subTag)
                return HttpResponse("操作成功")
            }
            else -> {
                return "操作类型传入有误".getErrorRespons()
            }
        }

    }

    /**
     * 关闭/显示首页展示标签
     */
    override fun openOrCloseHomeSubTag(mSubTagControllerBean: SubTagControllerBean, token: String): HttpResponse<Any> {
        val user = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有权限操作".getErrorRespons()
        if (!user.userId.equals("admin")) return "没有操作权限".getErrorRespons()
        val subTag = mSubSkillTabRepository.findOneBySubTagId(mSubTagControllerBean.subTagId)
        if (mSubTagControllerBean.subTagId == -1L || subTag == null) return "标签id传入有误".getErrorRespons()
        when (mSubTagControllerBean.doType) {
            0 -> {
                //开启标签展示
                if (subTag.hotTag == 1) return "该标签已在首页展示，请勿重新操作".getErrorRespons()
                subTag.hotTag = 1
                mSubSkillTabRepository.saveAndFlush(subTag)
                return HttpResponse("操作成功")
            }
            1 -> {
                //关闭
                if (subTag.hotTag == 0) return "该标签未在首页展示，请勿重新操作".getErrorRespons()
                subTag.hotTag = 0
                mSubSkillTabRepository.saveAndFlush(subTag)
                return HttpResponse("操作成功")
            }
            else -> {
                return "操作类型传入有误".getErrorRespons()
            }
        }
    }

    /**
     * 发送系统通知
     */
    override fun sendSystemNotification(mSystemNotification: SystemNotification, token: String): HttpResponse<Any> {
        if (!Helper.getUserIdByToken(token).trim().equals("admin")) return "没有操作权限".getErrorRespons()
        mSystemNotification.apply {
            if (notificationTitle == null) return "通知的标题为空".getErrorRespons()
            if (notificationContent == null) return "通知的内容为空".getErrorRespons()
            if (userId == null || mUserRepository.findTop1ByUserId(userId) == null) return "通知对象为空或不存在".getErrorRespons()

        }
        mSystemNotification?.sendNotification(mSystemNotificationRepository)
        return HttpResponse("发送成功")
    }

    /**
     * 封禁/解封技能
     */
    override fun doBanSkill(mRequestBean_BanSkill: RequestBean_BanSkill, token: String): HttpResponse<Any> {
        val userWebDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有操作权限".getErrorRespons()
        if (mRequestBean_BanSkill.doType != 0 && mRequestBean_BanSkill.doType != 1) return "操作类型doType传入有误".getErrorRespons()
        if (!userWebDB.userId.equals("admin")) return "没有操作权限".getErrorRespons()

        val skillDB = mUserSkillRepository.findTop1ById(mRequestBean_BanSkill.skillId)
                ?: return "技能不存在".getErrorRespons()
        when (mRequestBean_BanSkill.doType) {
            0 -> {
                //封禁
                if (skillDB.isBan) return "该技能已封禁，请勿重复操作".getErrorRespons()
                skillDB.isBan = true
                mUserSkillRepository.saveAndFlush(skillDB)
                return HttpResponse("封禁成功")
            }
            else -> {
                if (!skillDB.isBan) return "该技能已解封，请勿重复操作".getErrorRespons()
                skillDB.isBan = false
                mUserSkillRepository.saveAndFlush(skillDB)
                return HttpResponse("解封成功")
            }
        }
    }

    /**
     * 获取全部技能列表
     */
    override fun getAllSkill(mReqBeanGetAllSkill: ReqBeanGetAllSkill, token: String): HttpResponse<Any> {
        val userWebDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有操作权限".getErrorRespons()
        if (!userWebDB.userId.equals("admin")) return "没有操作权限".getErrorRespons()
        return HttpResponse(mUserSkillRepository.findAll(mReqBeanGetAllSkill.page - 1, mReqBeanGetAllSkill.number).filter {
            it.pTagBean = mParentTabRepository.findTop1ByParentId(it.p_tag ?: 0).also {
                it?.subSkills = null
            }
            it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
            true
        })
    }


    /**
     * 封禁/解封需求
     */
    override fun doBanDemand(mRequestBean_BanDemand: RequestBean_BanDemand, token: String): HttpResponse<Any> {
        val userWebDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有操作权限".getErrorRespons()
        if (mRequestBean_BanDemand.doType != 0 && mRequestBean_BanDemand.doType != 1) return "操作类型doType传入有误".getErrorRespons()
        if (!userWebDB.userId.equals("admin")) return "没有操作权限".getErrorRespons()
        val demandDB = mDemandRepository.findTop1ById(mRequestBean_BanDemand.demandId)
                ?: return "需求不存在".getErrorRespons()
        when (mRequestBean_BanDemand.doType) {
            0 -> {
                //封禁
                if (demandDB.isBan) return "该需求已封禁，请勿重复操作".getErrorRespons()
                demandDB.isBan = true
                mDemandRepository.saveAndFlush(demandDB)
                return HttpResponse("封禁成功")
            }
            else -> {
                if (!demandDB.isBan) return "该需求已解封，请勿重复操作".getErrorRespons()
                demandDB.isBan = false
                mDemandRepository.saveAndFlush(demandDB)
                return HttpResponse("解封成功")
            }
        }
    }

    /**
     * 获取全部需求列表
     */
    override fun getAllDemand(mReqBeanGetWithdrawal: ReqBeanGetWithdrawal2, token: String): HttpResponse<Any> {
        val userWebDB = mUser_WebRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "没有操作权限".getErrorRespons()
        if (!userWebDB.userId.equals("admin")) return "没有操作权限".getErrorRespons()
        if (mReqBeanGetWithdrawal.state == 3) {
            //获取全部需求
            return HttpResponse(mDemandRepository.findAll(mReqBeanGetWithdrawal.page - 1, mReqBeanGetWithdrawal.number).filter {
                it.user = mUserRepository.findTop1ByUserId(it.userId!!)?.also {
                    it.indentity = null
                    it.address = null
                }
                it.pTagBean = mParentTabRepository.findTop1ByParentId(it.p_tag ?: 0).also {
                    it?.subSkills = null
                }
                it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                true
            })
        } else {
            return HttpResponse(mDemandRepository.findAll(mReqBeanGetWithdrawal.state, mReqBeanGetWithdrawal.page - 1, mReqBeanGetWithdrawal.number).filter {
                it.user = mUserRepository.findTop1ByUserId(it.userId!!)?.also {
                    it.indentity = null
                    it.address = null
                }
                it.pTagBean = mParentTabRepository.findTop1ByParentId(it.p_tag ?: 0).also {
                    it?.subSkills = null
                }
                it.sTagBean = mSubSkillTabRepository.findOneBySubTagId(it.s_tag ?: 0)
                true
            })
        }
    }

}