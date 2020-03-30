package com.xinlian.wb.util

class Constant {

//    object CacheKey{
//        /**
//         * web 获取城市信息的缓存key
//         */
//        @JvmField
//        val ERROR__DEFAULT = -0x01
//    }

    object Code {

        /**
         * 默认的错误码
         */
        @JvmField
        val ERROR__DEFAULT = -0x01


        /**
         * 操作成功回传code
         */
        @JvmField
        val SUCCESSFUL_CODE = 9000


        /**
         * 用户身份失效
         */
        @JvmField
        val USER_AUTH_FAILD_CODE = -0x02


        /**
         * 用户被封禁
         */
        @JvmField
        val USER_HAS_BEEN_BANNED_CODE = -0x03


        /**
         * 用户不存在
         */
        @JvmField
        val USER_NOT_EXIST_CODE = -0x04


        /**
         * IP封禁
         */
        @JvmField
        val IP_FILETER_CODE = -0x05


    }

    object RedisKey {

        /**
         * 需求过期key
         */
        @JvmField
        val DEMAND_KEY = "DEMAND_KEY_"

        /**
         * 短信验证码
         */
        @JvmField
        val MSG_CODE = "MSG_CODE_"

        /**
         * 用户token存储的key 头
         */
        @JvmField
        val USER_TOKEN = "USER_TOKEN_"


        /**
         * 日志
         */
        @JvmField
        val LOGTAG = "LOGTAG_"


        /**
         * 注册码过期Key
         */
        @JvmField
        val REGISTERCODE = "REGISTERCODE_"

        /**
         * 订单过期Key
         */
        @JvmField
        val ORDER_REDIS_KEY = "ORDER_REDIS_KEY_"

        /**
         * 订单超时未评价过期Key
         */
        @JvmField
        val ORDER_REDIS_KEY_COMM = "ORDER_REDIS_KEY_COMM_"


    }

    object finalParams {
        /**
         * redis中存储的短信验证码有效期 单位 秒
         */
        @JvmField
        val MSG_CODE_EXPIRETIME = 5 * 60L

        /**
         * redis中存储的订单超时时间 单位 秒
         */
        @JvmField
        val ORDER_EXPIRETIME = 30 * 60L

        /**
         * redis中存储的订单超时自动好评时间 单位 秒
         */
        @JvmField
        val ORDER_COMM_EXPIRETIME = 3 * 24 * 60 * 60L

        /**
         * redis中存储的注册码过期时间 单位 秒
         */
        @JvmField
        val REGISTER_CODE_EXPIRETIME = 24 * 60 * 60L

        /**
         * IP限制时长 秒
         */
        @JvmField
        val IPFILTER_TIME = 5 * 60 * 1000L

        /**
         * 用户连续访问最高阀值，超过该值则认定为恶意操作的IP，进行限制
         */
        @JvmField
        val LIMIT_NUMBER = 5

        /**
         * 用户访问最小安全时间，在该时间内如果访问次数大于阀值，则记录为恶意IP，否则视为正常访问
         */
        @JvmField
        val MIN_SAFE_TIME = 5000

        /**
         * 用户注册网易云信即时通讯时的accid的前缀
         */
        @JvmField
        val WYY_ACCID_HEANDER = "yxAccId_wb_"

        /**
         * 用户注册网易云信即时通讯时的token的前缀
         */
        @JvmField
        val WYY_TOKEN_HEANDER = "yxToken_wb_v2_"


        /**
         * 系统生成的用户默认头像链接
         */
        @JvmField
        val DEFULT_USER_LOGO_URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658"

        /**
         * 反地理编码调用的URL
         */
        @JvmField
        val CITY_RE_GET_URL = "http://restapi.amap.com/v3/geocode/regeo?key="


    }

    object OtherKey {
        /**
         * 阿里云短信APPID
         */
        @JvmField
        val ALIYUN_MSGCODE_APPID = "LTAI4FwL6KjQejnioiUdzVid"


        /**
         * 阿里云短信appSecret
         */
        @JvmField
        val ALIYUN_MSGCODE_APPSECRET = "GHBIg0BPDlBZKK44W7GSHNGwioM0Bl"


        /**
         * 网易云即时通讯Appkey
         */
        @JvmField
        val WYY_CHAT_APPKEY = "76f606d1dfc8590ce7f2a7490075255b"

        /**
         * 网易云即时通讯appSecret
         */
        @JvmField
        val WYY_CHAT_APPSECRET = "61442df3e1bb"


        /**
         * 友盟Appkey
         */
        @JvmField
        val JGMastKey = "5a98a887f1547976b575e417"

        /**
         * 友盟Appkey
         */
        @JvmField
        val JGAppKey = "775cdfd9acb54656d7d2ebbd"

        /**
         * 用友实名apikey
         */
        @JvmField
        val YYApiKey = "353273ab379d49c28efdea0ccc7e20e3"

        /**
         * 七牛云上传图片的AccessKey
         */
        @JvmField
        val QN_ACCESSKEY = "nEydSRyh_lSqvstx0RSnBP-o4KzCDa_WOHEIFae_"

        /**
         * 七牛云上传图片的SecretKey
         */
        @JvmField
        val QN_SECRETKEY = "cf-IcTTLoGbU3dd-KQWMkb9ZNrGEBACXaW2bkTr4"

        /**
         * 七牛云上传图片的空间名  shcyapp
         */
        @JvmField
        val QN_BUCKET = "shcyapp"

        /**
         * 高德key
         */
        @JvmField
        val GAODE_KEY = "9b73c03a2934acf2c37bdba992bafa65"
    }


}