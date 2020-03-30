package com.xinlian.wb.core.entity

import org.hibernate.transform.AliasToBeanResultTransformer
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

class Testtrans(private val resultClass: Class<*>) : AliasToBeanResultTransformer(resultClass) {
    override fun transformTuple(tuple: Array<Any>, aliases: Array<String>): List<*> {
        val list = ArrayList<Any?>()
        var obj: Any? = null
        try {
            obj = resultClass.newInstance()
        } catch (e1: InstantiationException) {
            e1.printStackTrace()
        } catch (e1: IllegalAccessException) {
            e1.printStackTrace()
        }
        val methods = resultClass.methods // 返回这个类里面方法的集合
        for (k in aliases.indices) {
            val aliase = aliases[k]
            val ch = aliase.toCharArray()
            ch[0] = Character.toUpperCase(ch[0])
            val s = String(ch)
            val names = arrayOf("set$s".intern(),
                    "get$s".intern(), "is$s".intern(),
                    "read$s".intern())
            var setter: Method? = null
            var getter: Method? = null
            val length = methods.size
            for (i in 0 until length) {
                val method = methods[i]
                /**
                 * 检查该方法是否为公共方法,如果非公共方法就继续
                 */
                if (!Modifier.isPublic(method.modifiers)) continue
                val methodName = method.name
                for (name in names) {
                    if (name == methodName) {
                        if (name.startsWith("set") || name.startsWith("read")) setter = method else if (name.startsWith("get") || name.startsWith("is")) getter = method
                    }
                }
            }
            if (getter != null) {
                val param = buildParam(getter.returnType.name, tuple[k])
                try {
                    setter!!.invoke(obj, *param)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        list.add(obj)
        return list
    }

    companion object {
        private const val serialVersionUID = 1L
        private fun buildParam(paramType: String, value: Any): Array<Any?> {
            val param = arrayOfNulls<Any>(1)
            if (paramType.equals("java.lang.String", ignoreCase = true)) {
                param[0] = value as String
            } else if (paramType.equals("int", ignoreCase = true)
                    || paramType.equals("java.lang.Integer", ignoreCase = true)) {
                param[0] = value.toString().toInt()
            } else if (paramType.equals("long", ignoreCase = true) || paramType.equals("java.lang.Long", ignoreCase = true)) {
                param[0] = value.toString().toLong()
            } else if (paramType.equals("double", ignoreCase = true) || paramType.equals("java.lang.Double", ignoreCase = true)) {
                param[0] = value.toString().toDouble()
            } else if (paramType.equals("float", ignoreCase = true) || paramType.equals("java.lang.Float", ignoreCase = true)) {
                param[0] = value.toString().toFloat()
            } else if (paramType.equals("char", ignoreCase = true)
                    || paramType.equals("Character", ignoreCase = true)) {
                param[0] = value.toString()
            }
            return param
        }
    }

}