//package com.xinlian.wb.redis
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect
//import com.fasterxml.jackson.annotation.PropertyAccessor
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.databind.SerializationFeature
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import com.xinlian.wb.SpringKotlinApplication
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.cache.annotation.CachingConfigurerSupport
//import org.springframework.cache.annotation.EnableCaching
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Import
//import org.springframework.data.redis.connection.RedisConnectionFactory
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
//import org.springframework.data.redis.serializer.StringRedisSerializer
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPool
//import redis.clients.jedis.JedisPoolConfig
//import javax.annotation.PostConstruct
//import org.springframework.data.redis.listener.RedisMessageListenerContainer
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.data.redis.listener.ChannelTopic
//
//
//@Configuration
//@EnableCaching
//@Import(SpringKotlinApplication::class)
//open class RedisCacheConfiguration : CachingConfigurerSupport() {
//
//
//    @Autowired
//    private var redisMessageListener: RedisKeyExpiredListener? = null
//
//
//    internal var logger = LoggerFactory.getLogger(RedisCacheConfiguration::class.java)
//
//    @Value("\${spring.redis.host}")
//    private val host: String? = null
//
//    @Value("\${spring.redis.port}")
//    private val port: Int = 0
//
//    @Value("\${spring.redis.timeout}")
//    private val timeout: Int = 0
//
//    @Value("\${spring.redis.lettuce.pool.max-idle}")
//    private val maxIdle: Int = 0
//
//    @Value("\${spring.redis.lettuce.pool.max-wait}")
//    private val maxWaitMillis: Long = 0
//
//    @Value("\${spring.redis.password}")
//    private val password: String? = null
//
//
//    @Bean
//    open fun redisPoolFactory(): JedisPool {
//        logger.info("JedisPool注入成功！！")
//        logger.info("redis地址：$host:$port")
//        val jedisPoolConfig = JedisPoolConfig()
//        jedisPoolConfig.maxIdle = maxIdle
//        jedisPoolConfig.maxWaitMillis = maxWaitMillis
//        jedisPoolConfig.setTestOnBorrow(false);
//        val pool = JedisPool(jedisPoolConfig, host, port, timeout, password)
//        pool.resource.auth(password)
//        return pool
//    }
//
//    @Bean
//    open fun JedisFactory(): Jedis {
//        val jd = Jedis(host)
//        jd.auth(password)
//        return Jedis(host)
//    }
//
//    /**
//     * 由于原生的redis自动装配，在存储key和value时，没有设置序列化方式，故自己创建redisTemplate实例
//     * @param factory
//     * @return
//     */
//    @Bean
//    open fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
//        val template = RedisTemplate<String, Any>()
//        template.connectionFactory = factory
//        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
//        val om = ObjectMapper();
////        val om = serializingObjectMapper()
//
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
//        jackson2JsonRedisSerializer.setObjectMapper(om)
//        val stringRedisSerializer = StringRedisSerializer()
//        // key采用String的序列化方式
//        template.keySerializer = stringRedisSerializer
//        // hash的key也采用String的序列化方式
//        template.hashKeySerializer = stringRedisSerializer
////        // value序列化方式采用jackson
//        template.valueSerializer = stringRedisSerializer
////        // hash的value序列化方式采用jackson
////        template.hashValueSerializer = jackson2JsonRedisSerializer
//        template.afterPropertiesSet()
//        logger.info("Redis数据转换器替换成功")
//        return template
//    }
//
//    @Bean
//    open fun expiredTopic(): ChannelTopic {
//        return ChannelTopic("__keyevent@0__:expired")
//    }
//
//    @Bean
//    open fun redisMessageListenerContainer(@Autowired redisConnectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
//        val redisMessageListenerContainer = RedisMessageListenerContainer()
//        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory)
//        redisMessageListenerContainer.addMessageListener(redisMessageListener, expiredTopic());
//        return redisMessageListenerContainer
//    }
//
//
////    @Bean
////    open fun container(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
////        val container = RedisMessageListenerContainer()
////        container.connectionFactory = connectionFactory
////        logger.info("Redis1")
////        return container
////    }
////
////    @Bean
////    open fun keyExpirationListener(listenerContainer: RedisMessageListenerContainer): RedisKeyExpiredListener {
////        logger.info("Redis2")
////        return RedisKeyExpiredListener()
////    }
//
////
////    @Bean
////    open fun serializingObjectMapper(): ObjectMapper {
////        val objectMapper = ObjectMapper()
////        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
////        objectMapper.registerModule(JavaTimeModule())
////        return objectMapper
////    }
//
//
//}