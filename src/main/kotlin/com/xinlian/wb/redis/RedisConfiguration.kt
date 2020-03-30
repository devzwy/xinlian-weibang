package com.xinlian.wb.redis

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.clients.jedis.JedisPoolConfig


@Configuration
@EnableCaching
open class RedisConfiguration {
    @Value("\${spring.redis.host}")
    private val host: String? = null

    @Value("\${spring.redis.port}")
    private val port: Int = 0

    @Value("\${spring.redis.timeout}")
    private val timeout: Int = 0

    @Value("\${spring.redis.lettuce.pool.max-idle}")
    private val maxIdle: Int = 0

    @Value("\${spring.redis.lettuce.pool.max-wait}")
    private val maxWaitMillis: Long = 0

    @Value("\${spring.redis.password}")
    private val password: String? = null

    @Autowired
    private val redisConnectionFactory: RedisConnectionFactory? = null

    internal var logger = LoggerFactory.getLogger(RedisConfiguration::class.java)

    @Bean
    open fun expiredTopic(): ChannelTopic {
        return ChannelTopic("__keyevent@0__:expired")  // 选择0号数据库
    }

    @Bean
    open fun redisMessageListenerContainer(): RedisMessageListenerContainer {
        val redisMessageListenerContainer = RedisMessageListenerContainer()
        redisMessageListenerContainer.connectionFactory = redisConnectionFactory!!
        return redisMessageListenerContainer
    }

    /**
     * 由于原生的redis自动装配，在存储key和value时，没有设置序列化方式，故自己创建redisTemplate实例
     * @param factory
     * @return
     */
    @Bean
    open fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = factory
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        val om = ObjectMapper();
//        val om = serializingObjectMapper()

        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        jackson2JsonRedisSerializer.setObjectMapper(om)
        val stringRedisSerializer = StringRedisSerializer()
        // key采用String的序列化方式
        template.keySerializer = stringRedisSerializer
        // hash的key也采用String的序列化方式
        template.hashKeySerializer = stringRedisSerializer
//        // value序列化方式采用jackson
        template.valueSerializer = stringRedisSerializer
//        // hash的value序列化方式采用jackson
//        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        logger.info("Redis_DB_0 注入成功，host:$host ,port:$port ")
        return template
    }

    @Bean
    open fun serviceRedisTemplate(factory2: RedisConnectionFactory): RedisTemplate<String, Any>? {
        val factory = JedisConnectionFactory()
        val jedisPoolConfig = JedisPoolConfig()
        jedisPoolConfig.maxIdle = maxIdle
        jedisPoolConfig.minIdle = 0
        jedisPoolConfig.maxWaitMillis = -1
        factory.setPoolConfig(jedisPoolConfig)
        factory.database = 1
        factory.hostName = host
        factory.password = password
        factory.port = port
        factory.timeout = timeout
        factory.afterPropertiesSet()

        val template = RedisTemplate<String, Any>()
        template.connectionFactory = factory
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        val om = ObjectMapper();
//        val om = serializingObjectMapper()

        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        jackson2JsonRedisSerializer.setObjectMapper(om)
        val stringRedisSerializer = StringRedisSerializer()
        // key采用String的序列化方式
        template.keySerializer = stringRedisSerializer
        // hash的key也采用String的序列化方式
        template.hashKeySerializer = stringRedisSerializer
//        // value序列化方式采用jackson
        template.valueSerializer = stringRedisSerializer
//        // hash的value序列化方式采用jackson
//        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        logger.info("Redis_DB_1 注入成功，host:$host ,port:$port ")
        return template
    }
}