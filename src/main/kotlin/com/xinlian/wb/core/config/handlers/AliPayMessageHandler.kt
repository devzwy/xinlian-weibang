package com.xinlian.wb.core.config.handlers

import com.egzosn.pay.ali.api.AliPayService
import com.egzosn.pay.ali.bean.AliPayMessage
import com.egzosn.pay.common.api.PayMessageHandler
import com.egzosn.pay.common.bean.PayOutMessage
import com.egzosn.pay.common.exception.PayErrorException
import com.xinlian.wb.core.entity.PushData
import com.xinlian.wb.core.service.service.IBusunessService
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.repositorys_web.WebBalanceNoteRepository
import com.xinlian.wb.jdbc.tabs.BalanceNotes
import com.xinlian.wb.jdbc.tabs_web.WebBalanceNoteTab
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.ktx.pushMessageAll
import com.xinlian.wb.util.ktx.toJsonStr
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

/**
 * 支付宝支付回调处理器
 *
 */
@Component
open class AliPayMessageHandler : PayMessageHandler<AliPayMessage, AliPayService> {

    private val logger = LoggerFactory.getLogger(AliPayMessageHandler::class.java)

    @Autowired
    private val busunessService: IBusunessService? = null

    /**
     * 注入的 Banner表操作对象
     */
    @Autowired
    lateinit var mBannerRepository: BannerRepository

    @Autowired
    lateinit var mOrderRepository: OrderRepository

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

    /**
     * 余额表
     */
    @Autowired
    lateinit var mBalanceRepository: BalanceRepository

    /**
     * 注入的 第一个子标签表操作对象
     */
    @Autowired
    lateinit var mSubSkillTabRepository: SubSkillTabRepository


    /**
     * 代理商和管理员流水
     */
    @Autowired
    lateinit var mWebBalanceNoteRepository: WebBalanceNoteRepository

    /**
     * 代理商和管理员表
     */
    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    /**
     * 跑腿代办表
     */
    @Autowired
    lateinit var mUserRunErrandRepository: UserRunErrandRepository

    @Autowired
    lateinit var mDemandRepository: DemandRepository

    /**
     * 注入的 User表操作对象
     */
    @Autowired
    lateinit var mUserRepository: UserRepository

    /**
     * 处理支付回调消息的处理器接口
     *
     * @param payMessage 支付消息
     * @param context    上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param payService 支付服务
     * @return xml, text格式的消息，如果在异步规则里处理的话，可以返回null
     * @throws PayErrorException 支付错误异常
     */
    @Throws(PayErrorException::class)
    override fun handle(payMessage: AliPayMessage, context: Map<String, Any>, payService: AliPayService): PayOutMessage {
        //com.egzosn.pay.demo.entity.PayType.getPayService()#48

        logger.info("busunessService ------ $busunessService")
        logger.info("mBannerRepository ------ $mBannerRepository")

        val payId = payService.payConfigStorage.attach

        val message = payMessage.payMessage
        //交易状态
        val trade_status = message["trade_status"] as String

        //上下文对象中获取账单
        //        AmtApply amtApply = (AmtApply)context.get("amtApply");
        //日志存储
        //        amtPaylogService.createAmtPaylogByCallBack(amtApply,  message.toString());
        val body = message["out_trade_no"] as String
        if (body.isNullOrEmpty()) return payService.getPayOutMessage("fail", "失败")
        val orderBean = mOrderRepository.findTop1ByOrderId(body)
        if (orderBean != null) {
            val finalPrice = orderBean.price + orderBean.addPrice
            //交易完成
            if ("TRADE_SUCCESS" == trade_status || "TRADE_FINISHED" == trade_status) {


                val payAmount = BigDecimal(message["total_amount"] as String)

                var subTitle = ""
                if (orderBean.orderType == 0) {
                    subTitle = mUserSkillRepository.findTop1ById(orderBean.orderBusinessId?.toLong()
                            ?: 0)?.title ?: "技能服务"
                    if (orderBean?.buyFromDemand == true) {
                        //需求发布方的购买技能 标注需求为服务中状态
                        val demand = mDemandRepository.findTop1ById(orderBean.demandId!!)
                        demand?.demandState = 1
                        mDemandRepository.saveAndFlush(demand)
                    }
                }
                if (orderBean.orderType == 1 || orderBean.orderType == 2) {
                    val userRunErrand = mUserRunErrandRepository.findTop1ById(orderBean.orderBusinessId!!.toLong())
                    subTitle = userRunErrand?.tradeNames
                            ?: if (orderBean.orderType == 1) "跑腿代办" else if (orderBean.orderType == 2) "同城配送" else "未知的交易类型"
                }
                //对应的技能实体
//            val userSkill = mUserSkillRepository.findTop1ById(orderBean?.orderBusinessId?.toLong() ?: 0)
//            //对应的标签实体
//            val subSkillTab = mSubSkillTabRepository.findOneBySubTagId(userSkill?.s_tag ?: 0)
//            if (subSkillTab == null) return payService.getPayOutMessage("fail", "失败")
                if (orderBean.state!! >= 2 || orderBean.state == -1) {
                    logger.info("重复的订单回调，已拦截")
                    return payService.getPayOutMessage("fail", "失败")
                }
                //记录当前用户流水
                mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = orderBean.userIdFromBuy!!, bType = 1,
                        orderId = body,
                        orderDec = subTitle,
                        price = finalPrice, payType = 0))

                //记录公司入账流水
                mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(
                        userId = "admin",
                        bType = 1,
                        price = finalPrice,
                        toOrFromUserId = orderBean.userIdFromServer,
                        orderId = body, noteDec = if (orderBean.orderType == 0) "购买技能付款" else if (orderBean.orderType == 1) "跑腿代办付款" else if (orderBean.orderType == 2) "同城配送付款" else "未知类型付款",
                        bFType = 0))

                val mUser_Web = mUser_WebRepository.findTop1ByUserId("admin")!!
                mUser_Web.balance = mUser_Web.balance!! + finalPrice
                mUser_WebRepository.saveAndFlush(mUser_Web)
//            //记录卖方用户流水
//            mBalanceNotesRepository.saveAndFlush(BalanceNotes(userId = userSkill?.user?.userId ?: "", bType = 0,
//                    orderId = orderId,
//                    orderDec = subSkillTab.subTitle,
//                    price = orderBean.price, payType = 0))
                //更新订单状态
                orderBean.state = 2
                orderBean.orderPayTime = Date().time
                orderBean.payment = 1
                mOrderRepository.saveAndFlush(orderBean)
                //删除redis中存储的数据 防止过期回调
                RedisUtil.del(Constant.RedisKey.ORDER_REDIS_KEY + orderBean.orderId)
                if (orderBean.orderType == 1) {
                    val user = mUserRepository.findTop1ByUserId(orderBean.userIdFromBuy!!)
                    PushData(pType = 0, title = "您有新的订单", content = "${user!!.userName}发布了一个跑腿代办订单，快来看看吧", bussContent = "${mUserRunErrandRepository.findTop1ByUserId(user.userId)?.id}").toJsonStr().pushMessageAll()
                }

                if (orderBean.orderType == 2) {
                    val user = mUserRepository.findTop1ByUserId(orderBean.userIdFromBuy!!)
                    PushData(pType = 1, title = "您有新的订单", content = "${user!!.userName}发布了一个同城配送订单，快来看看吧", bussContent = "${mUserRunErrandRepository.findTop1ByUserId(user.userId)?.id}").toJsonStr().pushMessageAll()
                }

                return payService.getPayOutMessage("success", "成功")

            } else if ("WAIT_BUYER_PAY".equals(trade_status) || "TRADE_CLOSED".equals(trade_status)) {
                orderBean.state = -2
                mOrderRepository.saveAndFlush(orderBean)
            }
            return payService.getPayOutMessage("Succ", "成功")

        }

        val backP = message["passback_params"] as String
        if (backP != null) {
            //用户充值回掉
            try {
                val b = backP.split("_")
                val userId = b[0] //充值的用户ID
                val price = b[1] //充值的金额

                //给用户充值金额

                val u_b = mBalanceRepository.findTop1ByUserId(userId)
                u_b?.balance = u_b?.balance ?: 0 + price.toDouble()
                mBalanceRepository.saveAndFlush(u_b)

                //记录用户流水

                mBalanceNotesRepository.saveAndFlush(
                        BalanceNotes(
                                userId = userId,
                                bType = 2,
                                orderId = null,
                                price = price.toFloat(), payType = 0))

                logger.info("用户：${userId}充值${price}元成功")
                return payService.getPayOutMessage("Succ", "成功")
            } catch (e: Exception) {
                logger.debug("未知的操作类型回掉")
            }
        }
        return payService.getPayOutMessage("fail", "失败")
    }
}
