package com.xinlian.wb.controller;


import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.IUserService;
import com.xinlian.wb.enc_utils.RSA;
import com.xinlian.wb.jdbc.tabs.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 用户相关接口
 *
 * @author Jason
 * @action /wb/user
 * @date 2019/10/9 上午10:01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;


    private Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 加解密测试接口
     *
     * @param testStr|测试的字符串|String|是
     * @param doType|操作类型             0 - 加密  1-解密|Int|是
     * @title 加解密测试接口
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType get
     * @author Jason
     * @date 2019/12/09 下午3:11
     */
    @GetMapping("/RSATest")
    public HttpResponse<Object> RSATest(@RequestParam String testStr, @RequestParam Integer doType) {
        if (testStr == null || testStr.isEmpty()) return new HttpResponse(-1, "数据源为空", null);
        if (doType == null) return new HttpResponse(-1, "操作类型不能为空", null);

        if (doType == 0) {
            //加密
            try {
                return new HttpResponse(RSA.encrypt(testStr, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAotNeP1Kly5GpSppIARDBsh5L63N2zHJc349cWxUYD0+dj+mhdIP/J2WXoqpzFP+ysqJLJxIrJkE73R7R5x7voDdL+VYmgTOtLgbpV4ZuS+7/0yo8kiEXeIM6npUHM7/xlPqdLF5iWOzsc+5YvTSoXqhLSDYSfmqX1q3lDQT86i+8g8D/rX6GBQfct6wfH/i0LBhJe5ylvKhKcGjvH6ErMPrjE6XUAg0s9W/yWGfK/kRWGtWxkoO+ZFpYhP2YOrXMb/Zey1pIfs8/lQdXba9abTiYtb1JsDVWdVBMAAJhUytCbRDEUCd2Yb26gyoaPWdnJdERPTkY3sDFTHxV9M1sCQIDAQAB"));
            } catch (Exception e) {
                return new HttpResponse(-1, "加密失败", null);
            }
        } else if (doType == 1) {
            //解密
            try {
                return new HttpResponse(RSA.decrypt(testStr, "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCi014/UqXLkalKmkgBEMGyHkvrc3bMclzfj1xbFRgPT52P6aF0g/8nZZeiqnMU/7KyoksnEismQTvdHtHnHu+gN0v5ViaBM60uBulXhm5L7v/TKjySIRd4gzqelQczv/GU+p0sXmJY7Oxz7li9NKheqEtINhJ+apfWreUNBPzqL7yDwP+tfoYFB9y3rB8f+LQsGEl7nKW8qEpwaO8foSsw+uMTpdQCDSz1b/JYZ8r+RFYa1bGSg75kWliE/Zg6tcxv9l7LWkh+zz+VB1dtr1ptOJi1vUmwNVZ1UEwAAmFTK0JtEMRQJ3ZhvbqDKho9Z2cl0RE9ORjewMVMfFX0zWwJAgMBAAECggEAeYTELwAwimgQb4QLPvhRhuyDfppyzAyB8bsdk0B/71Vc4z3a6IlmkPGKJLWPI9nddOIYsnUlzOwckF9jClmVPv5l7hT0sbJuig5Qcaj7giJBvUglYm3eBWvzAM16TY3v717GlIlwXquboL3+bl7xVYvGe4MXdE97OJYZfwj89I0vSPruZb3QATJS9c178bWgQPSY8IabBsXU0Hf4EHjrJL+y6yJ+YPi07oQDOqAIwK3Mufo7AqjREbjR43DMY3LU7Ow05xB85uH1P/ze1z0GtGrExjqab0aRT3jzAjRqqBg6DjOMUgECCZG0uBWk7iE4xrxO55atX8690Boq6VmyMQKBgQDkB8JxYvKFYiurYxnxYsLQ41YzoC9bOSIjrV1n5YUNQneW8WH7v9DffIemMpzVBxVcp8t89+4S6xErEYtRZCSCww44BvvxQXAK3W8RoCvi8r6LyjhKnY0OzGqkk0EuXuN5KpP6HYF2ulkyDOD3wZ19FSKX0VpIg4FvvAQhIyEOVQKBgQC2zCg4wPMCROXP3LnSgRmtQX/W2CskFBvTLxIUEk+ES3F8469pQkmWRqlNaojlM9u7FmSjdzgtJgDJxfugzdSxyT+Ms/U4q9djZOxXjlGnsmj14UT1zzDtEHX37ZkGCzoT2jZuhYGQiHoRxuyI5fmqIHuHSWlzwl9KLmPJ1rMy5QKBgQCrmstR2Uz55C9JA4N6jQBfgzZUE7CPzidLAiTRE4FVwTeOeIlsk6X1ChprkJtGFdaVrBEPMuYPhqec6c8WqW5wmaoRr+/aV4yiIJJ9iTR9zoBnYv+J55dIE74NrGPZKb+2Z7yE9b+AQizt5ZNH4IVMpKMr7XksKQs3sx7IcU9nIQKBgD3BmXEbFr5UgoOIIatRfFhBQaxW2bRVqtTdGTF4wi6CwnOcBH3+LBg+BSKndFpi+8AoH5XuSCdQqIGChrFb+Jib0gF6JsWfoKPuy74E0edi6fzvvzmAZxogLoq1VbZqApQEa9FI/23R/dOVrgHOGFv2n2UkUyENsN3B8GqXQ3FVAoGAQ1VvyyXOWeHfqd7AdzGoiWk1LVCS/F18+85IrUL0nuDPczqWEwlhUrn1+g4P2k8JpEomLJlArNGgLj4x9khFChtisaAB5jr1hUS62CXJZeNF+EKXxbncY9zrC//YfxkDvShrmq2+QcEj7hy46Y0dFEe+FeGwj3mZPOLBNVwxZOY="));
            } catch (Exception e) {
                return new HttpResponse(-1, "解密失败", null);
            }
        } else {
            return new HttpResponse(-1, "未知的操作类型."+new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()), null);
        }
    }

    /**
     * 用户身份认证
     *
     * @param token|⚠️请求头携带             由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param cardNumber|身份证号码|String|是
     * @param userRelName|真实姓名|String|是
     * @title 实名认证
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/authCard")
    public HttpResponse<Object> authCard(@RequestBody RequestBean_AuthCard mRequestBean_AuthCard, @RequestHeader String token) {
        return userService.authCard(mRequestBean_AuthCard, token);
    }


    /**
     * 获取技能标签
     *
     * @title 获取技能标签(新)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|ResponseBean_Tag|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "tagVersion": 1,
     * "tagList": [
     * {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2,
     * "subSkills": [
     * {
     * "subTagId": 29,
     * "subTitle": "跑腿代办",
     * "suggestPrice": 67.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/29.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 30,
     * "subTitle": "宠物",
     * "suggestPrice": 44.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/30.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 32,
     * "subTitle": "推拿",
     * "suggestPrice": 154.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/32.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 33,
     * "subTitle": "家装设计",
     * "suggestPrice": 117.0,
     * "tagUnit": "平米",
     * "tagImgUrl": "/Images/Skills/33.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 35,
     * "subTitle": "母婴咨询",
     * "suggestPrice": 27.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/35.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 80,
     * "subTitle": "购车指导",
     * "suggestPrice": 65.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/80.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 81,
     * "subTitle": "汽车陪练",
     * "suggestPrice": 72.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/81.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 82,
     * "subTitle": "物品收纳",
     * "suggestPrice": 376.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/82.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 83,
     * "subTitle": "兴趣",
     * "suggestPrice": 206.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/83.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 84,
     * "subTitle": "传单派发",
     * "suggestPrice": 21.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/84.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 109,
     * "subTitle": "代驾",
     * "suggestPrice": 300.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/109.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 110,
     * "subTitle": "二手物品",
     * "suggestPrice": 80.0,
     * "tagUnit": "件",
     * "tagImgUrl": "/Images/Skills/110.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 5,
     * "parentTagName": "家政维修",
     * "orderByNumber": 1,
     * "subSkills": [
     * {
     * "subTagId": 44,
     * "subTitle": "手机维修",
     * "suggestPrice": 35.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/44.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 67,
     * "subTitle": "保洁",
     * "suggestPrice": 30.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/67.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 68,
     * "subTitle": "保姆",
     * "suggestPrice": 4000.0,
     * "tagUnit": "月",
     * "tagImgUrl": "/Images/Skills/68.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 69,
     * "subTitle": "搬家服务",
     * "suggestPrice": 200.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/69.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 70,
     * "subTitle": "管道疏通",
     * "suggestPrice": 98.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/70.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 71,
     * "subTitle": "家电维修",
     * "suggestPrice": 102.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/71.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 72,
     * "subTitle": "电脑维修",
     * "suggestPrice": 51.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/72.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 73,
     * "subTitle": "数码维修",
     * "suggestPrice": 35.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/73.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 74,
     * "subTitle": "房屋维修",
     * "suggestPrice": 248.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/74.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 75,
     * "subTitle": "家具维修",
     * "suggestPrice": 76.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/75.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 76,
     * "subTitle": "开锁换锁",
     * "suggestPrice": 150.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/76.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 77,
     * "subTitle": "锁具购买",
     * "suggestPrice": 260.0,
     * "tagUnit": "个",
     * "tagImgUrl": "/Images/Skills/77.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 111,
     * "subTitle": "机顶盒维修",
     * "suggestPrice": 200.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/111.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 118,
     * "subTitle": "代扔垃圾",
     * "suggestPrice": 50.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/118.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 8,
     * "parentTagName": "咨询服务",
     * "orderByNumber": 9,
     * "subSkills": [
     * {
     * "subTagId": 51,
     * "subTitle": "财税顾问",
     * "suggestPrice": 171.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/51.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 52,
     * "subTitle": "工商注册",
     * "suggestPrice": 115.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/52.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 53,
     * "subTitle": "社保咨询",
     * "suggestPrice": 93.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/53.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 54,
     * "subTitle": "情感咨询",
     * "suggestPrice": 154.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/54.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 55,
     * "subTitle": "心理咨询",
     * "suggestPrice": 185.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/55.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 56,
     * "subTitle": "市场营销",
     * "suggestPrice": 754.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/56.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 57,
     * "subTitle": "兼职猎头",
     * "suggestPrice": 95.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/57.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 58,
     * "subTitle": "占卜",
     * "suggestPrice": 320.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/58.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 59,
     * "subTitle": "法律",
     * "suggestPrice": 175.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/59.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 60,
     * "subTitle": "翻译",
     * "suggestPrice": 120.0,
     * "tagUnit": "千字",
     * "tagImgUrl": "/Images/Skills/60.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 85,
     * "subTitle": "旅游咨询",
     * "suggestPrice": 60.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/85.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 86,
     * "subTitle": "保险咨询",
     * "suggestPrice": 53.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/86.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 9,
     * "parentTagName": "技术服务",
     * "orderByNumber": 10,
     * "subSkills": [
     * {
     * "subTagId": 45,
     * "subTitle": "文字处理",
     * "suggestPrice": 45.0,
     * "tagUnit": "页",
     * "tagImgUrl": "/Images/Skills/45.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 46,
     * "subTitle": "程序编写",
     * "suggestPrice": 343.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/46.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 47,
     * "subTitle": "平面设计",
     * "suggestPrice": 349.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/47.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 48,
     * "subTitle": "UI设计",
     * "suggestPrice": 698.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/48.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 87,
     * "subTitle": "VI设计",
     * "suggestPrice": 120.0,
     * "tagUnit": "个",
     * "tagImgUrl": "/Images/Skills/87.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 88,
     * "subTitle": "图片处理",
     * "suggestPrice": 63.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/88.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 10,
     * "parentTagName": "校园服务",
     * "orderByNumber": 4,
     * "subSkills": [
     * {
     * "subTagId": 61,
     * "subTitle": "叫起床",
     * "suggestPrice": 15.0,
     * "tagUnit": "周",
     * "tagImgUrl": "/Images/Skills/61.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 62,
     * "subTitle": "代点名",
     * "suggestPrice": 10.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/62.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 63,
     * "subTitle": "帮带饭",
     * "suggestPrice": 10.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/63.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 64,
     * "subTitle": "代取快递",
     * "suggestPrice": 5.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/64.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 65,
     * "subTitle": "上自习",
     * "suggestPrice": 20.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/65.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 66,
     * "subTitle": "代洗衣服",
     * "suggestPrice": 30.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/66.png",
     * "orderByNumber": 0,
     * "hotTag": 1
     * },
     * {
     * "subTagId": 89,
     * "subTitle": "校园兼职",
     * "suggestPrice": 150.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/89.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 90,
     * "subTitle": "生活技能",
     * "suggestPrice": 52.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/90.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 91,
     * "subTitle": "兴趣特长",
     * "suggestPrice": 200.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/91.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 92,
     * "subTitle": "学习辅导",
     * "suggestPrice": 88.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/92.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 11,
     * "parentTagName": "车辆服务",
     * "orderByNumber": 8,
     * "subSkills": [
     * {
     * "subTagId": 96,
     * "subTitle": "车辆年检",
     * "suggestPrice": 193.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/96.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 97,
     * "subTitle": "违章处理",
     * "suggestPrice": 77.0,
     * "tagUnit": "分",
     * "tagImgUrl": "/Images/Skills/97.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 98,
     * "subTitle": "车辆上牌",
     * "suggestPrice": 1588.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/98.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 99,
     * "subTitle": "二手车过户代办",
     * "suggestPrice": 1968.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/99.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 100,
     * "subTitle": "车辆维修",
     * "suggestPrice": 276.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/100.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 101,
     * "subTitle": "汽车保养",
     * "suggestPrice": 368.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/101.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 102,
     * "subTitle": "道路救援",
     * "suggestPrice": 200.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/102.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 103,
     * "subTitle": "汽车配件",
     * "suggestPrice": 800.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/103.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 104,
     * "subTitle": "汽车装潢",
     * "suggestPrice": 800.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/104.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 105,
     * "subTitle": "洗车",
     * "suggestPrice": 20.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/105.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 106,
     * "subTitle": "二手车",
     * "suggestPrice": 888.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/106.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 107,
     * "subTitle": "新车购买",
     * "suggestPrice": 65.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/107.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * },
     * {
     * "parentId": 13,
     * "parentTagName": "租赁服务",
     * "orderByNumber": 11,
     * "subSkills": [
     * {
     * "subTagId": 112,
     * "subTitle": "合租房",
     * "suggestPrice": 618.0,
     * "tagUnit": "月",
     * "tagImgUrl": "/Images/Skills/112.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 113,
     * "subTitle": "短租房",
     * "suggestPrice": 129.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/113.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 114,
     * "subTitle": "租车位",
     * "suggestPrice": 147.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/114.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 115,
     * "subTitle": "租工位",
     * "suggestPrice": 70.0,
     * "tagUnit": "天",
     * "tagImgUrl": "/Images/Skills/115.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 116,
     * "subTitle": "时租房",
     * "suggestPrice": 50.0,
     * "tagUnit": "小时",
     * "tagImgUrl": "/Images/Skills/116.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * {
     * "subTagId": 117,
     * "subTitle": "租场地",
     * "suggestPrice": 50.0,
     * "tagUnit": "人",
     * "tagImgUrl": "/Images/Skills/117.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * }
     * ]
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/29 下午2:45
     */
    @PostMapping("/getTags")
    public HttpResponse<Object> getTags() {
        return userService.getTags();
    }

    /**
     * 获取技能标签版本（分开两个接口是因为一个接口数据太大了）
     *
     * @title 获取技能标签版本
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|技能标签版本号 在获取技能标签时先调用该接口查询版本号，比对客户端本地版本后根据需要决定是否更新本地的标签|Int|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": 1
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getTagsVersion")
    public HttpResponse<Object> getTagsVersion() {
        return userService.getTagsVersion();
    }

    /**
     * 修改用户资料
     *
     * @param token|⚠️请求头携带          由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userName|昵称|String|否
     * @param sex|性别                 -1 未知 男 1 女 0|Int|否
     * @param userLogo|用户头像|String|否
     * @param birthday|生日            时间戳 ms|String|否
     * @title 修改用户资料
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/updateUserInfo")
    public HttpResponse<Object> updateUserInfo(@RequestBody RequestBean_UpdateUserInfo mUserInfo, @RequestHeader String token) {
        return userService.updateUserInfo(mUserInfo, token);
    }


    /**
     * 获取用户资料
     *
     * @param token|⚠️请求头携带    由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userId|需要获取的用户ID 获取当前用户时传空字符串|String|否
     * @title 获取用户资料
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|User|登录成功时包含如下参数
     * @resqParam 以下为User实体的参数说明|以下为User实体的参数说明|以下为User实体的参数说明|以下为User实体的参数说明
     * @resqParam userId|用户唯一标识符|String|是
     * @resqParam userType|用户类型 0-android 1-iOS 2-小程序|Int|是
     * @resqParam phoneNumber|用户注册的电话号码|String|是
     * @resqParam userName|用户名 未设置用户名默认使用电话号码作为用户名|String|是
     * @resqParam sex|性别 0-女 1-男 -1表示未设置|Int|是
     * @resqParam birthday|生日 未设置时默认未注册时的时间戳|String|是
     * @resqParam userLogo|用户头像 未设置时由系统生成|String|是
     * @resqParam wyy_accid|网易云信使用的用户Accid|String|是
     * @resqParam wyy_token|网易云信使用的用户token|String|是
     * @resqParam userBussnessType|用户类型 0-普通用户  1-web用户|Int|是
     * @resqParam indentity|实名认证信息实体 返回："idemyityName": "赵文贇",//实名的姓名 "idemyityCardNumber": "62222222222222222222222"//实名的身份证号码|String|否
     * @resqParam ban|用户是否被封禁 封禁时禁止登录|String|是
     * @resqParam wopenIdx|用户绑定的第三方微信token|String|否
     * @resqParam qopenIdq|用户绑定的第三方QQtoken|String|否
     * @resqParam merchanAuthState|用户商户认证状态 -1-未提交审核 0 - 等待审核 1-未通过认证 2-审核成功|Int|是
     * @respBody {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https:\/\/timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "62222222222222222222222"
     * },
     * "ban": false,
     * "wopenIdx": "22222222",
     * "qopenIdq": "111111111",
     * "merchanAuthState":-1
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getUserInfo")
    public HttpResponse<Object> getUserInfo(@RequestBody RequestBean_GetUserInfo mRequestBean_GetUserInfo, @RequestHeader String token) {
        return userService.getUserInfo(mRequestBean_GetUserInfo, token);
    }


    /**
     * 添加或修改用户收货地址 修改时请携带id字段
     *
     * @param token|⚠️请求头携带                         由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param id|修改收货地址时必传|Int|否
     * @param phoneNumber|手机号码|String|是
     * @param address_head|地址前缀|String|是
     * @param address_end|地址后缀|String|是
     * @param name|收货人姓名|String|是
     * @param sex|收货人性别|String|是
     * @param tag|收货地址标签|String|是
     * @param isDefaultAddress|是否设置默认收获地址|Boolean|是
     * @param addressLat|收货地址经纬度|Double|是
     * @param addressLng|收货地址经纬度|Double|是
     * @title 添加收货地址
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/addAddress")
    public HttpResponse<Object> addAddress(@RequestBody Address mAddress, @RequestHeader String token) {
        return userService.addAddress(mAddress, token);
    }


    /**
     * 获取用户收货地址列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取收货地址列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|List<Address>|为空时返回空集合
     * @resqParam 以下为Address实体的参数说明|以下为Address实体的参数说明|以下为Address实体的参数说明|以下为Address实体的参数说明
     * @resqParam id|地址ID|Int|是
     * @resqParam phoneNumber|手机号码|String|是
     * @resqParam address_head|地址前缀|String|是
     * @resqParam address_end|地址后缀|String|是
     * @resqParam name|收货人姓名|String|是
     * @resqParam sex|收货人性别|String|是
     * @resqParam tag|收货地址标签|String|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 387,
     * "phoneNumber": "15317026229",
     * "address_head": "陕西省西安市2222",
     * "address_end": "长安区62222222",
     * "name": "张四",
     * "sex": "女",
     * "tag": "家"
     * },
     * {
     * "id": 411,
     * "phoneNumber": "15317026229",
     * "address_head": "陕西省西安市2222",
     * "address_end": "长安区62222222",
     * "name": "张四",
     * "sex": "女",
     * "tag": "家"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getUserAddressList")
    public HttpResponse<Object> getUserAddressList(@RequestHeader String token) {
        return userService.getUserAddressList(token);
    }

    /**
     * 关注/取消关注接口
     *
     * @param token|⚠️请求头携带                由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userId|关注/取消关注的用户ID|String|是
     * @param doType|操作类型                  0-关注1-取消关注|Int|是
     * @title 关注/取消关注
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/likeOrDisLikeUser")
    public HttpResponse<Object> likeOrDisLikeUser(@RequestHeader String token, @RequestBody RequestBean_LikeOrDisLike mRequestBean_LikeOrDisLike) {
        return userService.likeOrDisLikeUser(mRequestBean_LikeOrDisLike, token);
    }

    /**
     * 获取当前用户关注的列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取关注列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|用户列表 为空时返回空集合 参考登录接口返回的用户实体|List<User>|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https:\/\/timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "62222222222222222222222"
     * },
     * "ban": false,
     * "wopenIdx": "22222222",
     * "qopenIdq": "111111111"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getUserLikedList")
    public HttpResponse<Object> getUserLikedList(@RequestHeader String token) {
        return userService.getUserLikedList(token);
    }


}
