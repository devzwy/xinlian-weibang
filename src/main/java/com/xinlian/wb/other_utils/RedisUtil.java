package com.xinlian.wb.other_utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xinlian.wb.redis.LogBean;
import com.xinlian.wb.redis.SpringContextUtil;
import com.xinlian.wb.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisUtil {

//    @Autowired
//    private static RedisTemplate<String, Object> redisTemplate;

    private static final RedisTemplate<String, Object> redisTemplate = SpringContextUtil.Companion.getBean("redisTemplate", RedisTemplate.class);
    private static final RedisTemplate<String, Object> serviceRedisTemplate = SpringContextUtil.Companion.getBean("serviceRedisTemplate", RedisTemplate.class);


    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    /**********************************************************************************
     * redis-公共操作
     **********************************************************************************/

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key, long time) {

        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("【redis：指定缓存失效时间-异常】", e);
            return false;
        }
    }

    public static boolean insertLog(LogBean logBean) {
        Object json_list = get(Constant.RedisKey.LOGTAG);
        if (json_list != null && json_list.toString().length() > 1) {
            ArrayList<LogBean> list = new Gson().fromJson(json_list.toString(), new TypeToken<List<LogBean>>() {
            }.getType());
            list.add(logBean);
            set(Constant.RedisKey.LOGTAG, new Gson().toJson(list));
        } else {
            ArrayList<LogBean> arrayList = new ArrayList<>();
            arrayList.add(logBean);
            set(Constant.RedisKey.LOGTAG, new Gson().toJson(arrayList));
        }
        return true;
    }

    public static List<LogBean> getLogs() {
        String str = "";
        try {
            str = get(Constant.RedisKey.LOGTAG).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(str, new TypeToken<List<LogBean>>() {
        }.getType());
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效;如果该key已经过期,将返回"-2";
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("【redis：判断{}是否存在-异常】", key, e);
            return false;
        }
    }


    /**********************************************************************************
     * redis-String类型的操作
     **********************************************************************************/

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("【redis：普通缓存放入-异常】", e);
            return false;
        }
    }


    /**
     * 写入服务端的token
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean insertServiceToken(String key, Object value) {
        try {
            serviceRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("【redis：普通缓存放入-异常】", e);
            return false;
        }
    }
//
//    public static boolean addLog(String log){
//        Object logStr = get(Constant.RedisKey.LOGTAG);
//        if (logStr!=null){
//            new Gson().fromJson(logStr.toString(),)
//        }
//    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("【redis：普通缓存放入并设置时间-异常】", e);
            return false;
        }
    }


    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void delServiceToken(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                serviceRedisTemplate.delete(key[0]);
            } else {
                serviceRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 删除Api缓存
     */
    @SuppressWarnings("unchecked")
    public static void delApiCache() {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("apiCache" + "*")));
    }

    /**
     * 获取缓存
     *
     * @param key   redis的key
     * @param clazz value的class类型
     * @param <T>
     * @return value的实际对象
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object obj = key == null ? null : redisTemplate.opsForValue().get(key);
        if (!obj.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("类转化异常");
        }
        return (T) obj;
    }

    /**
     * 获取泛型
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取泛型
     *
     * @param key 键
     * @return 值
     */
    public static Object getServiceToken(String key) {
        return key == null ? null : serviceRedisTemplate.opsForValue().get(key);
    }

    /**
     * 检测是否有发送成功的验证码
     * @param phoneNumber
     * @return
     */
    public static Boolean checkCode(String phoneNumber){
        Object aa = null;
        try {
            aa = redisTemplate.opsForValue().get(Objects.requireNonNull(redisTemplate.keys(Constant.RedisKey.MSG_CODE + phoneNumber + "*").iterator().next()));
        } catch (Exception e) {
        }
        return aa!=null;
    }


    /**
     * 获取验证码
     *
     * @return 值
     */
    public static Object getMsgCode(String phoneNumber) {
        Object a = null;
        try {
            a = redisTemplate.opsForValue().get(Objects.requireNonNull(redisTemplate.keys(Constant.RedisKey.MSG_CODE + phoneNumber+"*").iterator().next()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber == null ? null : a;
    }

    /**
     * 删除所有过期验证码
     *
     * @return 值
     */
    public static void deleteAllExpMsgKey() {
        List<String> keys = new ArrayList<>();
        Set<String> aaa = redisTemplate.keys(Constant.RedisKey.MSG_CODE + "*");
        if (aaa != null && aaa.size() > 0) {
            for (String s : aaa) {
                try {
                    if (redisTemplate.opsForValue().get(s) != null)
                        keys.add(s);

                } catch (Exception ignored) {
                }
            }
        }
        for (int i = 0; i < keys.size(); i++) {
            String[] a = keys.get(i).split("_");
            if (new Date().getTime() - Long.valueOf(a[a.length - 1]) >= Constant.finalParams.MSG_CODE_EXPIRETIME * 1000) {
                redisTemplate.delete(keys.get(i));
                logger.info("删除过期的验证码");
            }
        }
    }




}