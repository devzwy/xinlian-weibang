package com.xinlian.wb.util.ktx

import cn.jiguang.common.ClientConfig
import cn.jiguang.common.resp.APIConnectionException
import cn.jiguang.common.resp.APIRequestException
import cn.jpush.api.JPushClient
import cn.jpush.api.push.model.Message
import cn.jpush.api.push.model.Platform
import cn.jpush.api.push.model.PushPayload
import cn.jpush.api.push.model.audience.Audience
import com.google.gson.Gson
import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.enc_utils.RSA
import com.xinlian.wb.jdbc.repositorys.SystemNotificationRepository
import com.xinlian.wb.jdbc.tabs.SystemNotification
import com.xinlian.wb.util.Constant
import org.slf4j.LoggerFactory


/**
 * Description: 字符串处理相关
 * Create by lxj, at 2018/12/7
 */

/**
 * 是否是手机号
 */
fun String.isPhone() = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$".toRegex().matches(this)

/**
 * 是否是邮箱地址
 */
fun String.isEmail() = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?".toRegex().matches(this)

/**
 * 是否是身份证号码
 */
fun String.isIDCard() = "[1-9]\\d{16}[a-zA-Z0-9]".toRegex().matches(this)

/**
 * 是否是中文字符
 */
fun String.isChinese() = "^[\u4E00-\u9FA5]+$".toRegex().matches(this)

fun String.getErrorRespons(code: Int = Constant.Code.ERROR__DEFAULT) = HttpResponse<Any>(code = code, message = this, data = null)
fun String.getErrorRespons2(code: Int = Constant.Code.ERROR__DEFAULT) = HttpResponse<Any?>(code = code, message = this, data = null)


//向所有用户发送透传
fun String.pushMessageAll() {
    getJGPushMessageClient(PushPayload.newBuilder().setMessage(Message.newBuilder()
                    .setTitle("威帮")
                    .addExtra("pushData", this)
                    .setMsgContent("")
                    .build())
            .setPlatform(Platform.all())
            .setAudience(Audience.all())
            .build())
}

fun getJGPushMessageClient(pushPayload: PushPayload) {
    val logger = LoggerFactory.getLogger(String::class.java)
    val jpushClient = JPushClient(Constant.OtherKey.JGMastKey, Constant.OtherKey.JGAppKey, null, ClientConfig.getInstance())
//    val payload: PushPayload = PushPayload.messageAll(message)
    try {
        val result = jpushClient.sendPush(pushPayload)
        logger.info("Got result - $result")
    } catch (e: APIConnectionException) { // Connection error, should retry later
        logger.error("Connection error, should retry later", e)
    } catch (e: APIRequestException) { // Should review the error, and fix the request
        logger.error("Should review the error, and fix the request", e)
        logger.info("HTTP Status: " + e.status)
        logger.info("Error Code: " + e.errorCode)
        logger.info("Error Message: " + e.errorMessage)
    }
}

val publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAotNeP1Kly5GpSppIARDBsh5L63N2zHJc349cWxUYD0+dj+mhdIP/J2WXoqpzFP+ysqJLJxIrJkE73R7R5x7voDdL+VYmgTOtLgbpV4ZuS+7/0yo8kiEXeIM6npUHM7/xlPqdLF5iWOzsc+5YvTSoXqhLSDYSfmqX1q3lDQT86i+8g8D/rX6GBQfct6wfH/i0LBhJe5ylvKhKcGjvH6ErMPrjE6XUAg0s9W/yWGfK/kRWGtWxkoO+ZFpYhP2YOrXMb/Zey1pIfs8/lQdXba9abTiYtb1JsDVWdVBMAAJhUytCbRDEUCd2Yb26gyoaPWdnJdERPTkY3sDFTHxV9M1sCQIDAQAB"
val privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCi014/UqXLkalKmkgBEMGyHkvrc3bMclzfj1xbFRgPT52P6aF0g/8nZZeiqnMU/7KyoksnEismQTvdHtHnHu+gN0v5ViaBM60uBulXhm5L7v/TKjySIRd4gzqelQczv/GU+p0sXmJY7Oxz7li9NKheqEtINhJ+apfWreUNBPzqL7yDwP+tfoYFB9y3rB8f+LQsGEl7nKW8qEpwaO8foSsw+uMTpdQCDSz1b/JYZ8r+RFYa1bGSg75kWliE/Zg6tcxv9l7LWkh+zz+VB1dtr1ptOJi1vUmwNVZ1UEwAAmFTK0JtEMRQJ3ZhvbqDKho9Z2cl0RE9ORjewMVMfFX0zWwJAgMBAAECggEAeYTELwAwimgQb4QLPvhRhuyDfppyzAyB8bsdk0B/71Vc4z3a6IlmkPGKJLWPI9nddOIYsnUlzOwckF9jClmVPv5l7hT0sbJuig5Qcaj7giJBvUglYm3eBWvzAM16TY3v717GlIlwXquboL3+bl7xVYvGe4MXdE97OJYZfwj89I0vSPruZb3QATJS9c178bWgQPSY8IabBsXU0Hf4EHjrJL+y6yJ+YPi07oQDOqAIwK3Mufo7AqjREbjR43DMY3LU7Ow05xB85uH1P/ze1z0GtGrExjqab0aRT3jzAjRqqBg6DjOMUgECCZG0uBWk7iE4xrxO55atX8690Boq6VmyMQKBgQDkB8JxYvKFYiurYxnxYsLQ41YzoC9bOSIjrV1n5YUNQneW8WH7v9DffIemMpzVBxVcp8t89+4S6xErEYtRZCSCww44BvvxQXAK3W8RoCvi8r6LyjhKnY0OzGqkk0EuXuN5KpP6HYF2ulkyDOD3wZ19FSKX0VpIg4FvvAQhIyEOVQKBgQC2zCg4wPMCROXP3LnSgRmtQX/W2CskFBvTLxIUEk+ES3F8469pQkmWRqlNaojlM9u7FmSjdzgtJgDJxfugzdSxyT+Ms/U4q9djZOxXjlGnsmj14UT1zzDtEHX37ZkGCzoT2jZuhYGQiHoRxuyI5fmqIHuHSWlzwl9KLmPJ1rMy5QKBgQCrmstR2Uz55C9JA4N6jQBfgzZUE7CPzidLAiTRE4FVwTeOeIlsk6X1ChprkJtGFdaVrBEPMuYPhqec6c8WqW5wmaoRr+/aV4yiIJJ9iTR9zoBnYv+J55dIE74NrGPZKb+2Z7yE9b+AQizt5ZNH4IVMpKMr7XksKQs3sx7IcU9nIQKBgD3BmXEbFr5UgoOIIatRfFhBQaxW2bRVqtTdGTF4wi6CwnOcBH3+LBg+BSKndFpi+8AoH5XuSCdQqIGChrFb+Jib0gF6JsWfoKPuy74E0edi6fzvvzmAZxogLoq1VbZqApQEa9FI/23R/dOVrgHOGFv2n2UkUyENsN3B8GqXQ3FVAoGAQ1VvyyXOWeHfqd7AdzGoiWk1LVCS/F18+85IrUL0nuDPczqWEwlhUrn1+g4P2k8JpEomLJlArNGgLj4x9khFChtisaAB5jr1hUS62CXJZeNF+EKXxbncY9zrC//YfxkDvShrmq2+QcEj7hy46Y0dFEe+FeGwj3mZPOLBNVwxZOY="

//向单个用户发送透传
fun String.pushMessageByDeviceId(deviceId: String) {
    val payload = PushPayload.newBuilder()
            .setPlatform(Platform.all())
            .setAudience(Audience.registrationId(deviceId))
            .setMessage(Message.newBuilder()
                    .setTitle("威帮")
                    .addExtra("pushData", this)
                    .setMsgContent("")
                    .build())
            .setPlatform(Platform.all())
            .build()
    getJGPushMessageClient(payload)
}

fun Any.toJsonStr(): String {
    return Gson().toJson(this)
}

fun String.decrypt(): String? {
    try {
        return RSA.decrypt(this, privateKey)
    } catch (e: Exception) {
        return null
    }
}

fun String.encrypt(): String? {
    try {
        return RSA.encrypt(this, publicKey)
    } catch (e: Exception) {
        return null
    }
}

fun SystemNotification.sendNotification(systemNotificationRepository: SystemNotificationRepository) {
    systemNotificationRepository.saveAndFlush(this)
}
