package com.xinlian.wb.controller;

import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.IPublicService;
import com.xinlian.wb.core.service.service.ISignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * 公用接口(.action)
 *
 * @author Jason
 * @action /wb/public
 * @date 2020/2/28 下午1:34
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private IPublicService mIPublicService;

    private Logger logger = LoggerFactory.getLogger(PublicController.class);

    /**
     * 获取申请提现列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param bType|管理员查询时传入有效 0 - 查询全部用户提现记录 1- 查询全部代理商提现记录 -1 - 查询全部提现记录 默认-1|Int|否
     * @param state|对应的状态   默认值-1 获取全部列表|Int|否
     * @param page|请求的页码    默认 1 必须从1开始|Int|否
     * @param number|请求的条数  默认返回全部数据|Int|否
     * @title 获取申请提现列表1
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|否
     * @resqParam state|提现的状态|Int|是
     * @resqParam price|提现的金额|Float|是
     * @resqParam alipayUserName|支付宝姓名|String|是
     * @resqParam aliaAccount|支付宝账号|String|是
     * @resqParam userId|提现申请的用户ID|String|是
     * @resqParam user|提现申请的用户实体，这里实体的类型根据userType字段区分|Any|是
     * @resqParam userType|提现申请发起的用户类型 0 - 用户 1-代理商|Int|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "id": 342,
     *       "createTime": 1582250201021,
     *       "state": 0,
     *       "price": 100.0,
     *       "alipayUserName": "潘颖",
     *       "aliaAccount": "18174424807",
     *       "userId": "03241f69-5215-4eb4-8146-7a72d889c39f",
     *       "user": {
     *         "userId": "03241f69-5215-4eb4-8146-7a72d889c39f",
     *         "userType": 1,
     *         "phoneNumber": "18174424807",
     *         "userName": "181****4807",
     *         "sex": -1,
     *         "birthday": 1581922038475,
     *         "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575440774873&di=8ca993e4de0156127f52cc02a364911f&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F69ad7a731f43d4b8729f1a2fbe65c43801ca0f033250-EV1vMf_fw658",
     *         "wyy_accid": "yxaccid_wb_e986bcaf939842619f89d",
     *         "wyy_token": "yxToken_wb_v2_18174424807",
     *         "createTime": 1581922038475,
     *         "merchanAuthState": 0,
     *         "ban": false,
     *         "dispatcher": true,
     *         "wopenIdx": "",
     *         "qopenIdq": ""
     *       },
     *       "userType": 0
     *     },
     *     {
     *       "id": 524,
     *       "createTime": 1582892571263,
     *       "state": 0,
     *       "price": 10.0,
     *       "alipayUserName": "张三",
     *       "aliaAccount": "dev_zwy@aliyun.com",
     *       "userId": "eec21274-1a09-4729-a941-88215e279bd5",
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
     *       "userType": 0
     *     },
     *     {
     *       "id": 528,
     *       "createTime": 1582893416029,
     *       "state": 0,
     *       "price": 10.0,
     *       "alipayUserName": "代理商",
     *       "aliaAccount": "dev_zwy@aliyun.com",
     *       "userId": "11c63f2b-8eb9-4530-866e-c38e93b8be59",
     *       "user": {
     *         "userId": "11c63f2b-8eb9-4530-866e-c38e93b8be59",
     *         "phoneNumber": "18000000000",
     *         "userName": "1级代理商",
     *         "sex": 1,
     *         "userLogo": "22222",
     *         "userType": 1,
     *         "realName": "1级代理商",
     *         "superAgentId": "admin",
     *         "proportion": 70.0,
     *         "balanceAll": 0.0,
     *         "free_balance": 0.0,
     *         "balance_geted": 0.0,
     *         "balance": 1000.0,
     *         "boundAreaListStr": "110101000000",
     *         "ban": false
     *       },
     *       "userType": 1
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2019/2/28 上午10:11
     */
    @PostMapping("/getWithdrawal.action")
    public HttpResponse<Object> getWithdrawal(@RequestBody ReqBeanGetWithdrawal mReqBeanGetWithdrawal, @RequestHeader String token) {
        return mIPublicService.getWithdrawal(mReqBeanGetWithdrawal, token);
    }

    /**
     * 申请提现
     *
     * @param token|⚠️请求头携带                 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param price|提现的金额|Double|是
     * @param alipayUserName|支付宝姓名|String|是
     * @param aliaAccount|支付宝账号|String|是
     * @title 申请提现
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
     * @date 2019/2/28 上午10:11
     */
    @PostMapping("/withdrawal.action")
    public HttpResponse<Object> withdrawal(@RequestBody ReqBeanWithdrawal mReqBeanWithdrawal, @RequestHeader String token) {
        return mIPublicService.withdrawal(mReqBeanWithdrawal, token);
    }

}
