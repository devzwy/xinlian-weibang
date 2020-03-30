package com.xinlian.wb.core.config.interceptor

import com.egzosn.pay.ali.api.AliPayService
import com.egzosn.pay.ali.bean.AliPayMessage
import com.egzosn.pay.common.api.PayMessageHandler
import com.egzosn.pay.common.api.PayMessageInterceptor
import com.egzosn.pay.common.exception.PayErrorException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 支付宝回调信息拦截器
 */
@Component
class AliPayMessageInterceptor : PayMessageInterceptor<AliPayMessage, AliPayService> {
    private val logger = LoggerFactory.getLogger(AliPayMessageInterceptor::class.java)

    /**
     * 拦截支付消息
     *
     * @param payMessage     支付回调消息
     * @param context        上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param payService 支付服务
     * @return true代表OK，false代表不OK并直接中断对应的支付处理器
     * @see PayMessageHandler 支付处理器
     *
     * @throws PayErrorException PayErrorException*
     */
    @Throws(PayErrorException::class)
    override fun intercept(payMessage: AliPayMessage, context: Map<String, Any>, payService: AliPayService): Boolean {

        //这里进行拦截器处理，自行实现
        val outTradeNo = payMessage.outTradeNo

        logger.info("支付宝回传数据:outTradeNo = $outTradeNo")
        logger.info("支付宝回传数据:payMessage = $payMessage")


        // 设置外部单号
//                amtApplyService.fillApplyoutId(outTradeNo, (String) payMessage.getPayMessage().get("trade_no"));


        //获取账单
        //        AmtApply amtApply = amtApplyService.getAmtApplyByApplyId(outTradeNo);
        //        if (null == amtApply){
        //            Log4jUtil.info("app 阿里pay：" + outTradeNo);
        //            return false;
        //        }
        //
        //        重复回调不进行处理
        //        if(amtApply.getApplyState().shortValue()== ApplyStateEnum.success.getCode()){
        //            return false;
        //        }
        //将账单存储至上下文对象中
        //        context.put("amtApply", amtApply);


        return true
    }
}
