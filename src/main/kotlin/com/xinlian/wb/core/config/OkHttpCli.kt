package com.xinlian.wb.core.config

import com.google.gson.Gson
import okhttp3.*
import org.codehaus.plexus.util.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OkHttpCli {
    @Autowired
    private val okHttpClient: OkHttpClient? = null
    private val logger = LoggerFactory.getLogger(OkHttpCli::class.java)

    /**
     * get 请求
     * @param url       请求url地址
     * @param headers   请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    fun doGet(url: String?, headers: Array<String?>?): String {
        return doGet(url, null, headers)
    }
    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @param headers   请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    /**
     * get 请求
     * @param url       请求url地址
     * @return string
     */
    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    @JvmOverloads
    fun doGet(url: String?, params: Map<String?, String?>? = null, headers: Array<String?>? = null): String {
        val sb = StringBuilder(url)
        if (params != null && params.keys.size > 0) {
            var firstFlag = true
            for (key in params.keys) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params[key])
                    firstFlag = false
                } else {
                    sb.append("&").append(key).append("=").append(params[key])
                }
            }
        }
        val builder = Request.Builder()
        if (headers != null && headers.size > 0) {
            if (headers.size % 2 == 0) {
                var i = 0
                while (i < headers.size) {
                    builder.addHeader(headers[i], headers[i + 1])
                    i = i + 2
                }
            } else {
                logger.info("headers's length[{}] is error.", headers.size)
            }
        }
        val request = builder.url(sb.toString()).build()
        logger.info("do get request and url[{}]", sb.toString())
        return execute(request)
    }

    private fun getParamStr(params: Map<String, String>): String {
        val sb = StringBuilder()
        if (params.keys.isNotEmpty()) {
            var firstFlag = true
            for (key in params.keys) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params[key])
                    firstFlag = false
                } else {
                    sb.append("&").append(key).append("=").append(params[key])
                }
            }
        }
        return sb.toString()
    }

    /**
     * post 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    fun doPost(url: String?, params: Map<String?, String?>?): String {
        val builder = FormBody.Builder()
        if (params != null && params.keys.size > 0) {
            for (key in params.keys) {
                builder.add(key, params[key])
            }
        }
        val request = Request.Builder().url(url).post(builder.build()).build()
        logger.info("do post request and url[{}]", url)
        return execute(request)
    }

    /**
     * post 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    fun doPost(url: String?, params: Map<String?, String?>?, headers: Headers): String {
        val builder = FormBody.Builder()
        if (params != null && params.keys.size > 0) {
            for (key in params.keys) {
                builder.add(key, params[key])
            }
        }
        val request = Request.Builder().url(url)
                .headers(headers)
                .post(builder.build()).build()
        logger.info("do post request and url[{}]", url)
        return execute(request)
    }

    /**
     * post 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    fun doAuthCardPost(url: String, params: Map<String?, String?>?, headers: Headers): String {
//        val builder = FormBody.Builder()
//        if (params != null && params.keys.size > 0) {
//            for (key in params.keys) {
//                builder.add(key, params[key])
//            }
//        }
        val requestBody = RequestBody.create(FORM, Gson().toJson(params))
        val request = Request.Builder().url(url)
                .headers(headers)
                .post(requestBody).build()
        logger.info("do post request and url[{}]", url)
        return execute(request)
    }

    /**
     * post 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    fun doPostGetMsg(url: String, params: Map<String, String>, headers: Headers): String {

        val builder = FormBody.Builder()
        if (params != null && params.keys.size > 0) {
            for (key in params.keys) {
                builder.add(key, params[key])
            }
        }

        val request = Request.Builder().url(url).headers(headers).post(builder.build()).build()
        logger.info("do post request and url[{}]", url)
        return execute(request)
    }


    /**
     * post 请求, 请求数据为 json 的字符串
     * @param url       请求url地址
     * @param json      请求数据, json 字符串
     * @return string
     */
    fun doPostJson(url: String, json: String): String {
        logger.info("do post request and url[{}]", url)
        return exectePost(url, json, JSON)
    }

    /**
     * post 请求, 请求数据为 json 的字符串
     * @param url       请求url地址
     * @param json      请求数据, json 字符串
     * @return string
     */
    fun doPostJson(url: String, json: String, mHeaders: Headers): String {
        logger.info("do post request and url[{}]", url)
        val requestBody = RequestBody.create(JSON, json)
        val request = Request.Builder().url(url).headers(mHeaders).post(requestBody).build()
        return execute(request)
    }


    /**
     * post 请求, 请求数据为 xml 的字符串
     * @param url       请求url地址
     * @param xml       请求数据, xml 字符串
     * @return string
     */
    fun doPostXml(url: String, xml: String): String {
        logger.info("do post request and url[{}]", url)
        return exectePost(url, xml, XML)
    }

    private fun exectePost(url: String, data: String, contentType: MediaType?): String {
        val requestBody = RequestBody.create(contentType, data)
        val request = Request.Builder().url(url).post(requestBody).build()
        return execute(request)
    }

    private fun execute(request: Request): String {
        var response: Response? = null
        try {
            response = okHttpClient!!.newCall(request).execute()
            if (response.isSuccessful) {
                return response.body()!!.string()
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            response?.close()
        }
        return ""
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private val XML = MediaType.parse("application/xml; charset=utf-8")
        private val FORM = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8")

    }
}