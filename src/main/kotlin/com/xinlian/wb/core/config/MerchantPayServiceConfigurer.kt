package com.xinlian.wb.core.config

import com.egzosn.pay.spring.boot.core.PayServiceConfigurer
import com.egzosn.pay.spring.boot.core.configurers.MerchantDetailsServiceConfigurer
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.AliPaymentPlatform
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.PaymentPlatforms
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxPaymentPlatform
import com.xinlian.wb.core.config.handlers.AliPayMessageHandler
import com.xinlian.wb.core.config.handlers.WxPayMessageHandler
import com.xinlian.wb.core.config.interceptor.AliPayMessageInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate


/**
 * 支付服务配置
 *
 * @author egan
 * email egzosn@gmail.com
 * date 2019/5/26.19:25
 */
@Configuration
open class MerchantPayServiceConfigurer : PayServiceConfigurer {


    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @Autowired
    private val spring: AutowireCapableBeanFactory? = null

    @Autowired
    private val aliPayMessageHandler: AliPayMessageHandler? = null

    @Autowired
    private val mWxPayMessageHandler: WxPayMessageHandler? = null


    private val logger = LoggerFactory.getLogger(MerchantPayServiceConfigurer::class.java)

    /**
     * 商户配置
     *
     * @param merchants 商户配置
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    override fun configure(merchants: MerchantDetailsServiceConfigurer) {
        //        数据库文件存放 /doc/sql目录下
//        merchants.jdbc(jdbcTemplate)
        //内存Builder方式
        merchants.inMemory()
                .ali()
                .detailsId("1")
                .appid("2018090761255730")
                .keyPrivate("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDXvEWYRVJ2WBYaRPNZRQJdtue4NP7+DKpD31qQr7P5gV5cGo559EujNBo42rGCkAclVea2lirU5UPEblKKtvLkYZcWvc197RBUZ3lpaFfh50XnGL9w2VjIgYp3ER1XzizCdOwwdCL4xBZYnjiy+dqGFKLEKXzZ6WGaG0075c286Ynv9zS9B4Jy6KrmR1rZyg0RWd8rd/LInB/w5J2Gx4m3lr4FuLy/RiOIj/YcxEtHja+VFDGi97Wbpj5i33XMj4q6OHujwM9o8qoSh9+zE8ipILy6WlvVo0RrvycBNNYBh/Wc//pntJWwUjEEbpF4lSijdJfsrA1G/iVYHI2XHU9fAgMBAAECggEAIZ2wqn2Cz/YB264KzUo3iCrMCKQZZ7TJgzH2hr0aKjLCZU+uC8rJTnD79Qf+yP1wX5z3ClRz8CCZYq5eJkdDqdqbFGwW7RxXgw9sl8QujYDgH5Z3njn/355v2QXNsLTrvJFDjHlMSb8kTEK7SwgryODGTBK65am8D6tZaY/xcxIXcKraxg4F9SKLC2pucohijVDLJiCYRHT3lQXwYpWUSgLI7RECwjbZ7b+M4kn3YQHTTM3B2XHi5eOC9QD2E8stEVpjrWOum2x4FuOJwcntRXEX7MlD5hByMdQtKs/apfg2NPO0w/HM5lB2FVt+C5mNIxaK1k+IY4YwgCRoPRN7YQKBgQD/prKr+D0OSDUsGhEiHwAeJTr/oOCvR/mM2HxAbbyDpm9kK6KruD5KhKFm1DCUfkIzyE+hWtZMIxEuUwTXAnW8BoZSO74F6ph8FF2AItCFu2ldqPZAo74VlRXACrIavmkWCDriFilERswVwoFmQ9AZT6N9xgdUcJnVqBTWDJs5nQKBgQDYB6GAn6teVABmrstiBLhbrV2Ibh6S5K2A5VR5XK3MBL4wpJdsso8222JPdogKVycTjJDbGzbcsROWapqrGrCZJGrw9RzbxBDkNQZb0fNEJc+A0C7X7X/mSIOGEsMr0wrqFKv7v8gQIX4swonpjJ68lc4lXDsQzTPHJ+9w6SyKKwKBgQDXBQzH6RzpM0BNnIsGiM5fjG7H7DauFjpFBRvUULe6frS3qp/CEE+L+2uJAjTt4BesV/DpxG3zUZc0wCyU7oO5pj85UPgtH7gqvDI8AIwjvpILbX3Yth+9JUxK3nQYXEgkeL2VxI/m1ij2sEaHjcesUjgvI+ysT5yDgEAuHCI+XQKBgQC1cDtJr1Eb6YtospgpG/PHM+Vhx7MwBpZesr3vLFLgDn+6lRKE/bdhCIMaYGBD5wzv01h6XfIHC0Z7R8/ds43tIXQcKpHxVkwljw9nIP+jFeqgsrkVK+kG/ZJRkgKQFTGOhown5dxfB/JUH5P7LtrHYz5/jGYjZ2V8eGMXPw/kfwKBgHILIz3RoHD+e6rU1kBJH+INEK633qJzTTbY4+GWCccZ98S7Y+pA+D/OoA/n9hFfyXoWt41K8J6Y9iDDlWFHFLFKdKfXtxN+gLkBIph2qXt+oh413IE9WbB/36t6fqvhMPyU/yz7XtMY2w8M9Alz9tb4vGWrXsUDtCFQYlx9w1Q0")
                .keyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnM6CHfR92sh++lJsdgT5v+xmRXZyeLIZxagi/bDGXiHqMsr/Bcs8ORDn89QwCLHxAIhbqwjAztF+P2Ix01fAFU72Glst4dKmJ9EpRLcXQlpMEItuR4IX0yvrKzr1cSB7VeFEms3qtQDBromDQ4YokqqQwrgVJ5EYrs+Wq+IDhWwlVRBlL1tkpKzZ42VpLWdX5nymfLCdoE4sBNToLSYw1k4bgfW1Ihk/Z5wDZMY2UlmcpJoA5HGtbkoT5odr7h0w59sq9uqSBO4/vfbbVlsblpx1S69d3FSG1SZzwyFNL6K+qA2Ao5dmOEXGhOfxUJ0P0U0DXSoNpoZpTJDsBpF5gQIDAQAB")
                .inputCharset("utf-8")
                .notifyUrl("https://www.api.huyuwl.cn/wb/business/payBack1.json")
                .returnUrl("https://www.api.huyuwl.cn/wb/business/payBack1.json")
//                .pid("2018090761255730")
//                .seller("2018090761255730")
                .signType("RSA2")
//                .test(true)
                .and()
                .wx()
                .detailsId("2")
                .appid("wxd33a062ce8eca8b0")
                .mchId("1556940831")
                .secretKey("wNDpshUKvduzJScsCRgcd6RFjXuUgvl5")
                .notifyUrl("https://www.api.huyuwl.cn/wb/business/payBack2.json")
                .returnUrl("https://www.api.huyuwl.cn/wb/business/payBack2.json")
                .inputCharset("utf-8")
                .signType("MD5")
//                .test(true)
                .and()
//        val aa  = merchants.builder.build() as MerchantDetailsService<MerchantDetails>
//       val bb =  aa.loadMerchantByMerchantId("1")?.getPayService() as PayService<*>
//        bb.setPayMessageHandler(aliPayMessageHandler)

        logger.info("支付参数初始化完成")
/*
        //------------内存手动方式------------------
        UnionMerchantDetails unionMerchantDetails = new UnionMerchantDetails();
        unionMerchantDetails.detailsId("3");
        //内存方式的时候这个必须设置
        unionMerchantDetails.setCertSign(true);
        unionMerchantDetails.setMerId("700000000000001");
        //公钥，验签证书链格式： 中级证书路径;
        unionMerchantDetails.setAcpMiddleCert("D:/certs/acp_test_middle.cer");
        //公钥，根证书路径
        unionMerchantDetails.setAcpRootCert("D:/certs/acp_test_root.cer");
        //私钥, 私钥证书格式： 私钥证书路径
        unionMerchantDetails.setKeyPrivateCert("D:/certs/acp_test_sign.pfx");
        //私钥证书对应的密码
        unionMerchantDetails.setKeyPrivateCertPwd("000000");
        //证书的存储方式
        unionMerchantDetails.setCertStoreType(CertStoreType.PATH);
        unionMerchantDetails.setNotifyUrl("http://127.0.0.1/payBack4.json");
        // 无需同步回调可不填  app填这个就可以
        unionMerchantDetails.setReturnUrl("http://127.0.0.1/payBack4.json");
        unionMerchantDetails.setInputCharset("UTF-8");
        unionMerchantDetails.setSignType("RSA2");
        unionMerchantDetails.setTest(true);
        //手动加入商户容器中
        merchants.inMemory().addMerchantDetails(unionMerchantDetails);*/
    }

    /**
     * 商户配置
     *
     * @param configurer 支付消息配置
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    override fun configure(configurer: PayMessageConfigurer) {
        val aliPaymentPlatform = PaymentPlatforms.getPaymentPlatform(AliPaymentPlatform.platformName)
        configurer.addHandler(aliPaymentPlatform, aliPayMessageHandler)
        configurer.addInterceptor(aliPaymentPlatform, spring!!.getBean(AliPayMessageInterceptor::class.java))
        configurer.addHandler(PaymentPlatforms.getPaymentPlatform(WxPaymentPlatform.platformName), mWxPayMessageHandler)
    }

}
