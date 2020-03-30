package com.xinlian.wb.controller;


import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.IBusunessService;
import com.xinlian.wb.core.service.service.IOtherService;
import com.xinlian.wb.jdbc.tabs.Demand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.Notification;

/**
 * 其他
 *
 * @author Jason
 * @action /wb/other
 * @date 2019/12/23 下午5:55
 */
@RestController
@RequestMapping("/other")
public class OtherController {
    private Logger logger = LoggerFactory.getLogger(OtherController.class);

    @Autowired
    private IOtherService mIOtherService;


    /**
     * 获取系统通知列表
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param page|请求的页码    默认 1 必须从1开始|Int|否
     * @param number|请求的条数  默认返回全部数据|Int|否
     * @title 获取系统通知列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam userId|通知的对象用户ID|String|是
     * @resqParam createTime|记录创建时间|Long|是
     * @resqParam notificationTitle|通知的标题|String|是
     * @resqParam notificationContent|通知的内容|String|是
     * @resqParam read|是否已读|Bool|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "id": 520,
     *       "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *       "createTime": 1582790962916,
     *       "notificationTitle": "系统发送的测试标题",
     *       "notificationContent": "系统发送的测试内容",
     *       "user": {
     *         "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *         "userType": 0,
     *         "phoneNumber": "15317026229",
     *         "userName": "153****6229",
     *         "sex": -1,
     *         "birthday": 1582166845102,
     *         "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     *         "wyy_accid": "yxaccid_wb_a3dcc5d7dd2f4787926fc",
     *         "wyy_token": "yxToken_wb_v2_15317026229",
     *         "createTime": 1582166845102,
     *         "merchanAuthState": 0,
     *         "ban": false,
     *         "dispatcher": false,
     *         "wopenIdx": "",
     *         "qopenIdq": ""
     *       },
     *       "read": true
     *     },
     *     {
     *       "id": 521,
     *       "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *       "createTime": 1582790967687,
     *       "notificationTitle": "系统发送的测试标题2",
     *       "notificationContent": "系统发送的测试内容2",
     *       "user": {
     *         "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *         "userType": 0,
     *         "phoneNumber": "15317026229",
     *         "userName": "153****6229",
     *         "sex": -1,
     *         "birthday": 1582166845102,
     *         "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     *         "wyy_accid": "yxaccid_wb_a3dcc5d7dd2f4787926fc",
     *         "wyy_token": "yxToken_wb_v2_15317026229",
     *         "createTime": 1582166845102,
     *         "merchanAuthState": 0,
     *         "ban": false,
     *         "dispatcher": false,
     *         "wopenIdx": "",
     *         "qopenIdq": ""
     *       },
     *       "read": true
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2020/2/27 上午10:11
     */
    @PostMapping("/getSystemNotifications")
    public HttpResponse<Object> getSystemNotifications(@RequestBody  Req_GetSystemNotification mReq_GetSystemNotification,@RequestHeader String token) {
        return mIOtherService.getSystemNotifications(mReq_GetSystemNotification,token);
    }


    /**
     * 获取全部派送员和商户列表
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param lat|当前位置的经纬度|Double|是
     * @param lng|当前位置的经纬度|Double|是
     * @title 获取全部派送员和商户列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @resqParam lat|地图中标点的经纬度信息|Double|是
     * @resqParam lng|地图中标点的经纬度信息|Double|是
     * @resqParam dispatcher|是否为派送员|Bool|是
     * @resqParam merchanAuthState|商户认证状态 -1-未提交审核 0 - 等待审核 1-未通过认证 2-审核成功 |Int|是
     * @resqParam merchanAuthBean|商户实体 当merchanAuthState为2时此处才有值|merchanAuthBean|否
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "user": {
     *         "userId": "b93f14aa-8e9c-4515-91f5-8ab3fb9faa2e",
     *         "userType": 0,
     *         "phoneNumber": "18020011122",
     *         "userName": "180****1122",
     *         "sex": -1,
     *         "birthday": 1582012493719,
     *         "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     *         "wyy_accid": "yxaccid_wb_1ef4bb5da5ab4a6da3964",
     *         "wyy_token": "yxToken_wb_v2_18020011122",
     *         "createTime": 1582012493719,
     *         "merchanAuthState": 2,
     *         "merchanAuthBean": {
     *           "merchanId": 87,
     *           "userId": "b93f14aa-8e9c-4515-91f5-8ab3fb9faa2e",
     *           "verifyDec": "",
     *           "verifyState": 2,
     *           "createTime": 1582173614277,
     *           "verifyTime": 1582173695731,
     *           "verifyUserId": "admin",
     *           "merchanTitle": "TestTitle",
     *           "merchanUserName": "Jason",
     *           "merchanPhoneNumber": "1888888888",
     *           "authId": 85,
     *           "merchanPhotos": "aaaaa|bbbb",
     *           "merchanBaseTagId": 5,
     *           "serviceTime": "lll",
     *           "city_merchan": "TestCity222222",
     *           "addressDetail_Merchan": "aaaaaa",
     *           "addressInfo": "testInfo",
     *           "serverBusinessLicenseType": 0,
     *           "serverBusinessLicenseName": "testName",
     *           "serverBusinessLicenseNumber": "111111",
     *           "serverBusinessLicenseEndTime": 10000111,
     *           "serverBusinessLicensePhotos": "aaa",
     *           "serverPermitLicenseNmae": "name",
     *           "serverPermitLicenseRange": "aaaaa",
     *           "serverPermitLicenseEndTime": 111,
     *           "serverPermitLicensePhotos": "ccccc"
     *         },
     *         "wopenIdx": "",
     *         "qopenIdq": "",
     *         "ban": false,
     *         "dispatcher": true
     *       },
     *       "lat": 32.223255,
     *       "lng": 132.002333
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2020/2/20 上午10:11
     */
    @PostMapping("/getAllDispatcherAndMerchants")
    public HttpResponse<Object> getAllDispatcherAndMerchants(@RequestBody ReqtgetAllDispatcherAndMerchants mReqtgetAllDispatcherAndMerchants,@RequestHeader String token) {
        return mIOtherService.getAllDispatcherAndMerchants(mReqtgetAllDispatcherAndMerchants,token);
    }

    /**
     * 根据订单ID或需求ID获取周围服务者 需求返回该需求附近30公里内的stag对应的服务者列表  跑腿代办和同城配送返回附近30公里内的派送员列表  注意 如果服务者或派送员未更新经纬度时不会返回
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单ID|String|否
     * @param demandId|需求ID|Long|否
     * @title 查询附近的服务者或派送员
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @resqParam merchanAuthState|当前用户的商户认证状态，参考登陆返回字段解释|Int|是
     * @resqParam lat|地图中标点的经纬度信息|Double|是
     * @resqParam lng|地图中标点的经纬度信息|Double|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "user": {
     *         "userId": "100f94d6-b937-4567-947c-4e149942fede",
     *         "userType": 0,
     *         "phoneNumber": "18020011121",
     *         "userName": "180****1121",
     *         "sex": -1,
     *         "birthday": 1581863667616,
     *         "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     *         "wyy_accid": "yxaccid_wb_29853148e2e74f29a45a0",
     *         "wyy_token": "yxToken_wb_v2_18020011121",
     *         "createTime": 1581863667616,
     *         "token": "c39ffb58-9ae5-46b8-a45c-8a46cde5e544|bdd0d0621190874f8cc9c16bf2bb652c",
     *         "merchanAuthState": -1,
     *         "dispatcher": true,
     *         "ban": false,
     *         "wopenIdx": "",
     *         "qopenIdq": ""
     *       },
     *       "lat": 100.0,
     *       "lng": 100.0
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2020/2/16 上午10:11
     */
    @PostMapping("/getNearUserList")
    public HttpResponse<Object> getNearUserList(@RequestBody RequestBean_getNearUserBean mgetNearUserBean,@RequestHeader String token) {
        return mIOtherService.getNearUserList(mgetNearUserBean,token);
    }
    /**
     * 派送员认证
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 派送员认证
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "认证成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/16 上午10:11
     */
    @PostMapping("/dispatcherCertification")
    public HttpResponse<Object> dispatcherCertification(@RequestHeader String token) {
        return mIOtherService.dispatcherCertification(token);
    }

    /**
     * 重置支付密码
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param password|旧密码(密文)|String|是
     * @param newPassword|新密码(密文)|String|是
     * @title 重置支付密码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "重置密码成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/12 上午10:11
     */
    @PostMapping("/reSetPassword")
    public HttpResponse<Object> reSetPassword(@RequestBody RequestBean_SetPsw mRequestBean_SetPsw, @RequestHeader String token) {
        return mIOtherService.reSetPassword(mRequestBean_SetPsw, token);
    }


    /**
     * 设置支付密码
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param password|密码(密文)|String|是
     * @title 设置支付密码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "设置成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/12 上午10:11
     */
    @PostMapping("/setPassword")
    public HttpResponse<Object> setPassword(@RequestBody RequestBean_SetPsw mRequestBean_SetPsw, @RequestHeader String token) {
        return mIOtherService.setPassword(mRequestBean_SetPsw, token);
    }

    /**
     * 获取用户余额流水记录
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param bType|筛选字段    0-全部流水 1-收入流水 2-支出流水|int|是
     * @param page|请求的页码    默认 1 必须从1开始|Int|否
     * @param number|请求的条数  默认返回全部数据|Int|否
     * @title 获取用户余额流水记录
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": []
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/12 上午10:11
     */
    @PostMapping("/getBalanceRecord")
    public HttpResponse<Object> getBalanceRecord(@RequestBody RequestBean_GetBalanceRe mRequestBean_GetBalanceRe, @RequestHeader String token) {
        return mIOtherService.getBalanceRecord(mRequestBean_GetBalanceRe, token);
    }


    /**
     * 获取全部需求列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param lat|经纬度       没传入经纬度时将无排序|Double|否
     * @param lng|经纬度       没传入经纬度时将无排序|Double|否
     * @param page|请求的页数    从1开始 不传page 和number时 page默认为1 number 默认为最大值|Int|否
     * @param number|请求的条数  从1开始 不传page 和number时 page默认为1 number 默认为最大值|Int|否
     * @title 获取全部需求列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 5514,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 229,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "registrationSkills": ",835",
     * "lat": 100.0,
     * "lng": 111.0,
     * "registrationSkillList": [
     * {
     * "id": 835,
     * "user": {
     * "userId": "5d4af9f9-3fd4-4650-8559-09d2c44b3f04",
     * "userType": 0,
     * "phoneNumber": "18000000008",
     * "userName": "180****0008",
     * "sex": -1,
     * "birthday": 1578990211353,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_142a3ed0f6b5412d96647",
     * "wyy_token": "yxToken_wb_v2_18000000008",
     * "createTime": 1578990211353,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "serviceDec": "用户1发布的技能备注",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "lat": 111.1111111,
     * "lng": 111.2222222,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "distance": 0,
     * "title": "用户1发布的测试技能",
     * "autoRegistration": true
     * }
     * ],
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579075880242,
     * "demandState": 2,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * },
     * {
     * "id": 5523,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 188,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "lat": 100.0,
     * "lng": 111.0,
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579077087115,
     * "demandState": 0,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * },
     * {
     * "id": 5524,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 876,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "registrationSkills": "835",
     * "lat": 100.0,
     * "lng": 111.0,
     * "registrationSkillList": [
     * {
     * "id": 835,
     * "user": {
     * "userId": "5d4af9f9-3fd4-4650-8559-09d2c44b3f04",
     * "userType": 0,
     * "phoneNumber": "18000000008",
     * "userName": "180****0008",
     * "sex": -1,
     * "birthday": 1578990211353,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_142a3ed0f6b5412d96647",
     * "wyy_token": "yxToken_wb_v2_18000000008",
     * "createTime": 1578990211353,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "serviceDec": "用户1发布的技能备注",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "lat": 111.1111111,
     * "lng": 111.2222222,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "distance": 0,
     * "title": "用户1发布的测试技能",
     * "autoRegistration": true
     * }
     * ],
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579077226528,
     * "demandState": 0,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/26 上午10:11
     */
    @PostMapping("/getAllRequirements")
    public HttpResponse<Object> getAllRequirements(@RequestBody RequestBean_GetDemands mDemand, @RequestHeader String token) {
        return mIOtherService.getAllRequirements(mDemand, token);
    }

    /**
     * 获取我发布的需求列表 不传page 和number时 page默认为1 number 默认为最大值
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param page|请求的页数    从1开始 不传page 和number时 page默认为1 number 默认为最大值|Int|否
     * @param number|请求的条数  从1开始 不传page 和number时 page默认为1 number 默认为最大值|Int|否
     * @title 获取我发布的需求列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 5514,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 229,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "registrationSkills": ",835",
     * "lat": 100.0,
     * "lng": 111.0,
     * "registrationSkillList": [
     * {
     * "id": 835,
     * "user": {
     * "userId": "5d4af9f9-3fd4-4650-8559-09d2c44b3f04",
     * "userType": 0,
     * "phoneNumber": "18000000008",
     * "userName": "180****0008",
     * "sex": -1,
     * "birthday": 1578990211353,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_142a3ed0f6b5412d96647",
     * "wyy_token": "yxToken_wb_v2_18000000008",
     * "createTime": 1578990211353,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "serviceDec": "用户1发布的技能备注",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "lat": 111.1111111,
     * "lng": 111.2222222,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "title": "用户1发布的测试技能",
     * "autoRegistration": true
     * }
     * ],
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579075880242,
     * "demandState": 2,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * },
     * {
     * "id": 5523,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 188,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "lat": 100.0,
     * "lng": 111.0,
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579077087115,
     * "demandState": 0,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * },
     * {
     * "id": 5524,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 876,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "registrationSkills": "835",
     * "lat": 100.0,
     * "lng": 111.0,
     * "registrationSkillList": [
     * {
     * "id": 835,
     * "user": {
     * "userId": "5d4af9f9-3fd4-4650-8559-09d2c44b3f04",
     * "userType": 0,
     * "phoneNumber": "18000000008",
     * "userName": "180****0008",
     * "sex": -1,
     * "birthday": 1578990211353,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_142a3ed0f6b5412d96647",
     * "wyy_token": "yxToken_wb_v2_18000000008",
     * "createTime": 1578990211353,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "serviceDec": "用户1发布的技能备注",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "lat": 111.1111111,
     * "lng": 111.2222222,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "title": "用户1发布的测试技能",
     * "autoRegistration": true
     * }
     * ],
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579077226528,
     * "demandState": 0,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/26 上午10:11
     */
    @PostMapping("/getMyRequirements")
    public HttpResponse<Object> getMyRequirements(@RequestBody RequestBean_GetDemands mRequestBean_GetDemands, @RequestHeader String token) {
        return mIOtherService.getMyRequirements(mRequestBean_GetDemands, token);
    }

    /**
     * 获取需求详情
     *
     * @param token|⚠️请求头携带  由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param id|需求ID|Long|否
     * @title 获取需求详情
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @resqParam id|需求ID|Long|是
     * @resqParam userId|发布该需求的用户ID|String|是
     * @resqParam p_tag|大类标签ID|Long|是
     * @resqParam s_tag|小类标签ID|Long|是
     * @resqParam serviceTime|服务时间戳|Long|是
     * @resqParam expiryDate|需求有效期 0 -一天  1 - 7天  2 - 长期有效|Int|是
     * @resqParam genderRequirements|性别要求|int|是
     * @resqParam serviceMode|服务类型|Int|是
     * @resqParam demandDescribe|需求描述|String|是
     * @resqParam registrationUserList|报名的用户列表|List<User>|否
     * @resqParam user|发布需求的用户实体|User|是
     * @resqParam ptagBean|大类标签实体|Any|是
     * @resqParam stagBean|小类标签实体|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "id": 5524,
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 876,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "registrationSkills": "835",
     * "lat": 100.0,
     * "lng": 111.0,
     * "registrationSkillList": [
     * {
     * "id": 835,
     * "user": {
     * "userId": "5d4af9f9-3fd4-4650-8559-09d2c44b3f04",
     * "userType": 0,
     * "phoneNumber": "18000000008",
     * "userName": "180****0008",
     * "sex": -1,
     * "birthday": 1578990211353,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_142a3ed0f6b5412d96647",
     * "wyy_token": "yxToken_wb_v2_18000000008",
     * "createTime": 1578990211353,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "serviceDec": "用户1发布的技能备注",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "lat": 111.1111111,
     * "lng": 111.2222222,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "distance": 0,
     * "title": "用户1发布的测试技能",
     * "autoRegistration": true
     * }
     * ],
     * "user": {
     * "userId": "6a39bb14-4714-4ea5-9225-f17174f8f495",
     * "userType": 0,
     * "phoneNumber": "18000002222",
     * "userName": "180****2222",
     * "sex": -1,
     * "birthday": 1579075814698,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_1649e02d5c4749b893862",
     * "wyy_token": "yxToken_wb_v2_18000002222",
     * "createTime": 1579075814698,
     * "merchanAuthState": 0,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "createTime": 1579077226528,
     * "demandState": 0,
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * }
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/26 上午10:11
     */
    @PostMapping("/requirementsDetails")
    public HttpResponse<Object> requirementsDetails(@RequestBody Demand mDemand, @RequestHeader String token) {
        return mIOtherService.requirementsDetails(mDemand, token);
    }

    /**
     * 需求报名
     *
     * @param token|⚠️请求头携带            由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param demandId|需求ID|Long|是
     * @param skillId|报名需求的技能ID|Long|是
     * @title 需求报名
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "报名成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/26 上午10:11
     */
    @PostMapping("/signUpRequirements")
    public HttpResponse<Object> signUpRequirements(@RequestBody SignDemandBean mSignDemandBean, @RequestHeader String token) {
        return mIOtherService.signUpRequirements(mSignDemandBean, token);
    }

    /**
     * 发布/修改需求
     *
     * @param token|⚠️请求头携带                由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param id|需求ID                      修改需求时必填|Long|否
     * @param p_tag|大类标签ID|Long|是
     * @param s_tag|小类标签ID|Long|是
     * @param lat|经纬度|Double|是
     * @param lat|经纬度|Double|是
     * @param serviceTime|服务时间的时间戳|Long|是
     * @param expiryDate|需求有效期             0 -一天  1 - 7天  2 - 长期有效|Int|是
     * @param genderRequirements|性别要求      0-女 1-男 -1-不限|Int|是
     * @param serviceMode|需求的服务方式          0 TA来找我 1 我去找TA 2 线上服务  3 邮寄|Int|是
     * @param demandDescribe|需求描述|String|是
     * @title 发布/修改需求
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "需求发布成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/26 上午10:11
     */
    @PostMapping("/releaseRequirements")
    public HttpResponse<Object> releaseRequirements(@RequestBody Demand mDemand, @RequestHeader String token) {
        return mIOtherService.releaseRequirements(mDemand, token);
    }

    /**
     * 余额充值
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param price|充值的金额|Float|是
     * @param doType|支付类型         0 - 支付宝  1 - 微信|Int|是
     * @title 余额充值
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
     * @date 2019/12/23 上午10:11
     */
    @PostMapping("/addBalance")
    public HttpResponse<Object> addBalance(@RequestBody AddBalanceBean addBalanceBean, @RequestHeader String token) {
        return mIOtherService.addBalance(addBalanceBean, token);//g
    }
}
