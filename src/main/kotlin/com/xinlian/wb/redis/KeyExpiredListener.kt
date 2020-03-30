package com.xinlian.wb.redis

import com.xinlian.wb.jdbc.repositorys.DemandRepository
import com.xinlian.wb.jdbc.repositorys.OrderCommRepository
import com.xinlian.wb.jdbc.repositorys.OrderRepository
import com.xinlian.wb.jdbc.repositorys_web.CountyRepository
import com.xinlian.wb.jdbc.repositorys_web.RegisterCodeRepository
import com.xinlian.wb.jdbc.repositorys_web.TownRepository
import com.xinlian.wb.jdbc.tabs.OrderCommTab
import com.xinlian.wb.util.Constant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component
import java.util.*

@Component
open class KeyExpiredListener(listenerContainer: RedisMessageListenerContainer) : KeyExpirationEventMessageListener(listenerContainer) {

    internal var logger = LoggerFactory.getLogger(RedisConfiguration::class.java)

    /**
     * 注入的 订单表操作对象
     */
    @Autowired
    lateinit var mOrderRepository: OrderRepository

    /**
     * 注入的 订单评价表操作对象
     */
    @Autowired
    lateinit var mOrderCommRepository: OrderCommRepository


    @Autowired
    private val mCountyRepository: CountyRepository? = null


    @Autowired
    private val mTownRepository: TownRepository? = null


    @Autowired
    lateinit var mDemandRepository: DemandRepository


    /**
     * 注册码
     */
    @Autowired
    lateinit var mRegisterCodeRepository: RegisterCodeRepository

    //这里是回调函数失效的时候回调用这个函数
    override fun onMessage(message: Message, pattern: ByteArray?) {
        super.onMessage(message, pattern)
        val key = String(message.body, charset("utf-8"))
        logger.info("过期的RedisKey:$key")
        if (key.contains(Constant.RedisKey.REGISTERCODE)) {
            //注册码过期回调
            logger.info("注册码过期回调:$key")
            val registerCodeId = key.substring(Constant.RedisKey.REGISTERCODE.length, key.length)
            val registerCode = mRegisterCodeRepository.findTop1ByRegisterId(registerCodeId)!!
            if (registerCode.registerCodeType == 0) { //一级代理商
                val a: Array<String> = registerCode.boundAreaListSrt.split(",").toTypedArray()
                for (i in a.indices) { //根据绑定的区 打开标记
                    if (!a[i].isEmpty()) {
                        val bb = mCountyRepository!!.findTop1ByCountyId(java.lang.Long.valueOf(a[i]))
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
                        val cc = mTownRepository!!.findTop1ByTownId(java.lang.Long.valueOf(a[i]))
                        cc!!.isAgent = false
                        mTownRepository.saveAndFlush(cc)
                        logger.info("打开" + cc + "区域的已代理标记")
                    }
                }
            }

            mRegisterCodeRepository.delete(mRegisterCodeRepository.findTop1ByRegisterId(registerCodeId))
            logger.info("过期的注册码已删除")
        }
        if (key.contains(Constant.RedisKey.ORDER_REDIS_KEY)) {
            //订单过期回调
            logger.info("订单过期回调:$key")
            val orderId = key.substring(Constant.RedisKey.ORDER_REDIS_KEY.length, key.length)
            val wbOrder = mOrderRepository.findTop1ByOrderId(orderId)
            if (wbOrder!!.state == 1) {
                logger.info("检测到订单：${wbOrder.orderId}过期未支付，订单状态修改 ${wbOrder.state} ->> -1")
                wbOrder.state = -1
                mOrderRepository.save(wbOrder)
                logger.info("订单状态(${wbOrder.orderId})修改成功")
            }
        }
        if (key.contains(Constant.RedisKey.ORDER_REDIS_KEY_COMM)) {
            //订单超时未评价回调
            logger.info("订单评价超时过期回调:$key")
            val orderId = key.substring(Constant.RedisKey.ORDER_REDIS_KEY_COMM.length, key.length)
            val wbOrder = mOrderRepository.findTop1ByOrderId(orderId)
            if (wbOrder!!.state == 3) {
                val commBean = mOrderCommRepository.findTop1ByOrderId(wbOrder.orderId)
                        ?: OrderCommTab(orderId = wbOrder.orderId)

                if (commBean.commTimeFormBuy == null) {
                    //买方未评
                    commBean.commTimeFormBuy = Date().time
                    commBean.commStarFromBuy = 5
                    commBean.buyCommContent = "订单超时未评价，系统自动好评"
                    mOrderCommRepository.saveAndFlush(commBean)
                    logger.info("买家未评，自动好评")
                }
                if (commBean.commTimeFormServer == null) {
                    //卖方未评
                    commBean.commTimeFormServer = Date().time
                    commBean.commStarFromServer = 5
                    commBean.serverCommContent = "订单超时未评价，系统自动好评"
                    mOrderCommRepository.saveAndFlush(commBean)
                    logger.info("卖家未评，自动好评")
                }
                wbOrder.commId = mOrderCommRepository.findTop1ByOrderId(wbOrder.orderId)!!.id
                wbOrder.state = 6
                mOrderRepository.save(wbOrder)
                logger.info("订单自动好评成功")
            }
        }
        if (key.contains(Constant.RedisKey.MSG_CODE)) {
            //验证码过期回调
            logger.info("验证码过期回调:$key")
//            val registerCodeId = key.substring(Constant.RedisKey.MSG_CODE.length, key.length)
        }

        //需求过期回调
        if (key.contains(Constant.RedisKey.DEMAND_KEY)) {
            logger.info("需求过期回调:$key")
            val demandId = key.substring(Constant.RedisKey.DEMAND_KEY.length, key.length)
            val demand = mDemandRepository.findTop1ById(demandId.toLong())
            demand?.demandState = -2 //标注为已过期状态
            logger.info("需求:${demandId}已过期，标注为过期状态(-2)")
//            mDemandRepository.deleteById(demandId.toLong())
//            val registerCodeId = key.substring(Constant.RedisKey.MSG_CODE.length, key.length)
        }

    }
}