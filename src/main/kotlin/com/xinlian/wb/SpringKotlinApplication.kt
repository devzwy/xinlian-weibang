package com.xinlian.wb

import com.xinlian.wb.other_utils.RedisUtil
import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean


/**
 * 程序入口
 */
@SpringBootApplication
@ServletComponentScan
@EnableCaching
open class SpringKotlinApplication {


    /**
     * 注入的 redis数据库操作对象
     */
    companion object {
        private val logger = LoggerFactory.getLogger(SpringKotlinApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SpringKotlinApplication::class.java, *args)
            logger.info("清除redis缓存的Api信息")
            RedisUtil.delApiCache()
            RedisUtil.deleteAllExpMsgKey()
            logger.info("\n" +
                    "  .--,       .--,\n" +
                    " ( (  \\.---./  ) )\n" +
                    "  '.__/o   o\\__.'\n" +
                    "     {=  ^  =}\n" +
                    "      >  -  <\n" +

                    "     /       \\\n" +
                    "    //       \\\\\n" +
                    "   //|   .   |\\\\\n" +
                    "   \"'\\       /'\"_.-~^`'-.\n" +
                    "      \\  _  /--'         `\n" +
                    "    ___)( )(___\n" +
                    "   (((__) (__)))    云出十里,未及孤村,长夜无眠,为海祈梦。\n\n"
            )


        }
    }

    /**
     * http重定向到https
     * @return
     */
    @Bean
    open fun servletContainer(): TomcatServletWebServerFactory? {
        val tomcat: TomcatServletWebServerFactory = object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                val constraint = SecurityConstraint()
                constraint.userConstraint = "CONFIDENTIAL"
                val collection = SecurityCollection()
                collection.addPattern("/*")
                constraint.addCollection(collection)
                context.addConstraint(constraint)
            }
        }
        tomcat.addAdditionalTomcatConnectors(httpConnector())
        return tomcat
    }

    @Bean
    open fun httpConnector(): Connector? {
        val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
        connector.scheme = "http"
        //Connector监听的http的端口号
        connector.port = 80
        connector.secure = false
        //监听到http的端口号后转向到的https的端口号
        connector.redirectPort = 443
        return connector
    }


}