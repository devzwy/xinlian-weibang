package com.xinlian.wb.core

import com.xinlian.wb.util.Helper
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class BodyReaderHttpServletRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    /**
     * 获取请求体
     * @return 请求体
     */
    val body: String

    init {
        // 将body数据存储起来
        body = getBody(request)
    }

    /**
     * 获取请求体
     * @param request 请求
     * @return 请求体
     */
    private fun getBody(request: HttpServletRequest): String {
        try {
            return Helper.getBodyString(request)
        } catch (e: IOException) {
            mLogger.debug(e.message)
            throw RuntimeException(e)
        }

    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        // 创建字节数组输入流
        val bais = ByteArrayInputStream(body.toByteArray(Charset.defaultCharset()))

        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {

            }

            @Throws(IOException::class)
            override fun read(): Int {
                return bais.read()
            }
        }
    }

    companion object {
        /**
         * 日志
         */
        private val mLogger = LoggerFactory.getLogger(BodyReaderHttpServletRequestWrapper::class.java)
    }

}