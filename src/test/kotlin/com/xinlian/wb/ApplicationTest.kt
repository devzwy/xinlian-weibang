package com.xinlian.wb

import com.xinlian.wb.core.entity.PushData
import com.xinlian.wb.jdbc.repositorys.UserRepository
import com.xinlian.wb.other_utils.RedisUtil
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.ktx.pushMessageByDeviceId
import com.xinlian.wb.util.ktx.toJsonStr
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTest {
    //test
    private val logger = LoggerFactory.getLogger(ApplicationTest::class.java)
    @Autowired
    private lateinit var userRepository: UserRepository
    /**
     * redis操作对象
     *  redis[0] 验证码
     *  redis[1] token
     *  redis[2] 日志
     */


    @Test
    fun test() {
        PushData(pType = 1,title = "标题",content = "内容",bussContent = "空数据").toJsonStr().pushMessageByDeviceId("1111111111111111111111111111")
    }

    @Test
    fun test2() {
//        PushData(pType = 1,title = "标题",content = "内容",bussContent = "空数据").toJsonStr().pushMessageByDeviceId("1111111111111111111111111111")
        val time = Date().time.toString()
        val aa = CheckSumBuilder.getCheckSum("18ca14972297","1234567890",time)
        logger.info("签名：${aa}")
        logger.info("随机数：1234567890")
        logger.info("时间：${time}")
    }

    @Test
    fun test3(){
        RedisUtil.set("TEST-1", "", 20)
    }

}