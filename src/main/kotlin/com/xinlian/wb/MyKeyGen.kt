package com.xinlian.wb

import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.*

@Component
class MyKeyGen : KeyGenerator {
    override fun generate(o: Any, method: Method, vararg objects: Any): Any {
        return method.name + ":" + Arrays.toString(objects)
    }
}