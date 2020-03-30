package com.xinlian.wb.util

import com.aliyuncs.CommonRequest
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.exceptions.ServerException
import com.aliyuncs.profile.DefaultProfile
import com.google.gson.Gson
import com.xinlian.wb.core.config.OkHttpCli
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant.OtherKey.ALIYUN_MSGCODE_APPID
import com.xinlian.wb.util.Constant.OtherKey.ALIYUN_MSGCODE_APPSECRET
import com.xinlian.wb.util.Constant.finalParams.CITY_RE_GET_URL
import okhttp3.Headers
import org.slf4j.LoggerFactory
import java.io.*
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest


object Helper {

    private val logger = LoggerFactory.getLogger(Helper::class.java)

    /**
     * 构造支付订单参数信息
     *
     * @param map
     * 支付订单参数
     * @return
     */
    fun buildOrderParam(map: Map<String, Any>): String {
        val keys = ArrayList(map.keys)

        val sb = StringBuilder()
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            val value = map[key]
            sb.append(buildKeyValue(key, value ?: "", true))
            sb.append("&")
        }

        val tailKey = keys[keys.size - 1]
        val tailValue = map[tailKey]
        sb.append(buildKeyValue(tailKey, tailValue ?: "", true))

        return sb.toString()
    }

    /**
     * 今天返回0 昨天返回 1 其他时间返回-1
     */
    fun getTimeType(time: Long): Int? {
        val date = Date(time)
        val now = Date().time
        val day = (now + 8 * 60 * 1000) / (24 * 60 * 60 * 1000) - (time + 8 * 60 * 1000) / (24 * 60 * 60 * 1000)
        // (now-time)/(……)的结果和上面的结果不一样噢
        return if (day < 1) { //今天
            val format = SimpleDateFormat("HH:mm")
            format.format(date)
            0
        } else if (day == 1L) { //昨天
            val format = SimpleDateFormat("HH:mm")
            "昨天 " + format.format(date)
            1
        } else { //可依次类推
            val format = SimpleDateFormat("yyyy/MM/dd")
            format.format(date)
            -1
        }
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private fun buildKeyValue(key: String, value: Any, isEncode: Boolean): String {
        val sb = StringBuilder()
        sb.append(key)
        sb.append("=")
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value.toString(), "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                sb.append(value)
            }

        } else {
            sb.append(value)
        }
        return sb.toString()
    }

    /**
     * 校验用户身份证认证信息
     */
    fun checkCardNumber(okHttpCli: OkHttpCli, mRequestBean_AuthCard: RequestBean_AuthCard): ResponseBean_Auth_YY {

        val mResponseBean_Auth_YY = okHttpCli.doPostJson("https://api.yonyoucloud.com/apis/dst/matchIdentity/matchIdentity",
                "{\"idNumber\":\"${mRequestBean_AuthCard.cardNumber}\",\"userName\":\"${mRequestBean_AuthCard.userRelName}\"}",
                Headers.of(hashMapOf("Content-type" to "application/json;charset=utf-8",
                        "connection" to "Keep-Alive",
                        "apicode" to Constant.OtherKey.YYApiKey)))
//        val mResponseBean_Auth_YY = HttpRequestUtils.sendPost(HttpRequestUtils.buildCheckCardNumberUrlConnection(),
//                "{\"idNumber\":\"${mRequestBean_AuthCard.cardNumber}\",\"userName\":\"${mRequestBean_AuthCard.userRelName}\"}")
        if (mResponseBean_Auth_YY.isEmpty()) {
            //注册失败
            return ResponseBean_Auth_YY(success = false, message = "请求超时")
        }

        logger.info("实名认证回传:$mResponseBean_Auth_YY")

        val beanAuthYy = Gson().fromJson(mResponseBean_Auth_YY, ResponseBean_Auth_YY::class.java)

        return beanAuthYy
    }

    /**
     * 根据token拆分出userID
     */
    fun getUserIdByToken(token: String): String {
        try {
            return token.split("|")[0]
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 根据token拆分出token
     */
    fun getUserTokenByToken(token: String): String {
        try {
            return token.split("|")[1]
        } catch (e: Exception) {
            return ""
        }
    }

    /***
     * 手机号码检测
     */
    fun checkPhoneNum(num: String): Boolean {
        val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(166)|(19[8,9])|)\\d{8}$"
        val p = Pattern.compile(regExp)
        val m = p.matcher(num)
        return m.matches()
    }

    /**
     * 注册到网易云即时通讯平台
     */
    fun registerAccIdToWyy(okHttpCli: OkHttpCli, phoneNumber: String): RegisterWyyResult {
        //注册网易云信即时通讯accid和token
        val accid = "${Constant.finalParams.WYY_ACCID_HEANDER}${UUID.randomUUID().toString().replace("-", "").substring(0, 21)}"
        logger.info("accid:" + accid)

        val nonce = Random().nextInt(1000000).toString()
        val curTime = (Date().time / 1000).toString()

        val resultStrFromRegisterAccId = okHttpCli.doPost("https://api.netease.im/nimserver/user/create.action",
                mapOf("accid" to accid,
                        "name" to phoneNumber,
                        "icon" to Constant.finalParams.DEFULT_USER_LOGO_URL,
                        "token" to "${Constant.finalParams.WYY_TOKEN_HEANDER}${phoneNumber}"), Headers.of(hashMapOf("Content-type" to "application/x-www-form-urlencoded;charset=utf-8",
                "connection" to "Keep-Alive",
                "AppKey" to Constant.OtherKey.WYY_CHAT_APPKEY,
                "Nonce" to nonce,
                "CurTime" to curTime,
                "CheckSum" to getCheckSum(Constant.OtherKey.WYY_CHAT_APPSECRET, nonce, curTime))))
        logger.info("注册到网易云即时通讯平台的回传:$resultStrFromRegisterAccId")
        if (resultStrFromRegisterAccId.isEmpty()) {
            return RegisterWyyResult(isSucc = false, errorMsg = "注册到即时通讯平台失败,请重试")
        }
        val resultFromWYYRegisterAccId = Gson().fromJson(resultStrFromRegisterAccId, ResultFromWYYRegisterAccId::class.java)

        if (resultFromWYYRegisterAccId.code == 200) {
            return RegisterWyyResult(isSucc = true, accid = resultFromWYYRegisterAccId.info?.accid
                    ?: "", accToken = resultFromWYYRegisterAccId.info?.token ?: "")
        } else {
            return RegisterWyyResult(false, errorMsg = "注册到即时通讯平台失败，${resultFromWYYRegisterAccId.desc}")
        }

    }

    // 计算并获取CheckSum
    fun getCheckSum(appSecret: String, nonce: String, curTime: String): String? {
        return encode("sha1", appSecret + nonce + curTime)
    }


    private fun encode(algorithm: String, value: String?): String? {
        if (value == null) {
            return null
        }
        try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            messageDigest.update(value.toByteArray())
            return getFormattedText(messageDigest.digest())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    private fun getFormattedText(bytes: ByteArray): String {
        val len = bytes.size
        val buf = StringBuilder(len * 2)
        for (j in 0 until len) {
            buf.append(HEX_DIGITS[bytes[j].toInt() shr 4 and 0x0f])
            buf.append(HEX_DIGITS[bytes[j].toInt() and 0x0f])
        }
        return buf.toString()
    }

    /**
     * 获取验证码
     * [phoneNumber] 电话号码
     * [redisTemplate] 写入读取redis的对象
     */
    fun getMsgCode(okHttpCli: OkHttpCli, phoneNumber: String): DoSomeThingResult {
        //生成短信存储的key
        val redisMsgCodeKey = "${Constant.RedisKey.MSG_CODE}${phoneNumber}_${Date().time}"

        //获取验证码之前先查询是否有获取成功的没有失效的验证码
        if (RedisUtil.checkCode(phoneNumber)) {
            return DoSomeThingResult(isSucc = false, errorMsg = "短信验证码已发送，有效期5分钟，无需再次发送。")
        }
        return sendMsgCodeByAliyun(phoneNumber = phoneNumber, redisMsgCodeKey = redisMsgCodeKey)
    }

    /**
     * 根据经纬度反地理编码城市信息
     */
    fun getCityInformation(okHttpCli: OkHttpCli, lng: Double?, lat: Double?): BoundCityBean? {
        val result = okHttpCli.doGet("${CITY_RE_GET_URL}${Constant.OtherKey.GAODE_KEY}&location=$lng,${lat}&poitype=&radius=&extensions=base&batch=true&roadlevel=")
        logger.info(result)
        val cityInformation = Gson().fromJson<CityInformation>(result, CityInformation::class.java)
        if (cityInformation != null && cityInformation.regeocodes != null && cityInformation.regeocodes.size > 0) {
            val cb = cityInformation.regeocodes[0].addressComponent
            val townCode = cb.towncode //110105034000   截取前面6位为county_id
            val county_id = "${townCode.substring(0, 6)}000000"

            logger.info("townCode:$townCode")
            logger.info("county_id:$county_id")
            return BoundCityBean(county_id.toLong(), townCode.toLong())
        }
        return null
    }

    /**
     * 调用阿里云SDK发送短信验证码
     */
    private fun sendMsgCodeByAliyun(phoneNumber: String, redisMsgCodeKey: String): DoSomeThingResult {
        val profile = DefaultProfile.getProfile("cn-hangzhou", ALIYUN_MSGCODE_APPID, ALIYUN_MSGCODE_APPSECRET)
        val client: IAcsClient = DefaultAcsClient(profile)

        val request = CommonRequest()
        request.sysMethod = com.aliyuncs.http.MethodType.POST
        request.sysDomain = "dysmsapi.aliyuncs.com"
        request.sysVersion = "2017-05-25"
        request.sysAction = "SendSms"
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", "威帮APP");
        request.putQueryParameter("TemplateCode", "SMS_180054055");
        val redomCode = (1000..9999).random()
        request.putQueryParameter("TemplateParam", "{\"code\":\"$redomCode\"}")
        try {
            val response = client.getCommonResponse(request)
            logger.info("获取验证码回传：${response.data}")
            if (response == null || response.data.isNullOrEmpty()) {
                //获取失败
                return DoSomeThingResult(isSucc = false, errorMsg = "验证码发送失败，服务器异常")
            }

            val resultFromAliYunMsgCode = Gson().fromJson(response.data, ResultFromAliYunMsgCode::class.java)
            when (resultFromAliYunMsgCode.Code) {
                "OK" -> {
                    //发送成功 将短信验证码存入redis
                    RedisUtil.set(redisMsgCodeKey, redomCode.toString(), Constant.finalParams.MSG_CODE_EXPIRETIME)
                    return DoSomeThingResult()
                }
                "isp.SYSTEM_ERROR" -> {
                    return DoSomeThingResult(isSucc = false, errorMsg = "获取验证码失败,系统错误请重试")
                }

                else -> {
                    return DoSomeThingResult(isSucc = false, errorMsg = resultFromAliYunMsgCode.Message)
                }
            }
        } catch (e: ServerException) {
            return DoSomeThingResult(isSucc = false, errorMsg = "获取验证码失败,${e.localizedMessage}")
        } catch (e: ClientException) {
            return DoSomeThingResult(isSucc = false, errorMsg = "获取验证码失败,${e.localizedMessage}")
        }
    }

    @Throws(IOException::class)
    fun getBodyString(request: HttpServletRequest): String {
        val sb = StringBuilder()
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        try {
            inputStream = request.inputStream
            reader = BufferedReader(
                    InputStreamReader(inputStream!!, Charset.forName("UTF-8")))

            val bodyCharBuffer = CharArray(1024)
            while (true) {
                val len = reader.read(bodyCharBuffer)
                if (len != -1) {
                    sb.append(String(bodyCharBuffer, 0, len))
                } else {
                    break
                }
            }
        } catch (e: IOException) {
//            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return sb.toString()
    }

    fun toMD5(plainText: String): String {
        var secretBytes: ByteArray? = null
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("没有md5这个算法！")
        }

        var md5code = BigInteger(1, secretBytes!!).toString(16)// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (i in 0 until 32 - md5code.length) {
            md5code = "0$md5code"
        }
        return md5code
    }

    /**
     * 传入经纬度  返回米
     */
    fun getDistance(lat1: Double?, lng1: Double?, lat2: Double?, lng2: Double?): Double { // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        val radiansAX = Math.toRadians(lng1 ?: 0.0) // A经弧度
        val radiansAY = Math.toRadians(lat1 ?: 0.0) // A纬弧度
        val radiansBX = Math.toRadians(lng2 ?: 0.0) // B经弧度
        val radiansBY = Math.toRadians(lat2 ?: 0.0) // B纬弧度
        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        val cos = (Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY))
        //        System.out.println("cos = " + cos); // 值域[-1,1]
        val acos = Math.acos(cos) // 反余弦值
        //        System.out.println("acos = " + acos); // 值域[0,π]
//        System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
        return 6371393 * acos // 最终结果
    }


}