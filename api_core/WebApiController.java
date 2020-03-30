package com.xinlian.wb.controller;


import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.IWebService;
import com.xinlian.wb.core.service.service.IWebService2;
import com.xinlian.wb.jdbc.tabs.SystemNotification;
import com.xinlian.wb.jdbc.tabs_web.ShowWebBoradTab;
import com.xinlian.wb.jdbc.tabs_web.User_Web;
import com.xinlian.wb.jdbc.tabs_web.WebTabTitleBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;


/**
 * Web端接口(.web)
 *
 * @author Jason
 * @action /wb/web
 * @date 2019/10/9 上午10:01
 */
@RestController
@RequestMapping("/web")
public class WebApiController {

    @Autowired
    private IWebService webService;

    @Autowired
    private IWebService2 webService2;


    private Logger logger = LoggerFactory.getLogger(WebApiController.class);


    /**
     * 禁用/启用标签
     *
     * @param token|⚠️请求头携带         由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param doType|操作类型           0 - 启用 1 - 禁用|Int|是
     * @param subTagId|副标签ID|Long|是
     * @title 禁用/启用标签
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/3/14 上午10:11
     */
    @PostMapping("/controllerSubTag.web")
    public HttpResponse<Object> controllerSubTag(@RequestBody SubTagControllerBean mSubTagControllerBean, @RequestHeader String token) {
        return webService2.controllerSubTag(mSubTagControllerBean, token);
    }

    /**
     * 关闭/显示首页展示标签
     *
     * @param token|⚠️请求头携带         由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param doType|操作类型           0 - 展示 1 - 关闭|Int|是
     * @param subTagId|副标签ID|Long|是
     * @title 关闭/显示首页展示标签
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/3/14 上午10:11
     */
    @PostMapping("/openOrCloseHomeSubTag.web")
    public HttpResponse<Object> openOrCloseHomeSubTag(@RequestBody SubTagControllerBean mSubTagControllerBean, @RequestHeader String token) {
        return webService2.openOrCloseHomeSubTag(mSubTagControllerBean, token);
    }


    /**
     * 提现审核
     *
     * @param token|⚠️请求头携带              由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param withdrawalId|提现记录ID|Long|是
     * @param bType|操作类型                 0 - 审核通过(后台自动发起转账) 1 - 拒绝提现申请|Int|是
     * @param errorMsg|拒绝提现申请的原因         bType为1时该自动不能为空|String|否
     * @title 提现审核
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "提现申请已拒绝"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/29 上午10:11
     */
    @PostMapping("/withdrawalAudit.web")
    public HttpResponse<Object> withdrawalAudit(@RequestBody WithdrawalAuditBean mWithdrawalAuditBean, @RequestHeader String token) {
        return webService2.withdrawalAudit(mWithdrawalAuditBean, token);
    }

    /**
     * 发送系统通知
     *
     * @param token|⚠️请求头携带                     由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param notificationTitle|通知标题|String|是
     * @param notificationContent|通知内容|String|是
     * @param userId|通知对象用户ID|String|是
     * @title 发送系统通知
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "发送成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/27 上午10:11
     */
    @PostMapping("/sendSystemNotification.web")
    public HttpResponse<Object> sendSystemNotification(@RequestBody SystemNotification mSystemNotification, @RequestHeader String token) {
        return webService2.sendSystemNotification(mSystemNotification, token);
    }

    /**
     * 封禁/解封技能
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param demandId|技能ID|Int|是
     * @param doType|0            - 封禁 1 - 解封|Int|是
     * @title 封禁/解封技能
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/28 上午10:11
     */
    @PostMapping("/doBanSkill.web")
    public HttpResponse<Object> doBanSkill(@RequestBody RequestBean_BanSkill mRequestBean_BanSkill, @RequestHeader String token) {
        return webService2.doBanSkill(mRequestBean_BanSkill, token);
    }

    /**
     * 获取全部技能列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param page|分页字段     最少为传1 默认为1 |Int|否
     * @param number|分页字段   请求的条数 默认返回全部|Int|否
     * @title 获取全部技能列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/28 上午10:11
     */
    @PostMapping("/getAllSkill.web")
    public HttpResponse<Object> getAllSkill(@RequestBody ReqBeanGetAllSkill mReqBeanGetAllSkill, @RequestHeader String token) {
        return webService2.getAllSkill(mReqBeanGetAllSkill, token);
    }

    /**
     * 封禁/解封需求
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param demandId|需求ID|Int|是
     * @param doType|0            - 封禁 1 - 解封|Int|是
     * @title 封禁/解封需求
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/17 上午10:11
     */
    @PostMapping("/doBanDemand.web")
    public HttpResponse<Object> doBanDemand(@RequestBody RequestBean_BanDemand mRequestBean_BanDemand, @RequestHeader String token) {
        return webService2.doBanDemand(mRequestBean_BanDemand, token);
    }

    /**
     * 获取全部需求列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param state|筛选字段    3 - 全部需求 0-等待报名 1-服务中 2 服务完成  -1 已取消 -2 已过期 默认为3|Int|是
     * @param page|分页字段     最少为传1 默认为1 |Int|否
     * @param number|分页字段   请求的条数 默认返回全部|Int|否
     * @title 获取全部需求列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 68,
     * "userId": "c39ffb58-9ae5-46b8-a45c-8a46cde5e544",
     * "p_tag": 4,
     * "s_tag": 31,
     * "serviceTime": 157,
     * "expiryDate": 0,
     * "genderRequirements": -1,
     * "serviceMode": 1,
     * "demandDescribe": "This is a test of a release requirements",
     * "lat": 100.0,
     * "lng": 111.0,
     * "user": {
     * "userId": "c39ffb58-9ae5-46b8-a45c-8a46cde5e544",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1581859635171,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_c9ff2262f7994425af7b1",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "createTime": 1581859635171,
     * "merchanAuthState": 0,
     * "wopenIdx": "",
     * "qopenIdq": "",
     * "dispatcher": true,
     * "ban": false
     * },
     * "createTime": 1581940870456,
     * "demandState": 0,
     * "ptagBean": {
     * "parentId": 4,
     * "parentTagName": "社区服务",
     * "orderByNumber": 2
     * },
     * "stagBean": {
     * "subTagId": 31,
     * "subTitle": "兼职厨师",
     * "suggestPrice": 174.0,
     * "tagUnit": "次",
     * "tagImgUrl": "/Images/Skills/31.png",
     * "orderByNumber": 0,
     * "hotTag": 0
     * },
     * "ban": false
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/17 上午10:11
     */
    @PostMapping("/getAllDemand.web")
    public HttpResponse<Object> getAllDemand(@RequestBody ReqBeanGetWithdrawal2 mReqBeanGetWithdrawal, @RequestHeader String token) {
        return webService2.getAllDemand(mReqBeanGetWithdrawal, token);
    }

    /**
     * 用户充值流水
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param page|分页字段     最少为传1 默认为1 |Int|否
     * @param number|分页字段   请求的条数 默认返回全部|Int|否
     * @title 用户充值流水
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam userId|流水发生的用户ID|String|是
     * @resqParam price|充值的金额|Double|是
     * @resqParam createTime|记录生成时间|Long|是
     * @resqParam payType|支付方式 0-支付 1-微信|Int|是
     * @resqParam notesDec|流水说明|String|是
     * @resqParam user|流水发生的对象实体|User|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "userId": "56d7c388-f448-438e-8e81-e157ed736ba0",
     * "orderId": "",
     * "price": 1000.0,
     * "createTime": 1581863668736,
     * "payType": 0,
     * "notesDec": "管理员发放红包奖励交易收入1000.0元",
     * "user": {
     * "userId": "56d7c388-f448-438e-8e81-e157ed736ba0",
     * "userType": 0,
     * "phoneNumber": "18020011122",
     * "userName": "180****1122",
     * "sex": -1,
     * "birthday": 1581863667935,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_8257d22b51cc44d68940d",
     * "wyy_token": "yxToken_wb_v2_18020011122",
     * "createTime": 1581863667935,
     * "merchanAuthState": 0,
     * "wopenIdx": "",
     * "qopenIdq": "",
     * "dispatcher": false,
     * "ban": false
     * },
     * "btype": 2
     * },
     * {
     * "userId": "100f94d6-b937-4567-947c-4e149942fede",
     * "orderId": "",
     * "price": 12.0,
     * "createTime": 1581863669855,
     * "payType": 0,
     * "notesDec": "管理员发放红包奖励交易收入12.0元",
     * "user": {
     * "userId": "100f94d6-b937-4567-947c-4e149942fede",
     * "userType": 0,
     * "phoneNumber": "18020011121",
     * "userName": "180****1121",
     * "sex": -1,
     * "birthday": 1581863667616,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_29853148e2e74f29a45a0",
     * "wyy_token": "yxToken_wb_v2_18020011121",
     * "createTime": 1581863667616,
     * "merchanAuthState": 0,
     * "wopenIdx": "",
     * "qopenIdq": "",
     * "dispatcher": true,
     * "ban": false
     * },
     * "btype": 2
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/17 上午10:11
     */
    @PostMapping("/userTopUpWater.web")
    public HttpResponse<Object> userTopUpWater(@RequestBody Request_AddBalanceNoto mRequest_AddBalanceNoto, @RequestHeader String token) {
        return webService.userTopUpWater(mRequest_AddBalanceNoto, token);
    }

    /**
     * 分成流水记录查询 管理员查询所有分成情况，代理商只能查询与自己有关的分成
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param state|筛选字段    0-支出 1-收入 2-全部 默认返回全部|Int|否
     * @param page|分页字段     最少为传1 默认为1 |Int|否
     * @param number|分页字段   请求的条数 默认返回全部|Int|否
     * @title 分成流水记录查询
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @resqParam userId|流水发生的对象ID|String|是
     * @resqParam userBean|流水发生的对象实体|String|是
     * @resqParam price|变动的金额|Double|是
     * @resqParam btype|0-支出 1-收入|Int|是
     * @resqParam noteDec|说明内容|String|是
     * @resqParam createTime|记录生成时间|Long|是
     * @resqParam bftype|流水发生对象的类型 代理商或用户  0-用户 1-代理商，该字段用来确认toOrFromUserBean的实体，0-用户实体接收参数 1-web用户实体接收对象|Any|是
     * @resqParam toOrFromUserId|流水发生对象用户ID|String|是
     * @resqParam toOrFromUserBean|流水发生对象的实体 可能为用户实体 可能为web用户实体|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 42,
     * "userId": "admin",
     * "price": 1000.0,
     * "toOrFromUserId": "56d7c388-f448-438e-8e81-e157ed736ba0",
     * "orderId": "wb-1581863668636",
     * "noteDec": "购买技能付款",
     * "createTime": 1581916123379,
     * "userBean": {
     * "userId": "admin",
     * "phoneNumber": "admin",
     * "userName": "系统管理员",
     * "sex": 1,
     * "userLogo": "wb",
     * "userType": 0,
     * "realName": "系统管理员",
     * "balanceAll": 0.0,
     * "free_balance": 0.0,
     * "balance_geted": 0.0,
     * "balance": 1792.8
     * },
     * "toOrFromUserBean": {
     * "userId": "56d7c388-f448-438e-8e81-e157ed736ba0",
     * "userType": 0,
     * "phoneNumber": "18020011122",
     * "userName": "180****1122",
     * "sex": -1,
     * "birthday": 1581863667935,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_8257d22b51cc44d68940d",
     * "wyy_token": "yxToken_wb_v2_18020011122",
     * "createTime": 1581863667935,
     * "merchanAuthState": 0,
     * "dispatcher": false,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "bftype": 0,
     * "btype": 1
     * },
     * {
     * "id": 43,
     * "userId": "admin",
     * "price": 1000.0,
     * "toOrFromUserId": "100f94d6-b937-4567-947c-4e149942fede",
     * "orderId": "wb-1581863668636",
     * "noteDec": "支出给服务者",
     * "createTime": 1581916123378,
     * "userBean": {
     * "userId": "admin",
     * "phoneNumber": "admin",
     * "userName": "系统管理员",
     * "sex": 1,
     * "userLogo": "wb",
     * "userType": 0,
     * "realName": "系统管理员",
     * "balanceAll": 0.0,
     * "free_balance": 0.0,
     * "balance_geted": 0.0,
     * "balance": 1792.8
     * },
     * "toOrFromUserBean": {
     * "userId": "100f94d6-b937-4567-947c-4e149942fede",
     * "userType": 0,
     * "phoneNumber": "18020011121",
     * "userName": "180****1121",
     * "sex": -1,
     * "birthday": 1581863667616,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     * "wyy_accid": "yxaccid_wb_29853148e2e74f29a45a0",
     * "wyy_token": "yxToken_wb_v2_18020011121",
     * "createTime": 1581863667616,
     * "merchanAuthState": 0,
     * "dispatcher": true,
     * "ban": false,
     * "wopenIdx": "",
     * "qopenIdq": ""
     * },
     * "bftype": 0,
     * "btype": 0
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2020/2/17 上午10:11
     */
    @PostMapping("/dividedWaterSearch.web")
    public HttpResponse<Object> dividedWaterSearch(@RequestBody Request_DivWater mRequest_DivWater, @RequestHeader String token) {
        return webService.dividedWaterSearch(mRequest_DivWater, token);
    }

    /**
     * 提现成功接口
     *
     * @param token|⚠️请求头携带    由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param id|提现记录ID|Long|是
     * @title 提现成功接口
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
     * @date 2019/12/30 上午10:11
     */
    @PostMapping("/markWithdrawal.web")
    public HttpResponse<Object> markWithdrawal(@RequestBody ReqBeanMarkedWithdrawal mReqBeanMarkWithdrawal, @RequestHeader String token) {
        return webService.markWithdrawal(mReqBeanMarkWithdrawal, token);
    }

    /**
     * 添加金额（无需支付，记录的流水为管理员手动添加）
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param price|添加的金额|Float|是
     * @param userId|添加的用户ID      为空时给自己添加|String|是
     * @title 添加金额
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/23 上午10:41
     */
    @PostMapping("/addBalance.web")
    public HttpResponse<Object> addBalance(@RequestBody AddBalanceBean mAddBalanceBean, @RequestHeader String token) {
        return webService.addBalance(mAddBalanceBean, token);
    }

    /**
     * 查询收获码
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @title 查询收获码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": 8768
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/19 上午10:41
     */
    @PostMapping("/getTransactionCode.web")
    public HttpResponse<Object> getTransactionCode(@RequestBody RequestBean_CancleOrder mRequestBean_CancleOrder, @RequestHeader String token) {
        return webService.getTransactionCode(mRequestBean_CancleOrder, token);
    }
//    /**
//     * 单文件上传
//     * @param file
//     * @return
//     */
//    @PostMapping("/upload1")
//    @ResponseBody
//    public HttpResponse<Object> upload1(@RequestParam("file") MultipartFile file)
//            throws IOException {
//        logger.info("[文件类型] - [{}]", file.getContentType());
//        logger.info("[文件名称] - [{}]", file.getOriginalFilename());
//        logger.info("[文件大小] - [{}]", file.getSize());
//       File file2 = new File("/Users/zhaowenwen/IdeaProjects/wb/" + file.getOriginalFilename());
//       if (file2.getParentFile().exists()){
//           file2.getParentFile().mkdir();
//       }
//        file.transferTo(file2);
//
//        try (Graph g = new Graph()) {
//            final String value = "Hello from " + TensorFlow.version();
//
//            // Construct the computation graph with a single operation, a constant
//            // named "MyConst" with a value "value".
//            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
//                // The Java API doesn't yet include convenience functions for adding operations.
//                g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
//            }
//
//            // Execute the "MyConst" operation in a Session.
//            try (Session s = new Session(g);
//                 // Generally, there may be multiple output tensors,
//                 // all of them must be closed to prevent resource leaks.
//                 Tensor output = s.runner().fetch("MyConst").run().get(0)) {
//                System.out.println(new String(output.bytesValue(), "UTF-8"));
//            }
//        }
//        String tensorflowVersion = TensorFlow.version();
////        TensorFlow.
//        logger.info("tensorflowVersion:"+tensorflowVersion);
//       org.tensorflow.TensorFlow.loadLibrary("classpath:nsfw.tflite");
////        Map<String, String> result = new HashMap<>(16);
////        result.put("contentType", file.getContentType());
////        result.put("fileName", file.getOriginalFilename());
////        result.put("fileSize", file.getSize() + "");
//        return new  HttpResponse(tensorflowVersion);
//    }

    /**
     * 商户审核
     *
     * @param token|⚠️请求头携带           由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param merchanId|商户ID|Long|是
     * @param bType|0-                审核通过 1-审核拒绝|Int|是
     * @param errMsg|审核失败的原因|String|否
     * @title 商户审核
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/verMerchant.web")
    public HttpResponse<Object> verMerchant(@RequestBody Request_Web_Merchan2 mRequest_Web_Merchan2, @RequestHeader String token) {
        return webService.verMerchant(mRequest_Web_Merchan2, token);
    }

    /**
     * 获取商户列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param bType|获取类型    0-获取全部商户 1-获取未审核商户 2-获取审核通过用户 3-获取审核被拒用户|Int|是
     * @title 获取商户列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "merchanId": 48,
     * "userId": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     * "verifyDec": "",
     * "verifyState": 1,
     * "createTime": 1575259879280,
     * "merchanTitle": "TestTitle",
     * "merchanUserName": "Jason",
     * "merchanPhoneNumber": "1888888888",
     * "authId": 40,
     * "merchanPhotos": "aaaaa|bbbb",
     * "merchanBaseTagId": 5,
     * "serviceTime": "lll",
     * "city_merchan": "TestCity222222",
     * "addressDetail_Merchan": "aaaaaa",
     * "addressInfo": "testInfo",
     * "serverBusinessLicenseType": 0,
     * "serverBusinessLicenseName": "testName",
     * "serverBusinessLicenseNumber": "111111",
     * "serverBusinessLicenseEndTime": 10000111,
     * "serverBusinessLicensePhotos": "aaa",
     * "serverPermitLicenseNmae": "name",
     * "serverPermitLicenseRange": "aaaaa",
     * "serverPermitLicenseEndTime": 111,
     * "serverPermitLicensePhotos": "ccccc"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/getMerchantList.web")
    public HttpResponse<Object> getMerchantList(@RequestBody Request_Web_Merchan mRequest_Web_Merchan, @RequestHeader String token) {
        return webService.getMerchantList(mRequest_Web_Merchan, token);
    }

    /**
     * 删除注册码 多个注册码用逗号隔开
     *
     * @param token|⚠️请求头携带  由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param registerId|注册码 多个注册码用逗号隔开|String|是
     * @title 删除注册码（新）
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "id": 567,
     * "title": "888888",
     * "url": "",
     * "permission": 0,
     * "parentTabId": null,
     * "subTabList": [
     * {
     * "id": 569,
     * "title": "999990999999",
     * "url": "",
     * "permission": 0,
     * "parentTabId": 567,
     * "subTabList": []
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/deleteRegisterCode.web")
    public HttpResponse<Object> deleteRegisterCode(@RequestBody RequestBean_RegisterCodeDetail mRequestBean_RegisterCodeDetail, @RequestHeader String token) {
        return webService.deleteRegisterCode(mRequestBean_RegisterCodeDetail, token);
    }

    /**
     * 删除/封禁/解封代理商
     *
     * @param token|⚠️请求头携带           由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userId|代理商用户ID|String|是
     * @param bType|删除或封禁             0-删除 1-封禁 2-解封|Int|是
     * @title 删除/封禁/解封代理商
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data":""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/25 下午4:41
     */
    @PostMapping("/delOrBanAgentUser.web")
    public HttpResponse<Object> delOrBanAgentUser(@RequestBody RquestBean_DelAgent userWeb, @RequestHeader String token) {
        return webService.delOrBanAgentUser(userWeb, token);
    }

    /**
     * 修改个人资料 字段为空时不修改
     *
     * @param token|⚠️请求头携带             由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param phoneNumber|电话号码|String|否
     * @param userName|用户名|String|否
     * @param sex|性别                    0-女 1-男|Int|否
     * @param userLogo|用户头像url|String|否
     * @param realName|真实姓名|String|否
     * @title 修改个人资料
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data":""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/25 下午4:41
     */
    @PostMapping("/updateUserInfo.web")
    public HttpResponse<Object> updateUserInfo(@RequestBody User_Web userWeb, @RequestHeader String token) {
        return webService.updateUserInfo(userWeb, token);
    }

    /**
     * 获取代理商列表 系统管理员可以查询所有代理商信息  一级代理商只能查询他线下的二级代理商
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取代理商列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "userId": "cde5db05-010b-40d7-9407-96f187932d34",
     * "phoneNumber": "2",
     * "userName": "二级代理商",
     * "sex": 1,
     * "userLogo": "22222",
     * "userType": 2,
     * "realName": "王麻子",
     * "superAgentId": "117dd0ef-e621-439c-855a-bfbc874700e0",
     * "proportion": 20.0,
     * "balanceAll": 0.0,
     * "free_balance": 0.0,
     * "balance_geted": 0.0,
     * "balance": 560.0,
     * "boundAreaListStr": "110101001000,110102001000"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/25 下午4:41
     */
    @PostMapping("/getAgentList.web")
    public HttpResponse<Object> getAgentList(@RequestBody RequestBean_List mRequestBean_List, @RequestHeader String token) {
        return webService.getAgentList(mRequestBean_List, token);
    }

    /**
     * 添加首页菜单
     *
     * @param token|⚠️请求头携带           由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param title|显示在侧边栏的标题         同级别的用户不能添加重复的tab 例如管理员权限下已添加一个名为·系统设置·的标题则不能添加第二个|String|是
     * @param url|Tab点击后跳转连接|String|是
     * @param permission|权限           用来控制哪个角色显示当前添加的tab 0 系统管理员加载  1 一级代理商显示  2 二级代理商显示 3 三级代理商显示|Int|是
     * @param parentTabId|上级ID        如果上级ID为空时添加到一级|Long|否
     * @title 添加首页菜单(改)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/addWebHomeBaseTab.web")
    public HttpResponse<Object> addWebHomeBaseTab(@RequestBody WebTabTitleBean webTabTitleBean, @RequestHeader String token) {
        return webService.addWebHomeTab(webTabTitleBean, token);
    }

    /**
     * 删除首页菜单 如果删除的为一级菜单时下级的所有子菜单将会被移除
     *
     * @param token|⚠️请求头携带     由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param tabId|菜单ID|Long|是
     * @title 删除首页菜单(改)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/deleteWebHomeTab.web")
    public HttpResponse<Object> deleteWebHomeTab(@RequestBody RequestBean_DeleteWebTab mre, @RequestHeader String token) {
        return webService.deleteWebHomeTab(mre, token);
    }

    /**
     * 修改首页菜单
     *
     * @param token|⚠️请求头携带     由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param tabId|菜单ID|Long|是
     * @param title|标题          |String|是
     * @param url|跳转连接          |String|是
     * @title 修改首页菜单(改)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/updateWebHomeTab.web")
    public HttpResponse<Object> updateWebHomeTab(@RequestBody RequestBean_DeleteWebTab mre, @RequestHeader String token) {
        return webService.updateWebHomeTab(mre, token);
    }

    /**
     * 获取首页菜单栏
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取首页菜单栏
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 426,
     * "title": "面板",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 427,
     * "title": "面板",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 428,
     * "title": "服务商管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": [
     * {
     * "id": 541,
     * "parentTabId": 428,
     * "title": "管理员显示二级",
     * "url": "wwwww"
     * }
     * ]
     * },
     * {
     * "id": 429,
     * "title": "广告管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 430,
     * "title": "推广二维码",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 431,
     * "title": "数据统计",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 432,
     * "title": "审核管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 433,
     * "title": "用户取现管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 434,
     * "title": "代理商取现管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 435,
     * "title": "代理商管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 436,
     * "title": "代理商专属Apk管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 437,
     * "title": "优惠券管理",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 438,
     * "title": "系统工具",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * },
     * {
     * "id": 439,
     * "title": "个人中心",
     * "url": "http://www.baidu.com",
     * "permission": 0,
     * "subTabList": []
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/getWebHomeTab.web")
    public HttpResponse<Object> getWebHomeTab(@RequestHeader String token) {
        return webService.getWebHomeTab(token);
    }

    /**
     * 获取首页菜单详情
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param tabId|tabId   可以是一级也可以是二级|Long|是
     * @title 获取首页菜单详情（新）
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "id": 567,
     * "title": "888888",
     * "url": "",
     * "permission": 0,
     * "parentTabId": null,
     * "subTabList": [
     * {
     * "id": 569,
     * "title": "999990999999",
     * "url": "",
     * "permission": 0,
     * "parentTabId": 567,
     * "subTabList": []
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/27 上午午10:41
     */
    @PostMapping("/getHomeTabDetail.web")
    public HttpResponse<Object> getHomeTabDetail(@RequestBody RequestBean_DeleteWebTab mRequestBean_DeleteWebTab, @RequestHeader String token) {
        return webService.getHomeTabDetail(mRequestBean_DeleteWebTab, token);
    }

    /**
     * 根据注册码查询对应的实体
     *
     * @param token|⚠️请求头携带             由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param registerId|注册码编号|String|是
     * @title 获取注册码详细数据
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|RegisterCodeBean|是
     * @resqParam 以下为实体RegisterCodeBean的参数说明|以下为实体RegisterCodeBean的参数说明|以下为实体RegisterCodeBean的参数说明|以下为实体RegisterCodeBean的参数说明
     * @resqParam registerId|注册码编号 代理商将使用该编号进行注册操作 有效期10分钟 10分钟后将自动删除|String|是
     * @resqParam creatUserId|生成用户ID|String|是
     * @resqParam boundAreaListSrt|注册码绑定的城市ID信息，多个ID将使用逗号隔开 当registerCodeType字段为0时 该处为城市ID 为1时 该处为镇区ID|String|是
     * @resqParam proportion|与代理商的分成比例 字段registerCodeType为1时出现|Float|否
     * @resqParam creatTime|生成时间|Long|是
     * @resqParam registerCodeType|注册码类型 0-一级代理商注册码 1-二级代理商注册码|Int|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "registerId": "9295f3e7-1305-44e3-82c3-073e710249c8",
     * "creatUserId": "admin",
     * "boundAreaListSrt": "110102000000,110107000000",
     * "proportion": 20.0,
     * "creatTime": 1574406435679,
     * "registerCodeType": 0
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/22 下午1:41
     */
    @PostMapping("/getRegisterCodeDetail.web")
    public HttpResponse<Object> getRegisterCodeDetail(@RequestBody RequestBean_RegisterCodeDetail mRequestBean_RegisterCodeDetail, @RequestHeader String token) {
        return webService.getRegisterCodeDetail(mRequestBean_RegisterCodeDetail, token);
    }

    /**
     * 获取当前用户生成的有效注册码
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取有效注册码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 数据说明参考 注册码详情接口|RegisterCodeBean|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "registerId": "b45fb484-2d62-44ab-85fc-489d89708b61",
     * "creatUserId": "admin",
     * "boundAreaListSrt": "110100000000",
     * "proportion": null,
     * "creatTime": 1574406108677,
     * "registerCodeType": 0
     * },
     * {
     * "registerId": "58401867-961d-4481-8c31-82977099cd00",
     * "creatUserId": "admin",
     * "boundAreaListSrt": "110101000000",
     * "proportion": 50.0,
     * "creatTime": 1574406217626,
     * "registerCodeType": 0
     * },
     * {
     * "registerId": "843fbbac-a762-432e-9ba3-c05d56575bad",
     * "creatUserId": "admin",
     * "boundAreaListSrt": "120100000000,130100000000,130400000000",
     * "proportion": null,
     * "creatTime": 1574406375345,
     * "registerCodeType": 0
     * },
     * {
     * "registerId": "9295f3e7-1305-44e3-82c3-073e710249c8",
     * "creatUserId": "admin",
     * "boundAreaListSrt": "110102000000,110107000000",
     * "proportion": 20.0,
     * "creatTime": 1574406435679,
     * "registerCodeType": 0
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/22 下午1:41
     */
    @PostMapping("/getAllRegisterCode.web")
    public HttpResponse<Object> getAllRegisterCode(@RequestHeader String token) {
        return webService.getRegisterCode(token);
    }

    /**
     * 生成一级代理商、二级代理商注册码 注册码长期有效
     *
     * @param token|⚠️请求头携带                                    由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param boundAreaListSrt|可用数据使用获取城市信息列表的集合得到             生成一级代理商传入的为区ID编号，多个用逗号隔开 二级代理商只能传入代理的区域的小街道的ID，多个用逗号隔开|String|是
     * @param proportion|与生成的该二级代理商的分成比例，生成二级代理商注册码时必填|Float|否
     * @title 生成注册码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/22 下午1:41
     */
    @PostMapping("/creatRegisterCode.web")
    public HttpResponse<Object> creatRegisterCode(@RequestBody RequestBean_RegisterCode mRequestBean_RegisterCode, @RequestHeader String token) {
        return webService.creatRegisterCode(mRequestBean_RegisterCode, token);
    }

    /**
     * 根据当前用户类型 获取省市区信息 管理员可获取到所有省市区信息  一级代理商只能获取到他代理的城市信息
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取城市信息
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源(页面加载缓慢 删减过数据 参考真实接口返回数据) 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": []
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/22 下午1:41
     */
    @PostMapping("/getCityList.web")
    public HttpResponse<Object> getCityList(@RequestHeader String token) {
        return webService.getCityList(token);
    }

    /**
     * 删除公告栏
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 删除公告栏
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 下午2:41
     */
    @PostMapping("/deleteShowBoradContent.web")
    public HttpResponse<Object> deleteShowBoradContent(@RequestBody ShowWebBoradTab showWebBoradTab, @RequestHeader String token) {
        return webService.deleteShowBoradContent(token);
    }

    /**
     * 发布公告栏
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param title|标题|String|是
     * @param content|内容|String|是
     * @title 发布公告栏
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 下午2:41
     */
    @PostMapping("/sendShowBoradContent.web")
    public HttpResponse<Object> sendShowBoradContent(@RequestBody ShowWebBoradTab showWebBoradTab, @RequestHeader String token) {
        return webService.sendShowBoradContent(showWebBoradTab, token);
    }


    /**
     * 获取首页数据
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取首页数据
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 说明如下|Any|是
     * @resqParam upDayAddUserNumber|昨日新增用户数|Int|是
     * @resqParam upDayUserNumber|昨日用户总数|Int|是
     * @resqParam upDayOrderNumber|昨日订单总数|Int|是
     * @resqParam upDayOrderAllPrice|昨日订单总额|Float|是
     * @resqParam upDayCanUseBalance|昨日可用余额|Float|是
     * @resqParam upDayAllGetedBalance|昨日总计收入|Float|是
     * @resqParam boardShowDec|公告栏展示内容|实体 返回内容：{
     * "id": 0,
     * "title": "标题",//标题
     * "content": "测试的公告栏内容",//内容
     * "creatTime": 1574320350837,//创建时间
     * "creatUserId": "156075ca-b480-4d2e-84f5-9054f5973c19"//发布的用户ID
     * }|是
     * @resqParam userTop10|新注册的前10个用户列表 User实体参考前端Api-登录返回实体|List<User>|否
     * @resqParam orderTop10|最新的10个订单列表 WbOrder实体参考前端Api-订单详情返回实体|List<WbOrder>|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "upDayAddUserNumber": 0,
     * "upDayUserNumber": 15,
     * "upDayOrderNumber": 0,
     * "upDayOrderAllPrice": 0.0,
     * "userTop10": [
     * {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "ban": false,
     * "wopenIdx": "111111111"
     * }
     * ],
     * "upDayCanUseBalance": 0.0,
     * "upDayAllGetedBalance": 0.0,
     * "boardShowDec": "测试的公告栏内容",
     * "orderTop10": [
     * {
     * "orderId": "wb-1574146174026",
     * "orderType": 0,
     * "orderBusinessId": "380",
     * "state": -1,
     * "number": 1,
     * "orderDec": "111222211",
     * "price": 100.0,
     * "userIdFromBuy": "admin",
     * "userIdFromServer": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "orderCreateTime": 1574146174026,
     * "saddressId": 413,
     * "stime": 1111111
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 上午10:11
     */
    @PostMapping("/getHomeData.web")
    public HttpResponse<Object> getHomeData(@RequestHeader String token) {
        return webService.getHomeData(token);
    }

    /**
     * 登录(当登录成功时会在请求头携带用户的token,使用键token获取值,客户端需要保存该值,后续业务接口调用时均需要携带该值)
     *
     * @param phoneNumber|电话号码|String|是
     * @param pwd|密码|String|是
     * @title 登录
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Int|是
     * @resqParam userId|用户ID|String|是
     * @resqParam phoneNumber|用户电话号码|String|是
     * @resqParam userName|角色名称|String|是
     * @resqParam sex|性别 -1未设置 0女 1男|Int|是
     * @resqParam userLogo|用户头像|String|是
     * @resqParam userType|用户类型 0-管理员 1-一级代理商 2-二级代理商 3-三级代理商|Int|是
     * @resqParam realName|用户真实姓名|String|是
     * @resqParam balanceAll|总余额|Float|是
     * @resqParam free_balance|冻结金额|Float|是
     * @resqParam balance_geted|已提取金额|Float|是
     * @resqParam balance|可用余额|Float|是
     * @resqParam proportion|分成比例设置|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "userId": "5a225a4e-85db-496c-8a2c-505ff25790e9",
     * "phoneNumber": "122211111",
     * "userName": "管理员",
     * "sex": 1,
     * "userLogo": "22222",
     * "userType": 0,
     * "realName": "张三",
     * "balanceAll": 0.0,
     * "free_balance": 0.0,
     * "balance_geted": 0.0,
     * "balance": 0.0
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 上午10:11
     */
    @PostMapping("/login.web")
    public HttpResponse<Object> login(@RequestBody Request_Web_Register userWeb, HttpServletResponse httpServletResponse) {
        return webService.login(userWeb, httpServletResponse);
    }


    /**
     * 代理商注册
     *
     * @param phoneNumber|电话号码|String|是
     * @param pwd|密码|String|是
     * @param userName|用户昵称|String|否
     * @param sex|-1                          未知 男 1 女 0|Int|否
     * @param userLogo|用户头像|String|否
     * @param realName|真实姓名|String|是
     * @param registerId|注册码|String|是
     * @param proportion|当前代理商设置的与代理区域的用户分成比例 与注册码代理的区域绑定 传参拼接规则：注册码-分成比例,注册码-分成比例 eg:11111-20,22222-50 这里的20/50均代表当前用户所得百分比 比如服务者收入120 设置50后可得120*(50/100.0)=60元|String|是
     * @title 代理商注册
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Int|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": 1234
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 上午10:11
     */
    @PostMapping("/register.web")
    public HttpResponse<Object> register(@RequestBody Request_Web_Register userWeb) {
        return webService.register(userWeb);
    }

    /**
     * 根据电话号码查询验证码
     *
     * @param token|⚠️请求头携带                由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param phoneNumber|用户的电话号码|String|是
     * @title 根据电话号码查询验证码
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Int|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": 1234
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 上午10:11
     */
    @PostMapping("/getMsgCodeByPhoneNumber.web")
    public HttpResponse<Object> getMsgCodeByPhoneNumber(@RequestBody RequestBean_GetMsgCode mRequestBean_GetMsgCode) {
        return webService.getMsgCodeByPhoneNumber(mRequestBean_GetMsgCode, "");
    }

    /**
     * 获取用户列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取用户列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|User 参考登录接口|否
     * @respBody [
     * {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "wopenIdx": "111111111",
     * "ban": false
     * },
     * {
     * "userId": "admin",
     * "userType": 0,
     * "phoneNumber": "admin",
     * "userName": "系统管理员",
     * "sex": 1,
     * "birthday": 999999999,
     * "userLogo": "http://e.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "ban": false
     * },
     * {
     * "userId": "d6137bd9-d28e-435b-8138-a88c9571f945",
     * "userType": 0,
     * "phoneNumber": "15317026222",
     * "userName": "153****6222",
     * "sex": -1,
     * "birthday": 1574046613557,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_b0d16cff0e604a6f9d2dd",
     * "wyy_token": "yxToken_wb_v2_15317026222",
     * "userBussnessType": 0,
     * "wopenIdx": "",
     * "ban": false
     * },
     * {
     * "userId": "1f807c8b-b106-4639-b908-52c56180bf6c",
     * "userType": 0,
     * "phoneNumber": "18888888888",
     * "userName": "188****8888",
     * "sex": -1,
     * "birthday": 1574060418808,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_539e370f07e4409a9e376",
     * "wyy_token": "yxToken_wb_v2_18888888888",
     * "userBussnessType": 0,
     * "wopenIdx": "",
     * "ban": false
     * },
     * {
     * "userId": "759ac251-a89a-43b6-8eb0-37f468cca07f",
     * "userType": 0,
     * "phoneNumber": "15317026228",
     * "userName": "153****6228",
     * "sex": -1,
     * "birthday": 1574130575182,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_e8496e7016fa44f8b4ee3",
     * "wyy_token": "yxToken_wb_v2_15317026228",
     * "userBussnessType": 0,
     * "wopenIdx": "",
     * "ban": false
     * },
     * {
     * "userId": "954cf790-e95e-490a-b3cb-30a3e8268283",
     * "userType": 0,
     * "phoneNumber": "15317026299",
     * "userName": "153****6299",
     * "sex": -1,
     * "birthday": 1574131087407,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_0ff1ffb5c0b84e2bb2440",
     * "wyy_token": "yxToken_wb_v2_15317026299",
     * "userBussnessType": 0,
     * "wopenIdx": "",
     * "ban": false
     * },
     * {
     * "userId": "8d23ab97-5400-4029-8b14-6eddd8f9418e",
     * "userType": 0,
     * "phoneNumber": "15317026666",
     * "userName": "153****6666",
     * "sex": -1,
     * "birthday": 1574134299110,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_b5fcb4e41ca14e458f1ff",
     * "wyy_token": "yxToken_wb_v2_15317026666",
     * "userBussnessType": 0,
     * "wopenIdx": "wx1111",
     * "qopenIdq": "",
     * "ban": false
     * },
     * {
     * "userId": "e301bf9c-7000-441c-814b-ab9fdd42631a",
     * "userType": 0,
     * "phoneNumber": "15317027777",
     * "userName": "153****7777",
     * "sex": -1,
     * "birthday": 1574134789068,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_c2bfbd1d664541e2b5a80",
     * "wyy_token": "yxToken_wb_v2_15317027777",
     * "userBussnessType": 0,
     * "wopenIdx": "wxeeeeeeeee",
     * "qopenIdq": "",
     * "ban": false
     * },
     * {
     * "userId": "c7e19759-3698-4013-a1dc-0c54e39578df",
     * "userType": 0,
     * "phoneNumber": "15317027778",
     * "userName": "153****7778",
     * "sex": -1,
     * "birthday": 1574134830272,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_5bf9c7ea01414718a5ae7",
     * "wyy_token": "yxToken_wb_v2_15317027778",
     * "userBussnessType": 0,
     * "wopenIdx": "",
     * "qopenIdq": "wxeeeeeeeee",
     * "ban": false
     * }
     * ]
     * @requestType post
     * @author Jason
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/getAllUsers.web")
    public HttpResponse<Object> getAllUsers(@RequestHeader String token, @RequestBody RequestBean_GetAllSkill mRequestBean_GetAllSkill) {
        return webService.getAllUsers(mRequestBean_GetAllSkill, token);
    }

    /**
     * 删除或封禁用户
     *
     * @param token|⚠️请求头携带                由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param phoneNumber|用户的电话号码|String|是
     * @param doType|0-封禁                  1-删除 2-解封|Int|是
     * @title 删除或封禁用户
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
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/deleteOrBanUser.web")
    public HttpResponse<Object> deleteOrBanUser(@RequestBody RequestBean_DeleteOrBan mRequestBean_DeleteOrBan, @RequestHeader String token) {
        return webService.deleteOrBanUser(mRequestBean_DeleteOrBan, token);
    }


    /**
     * 抽奖
     *
     * @title 抽奖
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
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/getLuckFrawNumber.web")
    public HttpResponse<Object> getLuckFrawNumber() {
        return webService.getLuckFrawNumber();
    }


    /**
     * 标记日志操作
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param logId|日志ID|String|是
     * @param delType|0-标记全部日志为已修复 2-标记logId对应的记录为已修复|Int|是
     * @title 标记日志操作
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
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/fixLogByLogId.web")
    public HttpResponse<Object> fixLogByLogId(@RequestBody RequestBean_DeleteLog mRequestBean_DeleteLog) {
        return webService.fixLogByLogId(mRequestBean_DeleteLog);
    }

    /**
     * 日志操作
     *
     * @param token|⚠️请求头携带       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param logId|日志ID|String|是
     * @param delType|0-删除全部日志    1-删除已修复日志  2-删除logId对应的记录|Int|是
     * @title 删除日志
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
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/deleteLog.web")
    public HttpResponse<Object> deleteLog(@RequestBody RequestBean_DeleteLog mRequestBean_DeleteLog) {
        return webService.deleteLog(mRequestBean_DeleteLog);
    }


    /**
     * 获取日志列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取日志列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": "af155f99-b838-430f-8199-73509fa0cfd0",
     * "createTime": "2019-11-29 09:47:14",
     * "logLever": 0,
     * "exceptionName": "No property id found for type User!",
     * "exceptionContent": "com.xinlian.wb.core.service.service.impl.WebServiceImpl,com.xinlian.wb.controller.WebApiController,com.xinlian.wb.core.HttpServletRequestFilter,com.xinlian.wb.core.HttpServletRequestFilter",
     * "lineNumber": "174,924,40,40",
     * "exceptionState": 0
     * },
     * {
     * "id": "49739c55-4fd3-4b0c-b038-79f5a8462e5f",
     * "createTime": "2019-11-29 09:49:33",
     * "logLever": 0,
     * "exceptionName": "No property create found for type User!",
     * "exceptionContent": "com.xinlian.wb.core.service.service.impl.WebServiceImpl,com.xinlian.wb.controller.WebApiController,com.xinlian.wb.core.HttpServletRequestFilter,com.xinlian.wb.core.HttpServletRequestFilter",
     * "lineNumber": "174,924,40,40",
     * "exceptionState": 0
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/20 上午10:11
     */
    @PostMapping("/getLogList.web")
    public HttpResponse<Object> getLogList() {
        return webService.getLogList();
    }

    /**
     * 获取Web页面首页数据
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取Web页面首页数据
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "usersNumber": 9,
     * "skillsNumber": 3,
     * "skillSuccNumber": 0,
     * "logNumber": 0,
     * "skillList": [
     * {
     * "id": 380,
     * "user": {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "wopenIdx": "111111111",
     * "ban": false
     * },
     * "subTagsStr": "332,2076@331,2068@333,2081@334,2085@335,2088@336,2092",
     * "serviceDec": "测试：这是一条测试的技能介绍",
     * "imgsUrl": "http://q06livvk1.bkt.clouddn.com/1574045308262?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10wb|http://q06livvk1.bkt.clouddn.com/1574045308265?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10wb|http://q06livvk1.bkt.clouddn.com/1574045308258?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10wb|http://q06livvk1.bkt.clouddn.com/1574045308254?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10wb|http://q06livvk1.bkt.clouddn.com/1574045308182?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10",
     * "unitStr": "月",
     * "price": 8000.0,
     * "stockNumber": 999,
     * "skillState": 0,
     * "lat": 31.0383,
     * "lng": 121.252,
     * "p_tag": 13,
     * "s_tag": 112,
     * "skill_type": 0,
     * "distance": 0,
     * "autoRegistration": true
     * },
     * {
     * "id": 382,
     * "user": {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "userType": 0,
     * "phoneNumber": "15317026229",
     * "userName": "153****6229",
     * "sex": -1,
     * "birthday": 1574045272848,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_f6ef45d7d5754175a96c4",
     * "wyy_token": "yxToken_wb_v2_15317026229",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "wopenIdx": "111111111",
     * "ban": false
     * },
     * "subTagsStr": "337,2097@338,2104",
     * "serviceDec": "测试：这是一条测试的技能介绍",
     * "imgsUrl": "http://q06livvk1.bkt.clouddn.com/1574045330846?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10wb|http://q06livvk1.bkt.clouddn.com/1574045330851?imageView2/0/w/1334/h/750/format/jpg/q/75|watermark/2/text/5aiB5biu/font/5a6L5L2T/fontsize/540/fill/I0ZERkRGRA==/dissolve/100/gravity/SouthEast/dx/10/dy/10",
     * "unitStr": "天",
     * "price": 5000.0,
     * "stockNumber": 999,
     * "skillState": 0,
     * "lat": 31.0383,
     * "lng": 121.252,
     * "p_tag": 13,
     * "s_tag": 113,
     * "skill_type": 0,
     * "distance": 0,
     * "autoRegistration": true
     * },
     * {
     * "id": 384,
     * "user": {
     * "userId": "admin",
     * "userType": 0,
     * "phoneNumber": "admin",
     * "userName": "系统管理员",
     * "sex": 1,
     * "birthday": 999999999,
     * "userLogo": "http://e.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
     * "userBussnessType": 0,
     * "indentity": {
     * "id": 386,
     * "idemyityName": "赵文贇",
     * "idemyityCardNumber": "620522199303113734"
     * },
     * "ban": false
     * },
     * "subTagsStr": "222|222|2222",
     * "serviceDec": "(服务)爱犬丢失，望好心人士看到后告知我，定有重礼相送，左侧为我家二狗子的照片",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "unitStr": "元",
     * "price": 1020.5,
     * "stockNumber": 999,
     * "skillState": 0,
     * "lat": 111.0,
     * "lng": 111.0,
     * "p_tag": 52,
     * "s_tag": 99,
     * "skill_type": -1,
     * "distance": 0,
     * "autoRegistration": true
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/20 上午10:11
     */
    @ApiIgnore
    @PostMapping("/getWebViewInfo.web")
    private HttpResponse<Object> getWebViewInfo(@RequestHeader String token) {
        return webService.getWebViewInfo(token);
    }

}


