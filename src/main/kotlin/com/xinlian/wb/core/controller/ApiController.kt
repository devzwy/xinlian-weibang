package com.xinlian.wb.core.controller

import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.Dict
import com.alibaba.fastjson.JSON
import com.xinlian.wb.api.ApiConfig
import com.xinlian.wb.api.ApiController2
import com.xinlian.wb.api.util.AnnotationParse
import com.xinlian.wb.redis.RedisConfiguration
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException
import java.util.function.BiConsumer


/**
 * @author
 */
@Controller
open class ApiController {
    internal var logger = LoggerFactory.getLogger(RedisConfiguration::class.java)

    /**
     * 生成解析实例
     */
//    public val AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters) = AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters).getInstance(readApiConfig().packages).filter(readApiConfig().filters)
    /** 主题 */

    /**
     * hello
     *
     * @return
     */
    @RequestMapping("/api")
    fun index(): String {
        return "api/index"
    }

    /**
     * hello
     *
     * @return
     */
    @RequestMapping("/api/help")
    fun help(): String {
        return "api/help"
    }

    /**
     * 获取主题色，暂时只支持：dark、light
     *
     * @author Jason
     * @date 2018/6/21 下午2:59
     */
    @RequestMapping("/api/getTheme")
    @ResponseBody
    fun getTheme(): String {
        return JSON.toJSONString(Dict.create().set("theme", readApiConfig().theme))
    }

    /**
     * 获取菜单数据
     *
     * @author Jason
     * @date 2018/5/19 下午1:55
     */
    @RequestMapping("/api/getMenuData")
    @Cacheable(value = arrayOf("apiCache"), key = "'getMenuData'")
    @ResponseBody
    open fun getMenuData(): String {
        //缓存
        return ApiController2.getMenuData(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }

    /**
     * 首页统计
     *
     * @author Jason
     * @date 2018/6/12 下午4:55
     */
    @RequestMapping("/api/getApiHome")
    @Cacheable(value = arrayOf("apiCache"), key = "'getApiHome'")
    @ResponseBody
    open fun getApiHome(): String {
        return ApiController2.getApiHome(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }

    /**
     * 接口贡献度查询
     *
     * @author Jason
     * @date 2018/6/19 下午6:53
     */
    @RequestMapping("/api/getCalendarData")
    @Cacheable(value = arrayOf("apiCache"), key = "'getCalendarData'")
    @ResponseBody
    open fun getCalendarData(): String {
        //缓存
        return ApiController2.getCalendarData(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }

    /**
     * 周更新
     *
     * @author Jason
     * @date 2018/6/20 下午4:25
     */
    @RequestMapping("/api/getWeekGroup")
    @Cacheable(value = arrayOf("apiCache"), key = "'getWeekGroup'")
    @ResponseBody
    open fun getWeekGroup(): String {
        //缓存
        return ApiController2.getWeekGroup(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }

    /**
     * 模块分组
     */
    @RequestMapping("/api/getModelGroup")
    @Cacheable(value = arrayOf("apiCache"), key = "'getModelGroup'")
    @ResponseBody
    open fun getModelGroup(): String {
        //缓存
        return ApiController2.getModelGroup(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }


    /**
     * 获取接口分组数据 [作者统计]
     *
     * @author Jason
     * @date 2018/6/20 上午11:49
     */
    @RequestMapping("/api/getAuthorGroup")
    @Cacheable(value = arrayOf("apiCache"), key = "'getAuthorGroup'")
    @ResponseBody
    open fun getAuthorGroup(): String {
        //缓存
        return ApiController2.getAuthorGroup(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters))
    }

    /**
     * 获取API名称
     *
     * @author Jason
     * @date 2018/5/19 下午1:56
     */
    @RequestMapping("/api/getMethodTitle")
    @Cacheable(value = arrayOf("apiCache"), key = "#actionName+'_getMethodTitle_'+#method")
    @ResponseBody
    open fun getMethodTitle(actionName: String, method: String): String {
        return ApiController2.getMethodTitle(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters), actionName, method)
    }

    /**
     * 获取参数数据
     * action controller 名称
     * method 方法名称
     * paramType 参数类型：1 入参，2 出参
     *
     * @author Jason
     * @date 2018/5/19 下午10:54
     */
    @RequestMapping("/api/getReqParam")
    @Cacheable(value = arrayOf("apiCache"), key = "#actionName+'_'+#method+'_'+#paramType")
    @ResponseBody
    open fun getReqParam(actionName: String, method: String, paramType: String): String {
        return ApiController2.getReqParam(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters), actionName, method, paramType)
    }

    /**
     * 获取数据请求数据
     *
     * @author Jason
     * @date 2018/5/20 下午12:24
     */
    @RequestMapping("/api/getPostManData")
    @Cacheable(value = arrayOf("apiCache"), key = "#actionName+'_'+#method")
    @ResponseBody
    open fun getPostManData(actionName: String, method: String): String {
        return ApiController2.getPostManData(AnnotationParse.getInstance(readApiConfig().packages).filter(readApiConfig().filters), actionName, method)
    }

    private fun filterList(dict: Dict, pre: String): List<String> {
        return ApiController2.filterList(dict, pre)
    }

    /**
     * 解析表格数据json
     *
     * @return
     */
    private fun parseApiTableJson(paramList: List<String>, accumulator: BiConsumer<Dict, String>): String {
        return ApiController2.parseApiTableJson(paramList, accumulator)
    }

    private fun getLastKv(list: List<Dict>, keys: List<String>): Dict? {
        return ApiController2.getLastKv(list, keys)
    }

    private fun getKvByName(list: List<Dict>, name: String): Dict? {
        var kv: Dict? = null
        try {
            kv = list.stream().filter { p -> p["name"] == name }.findFirst().get()
        } catch (e: Exception) {
        }

        return kv
    }

    /**
     * 读取配置信息
     */
    fun readApiConfig(): ApiConfig {

//        var resources: Array<Resource>
//        resources = ResourcePatternUtils.getResourcePatternResolver(null).getResources("classpath:template")
//        val parentPath: String = resources[0].getFile().getParent() //打成jar包后报错

//        resources = ResourcePatternUtils.getResourcePatternResolver(null).getResources("classpath:")
//        for (resource in resources) {
//            val `is`: InputStream = resource.getInputStream()
//            val encoded: ByteArray = toByteArray(`is`)
//            val content = String(encoded, Charset.forName("UTF-8"))
//            println(content) //可成功获得文件里面的内容
//            logger.info("测试测试;"+content)
////            System.out.println(resource.getFilename()) //打成jar包后报错
////            System.out.println(resource.getFile().getPath().replace(parentPath, "")) //打成jar包后报错
//        }


//        return ApiController2.readApiConfig()
        var apiConfig = ApiConfig()
        val yaml = Yaml()
//        val url = ApiController::class.java.classLoader.getResource("application.yml")
        val url: Resource = ClassPathResource("application-release.yml")

//        ResourcePatternUtils.getResourcePatternResolver(null).getResources("classpath:template");
//        val reader = InputStreamReader(input, Charset.forName("UTF-8"))
//        val url = reader.encoding
        if (url != null) {
            try { //获取test.yaml文件中的配置数据，然后转换为obj，
                val obj = yaml.load<Any>(url.inputStream)
                val map = Convert.convert<Map<*, *>>(MutableMap::class.java, obj)
                val apiMap = Convert.convert<Map<*, *>>(MutableMap::class.java, map["api"])
                apiConfig = JSON.parseObject(JSON.toJSONString(apiMap), ApiConfig::class.java)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return apiConfig
    }

}