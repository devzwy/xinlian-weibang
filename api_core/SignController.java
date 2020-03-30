package com.xinlian.wb.controller;

import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.ISignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * 登录模块接口
 *
 * @author Jason
 * @action /wb/sign
 * @date 2019/10/27 下午1:34
 */
@RestController
@RequestMapping("/sign")
public class SignController {

    @Autowired
    private ISignService userService;

    private Logger logger = LoggerFactory.getLogger(SignController.class);

    /**
     * 注销登录状态
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 退出登录
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
    @PostMapping("/signOut")
    public HttpResponse<Object> signOut( @RequestHeader String token) {
        return userService.signOut(token);
    }


    /**
     * 校验token(客户端选择在合适的时机调用，返回用户最新资料)
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 校验token
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|校验成功时返回用户资料 参数说明参考登录返回|User|否
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
    @PostMapping("/checkUserToken")
    public HttpResponse<Object> checkUserToken(@RequestHeader String token) {
        return userService.checkUserToken(token);
    }

    /**
     * 重置密码
     *
     * @param phoneNumber|电话号码|String|是
     * @param msgCode|验证码|String|是
     * @param password|新密码|String|是
     * @title 重置密码
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
    @PostMapping("/reSetPassword")
    public HttpResponse<Object> reSetPassword(@RequestBody RequestBean_Register mRequestBean_Register) {
        return userService.reSetPassword(mRequestBean_Register);
    }

    /**
     * 用户登录(当登录成功时会在请求头携带用户的token,使用键token获取值,客户端需要保存该值,后续业务接口调用时均需要携带该值)
     *
     * @param signType|登录方式                             0-短信登录   1-密码登录  2-第三方登录|Int|是
     * @param phoneNumber|电话号码|String|当登录方式为短信或密码登录时时必填
     * @param msgCode|验证码|String|当登录方式为短信登录时时必填
     * @param password|密码                               |Int|当登录方式为密码登录时时必填
     * @param openId|第三方授权的唯一标识符                        |String|当登录方式为第三方登录时必填 取值参考openIdType
     * @param openIdType|第三方授权的唯一标识符的类型                 0-微信 1-QQ|Int|注册类型为微信或QQ时必填
     * @param userType|用户类型                             0-安卓  1-苹果  2-小程序 3-web 短信登录时必填|Int|否
     * @title 用户登录
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|登录成功时返回用户资料
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
     * @date 2019/11/19 上午10:15
     */
    @PostMapping("/doSign")
    public HttpResponse<Object> doSign(@RequestBody RequestBean_DoSign requestbeanDosign, HttpServletResponse httpServletResponse) {
        return userService.doSign(requestbeanDosign, httpServletResponse);
    }

    /**
     * 注册
     *
     * @param phoneNumber|电话号码|String|是
     * @param msgCode|验证码|String|是
     * @param password|密码|String|是
     * @param userType|用户身份标示             0-安卓  1-苹果  2-小程序 3-web|Int|是
     * @param registerType|注册类型           0-正常软件注册  1-微信注册  2-QQ注册|Int|是
     * @param openId|openId               注册类型为1或2时必填|String|注册类型为微信或QQ时必填
     * @param boundCountId|绑定的区域ID|Long|否
     * @title 用户注册
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|⚠️：当注册类型为第三方注册时，接口内部会调用一次登录接口，返回参数说明参考登录|User|当注册类型为QQ或微信时是
     * @respBody {
     * "code": 9000,
     * "message": "success",
     * "data": {
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
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:15
     */
    @PostMapping("/register")
    public HttpResponse<Object> register(@RequestBody RequestBean_Register requestBeanRegister, HttpServletResponse httpServletResponse) {
        return userService.register(requestBeanRegister, httpServletResponse);
    }


    /**
     * 获取验证码
     *
     * @param phoneNumber|电话号码|String|是
     * @title 获取验证码
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
    @PostMapping("/getMsgCode")
    public HttpResponse<Object> getMsgCode(@RequestBody RequestBean_GetMsgCode requestbeanGetmsgcode) {
        return userService.getMsgCode(requestbeanGetmsgcode.getPhoneNumber());
    }

}
