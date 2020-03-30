package com.xinlian.wb.controller;


import com.egzosn.pay.spring.boot.core.MerchantPayServiceManager;
import com.xinlian.wb.core.entity.*;
import com.xinlian.wb.core.service.service.IBusunessService;
import com.xinlian.wb.core.service.service.IBusunessService2;
import com.xinlian.wb.jdbc.repositorys.DemandRepository;
import com.xinlian.wb.jdbc.repositorys.OrderCommRepository;
import com.xinlian.wb.jdbc.repositorys.OrderRepository;
import com.xinlian.wb.jdbc.repositorys_web.CountyRepository;
import com.xinlian.wb.jdbc.repositorys_web.RegisterCodeRepository;
import com.xinlian.wb.jdbc.repositorys_web.TownRepository;
import com.xinlian.wb.jdbc.tabs.*;
import com.xinlian.wb.jdbc.tabs_web.County;
import com.xinlian.wb.jdbc.tabs_web.Town;
import com.xinlian.wb.util.Constant;
import io.swagger.annotations.ApiOperation;
import kotlin.jvm.Throws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;


/**
 * 业务模块接口
 *
 * @author Jason
 * @action /wb/business
 * @date 2019/10/24 下午5:55
 */
@RestController
@RequestMapping("/business")
public class BusinessController {
    private Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    private IBusunessService busunessService;

    @Autowired
    private IBusunessService2 busunessService2;

    @Autowired
    private MerchantPayServiceManager payManager;


    @Autowired
    private OrderRepository mOrderRepository;

    @Autowired
    private DemandRepository mDemandRepository;
    /**
     * 注入的 订单评价表操作对象
     */
    @Autowired
    private OrderCommRepository mOrderCommRepository;


    /**
     * 注入的 注册码表操作对象
     */
    @Autowired
    private RegisterCodeRepository mRegisterCodeRepository;


    @Autowired
    private CountyRepository mCountyRepository;


    @Autowired
    private TownRepository mTownRepository;


    /**
     * 技能搜索
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param keyWord|关键字 匹配技能名称和说明|String|是
     * @param lat|经纬度 距离排序字段|Double|否
     * @param lng|经纬度 距离排序字段|Double|否
     * @param page|请求的页数 必须从1开始 默认1|Int|否
     * @param number|请求的条数 默认Int最大值|Int|否
     * @title 技能搜索
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "id": 1466,
     *       "user": {
     *         "userId": "73a8f2d1-7e52-4979-b2d5-6eafad35e946",
     *         "userType": 0,
     *         "phoneNumber": "18174424805",
     *         "userName": "181****4805",
     *         "sex": -1,
     *         "birthday": 1584025075525,
     *         "userLogo": "http://images.weibangqm.com/2020032311451917800679-540-540",
     *         "wyy_accid": "yxaccid_wb_f513695cfebe43899bdc2",
     *         "wyy_token": "yxToken_wb_v2_18174424805",
     *         "indentity": {
     *           "id": 1403,
     *           "idemyityName": "Fn2LhwXoffvfCRiogUCxuVdNA5jdNAWO6cg/1D1FX5GkxHaTLIT4iR+uRtPzLAeWjWGaSyLGuo/yaBtDgPBTvOslDWmGTWBt12wfmmseORsaMerfnCw1LOk1bLzVjrxxSHSFACwHT5jQTa1pu11gu7DPOaDBYKIoGNcVacQnDuO9ESTDDBtOdrGR3TdLV90SaoxplNYx8uzYomgSod18Z3qF4Ps8w38PoDm6Rrvvz3dJvy3lEzcxPbYaw9z8Wh7ieUIBSioQYgfwh6pVFR323eBSqTQzpv/1hoH9nyfF6mgmIQFqjeV5h64DuVdI6KhHNTMBls9BAeN+LngTllatsg==",
     *           "idemyityCardNumber": "WPbHDJN9IXzXg50TIK7YJCwub9OUWi4FTTffBv+gkm+mdryE4op9TpDpaNQY1eecHOsC8eRcjCqPmahnB3AQv/Ml87iOupCSYOTJRvJS5niR03VcreB3qO96H+ThmqkQIRu3Gu0bM/u+cVWushTemoOzFlqUHgOJzJDsA9jdN2twK6pAlQu+/seflvY0KMNe+Xj8NH6Dv1Awb5r2ogKnllk1BZjxtQHXLpn4ke185gWggnxDIVJOk4QB9EOmoFHBuZGLAA1l/ZDTQJkKojDmjtGRQ7+cEd+FHWCeY4WLsTlKG9UiQPdVICb8IilPxsX5PGMyd9stcQWjjTjAz9Fslg=="
     *         },
     *         "createTime": 1584025075525,
     *         "merchanAuthState": 2,
     *         "merchanAuthBean": {
     *           "merchanId": 1404,
     *           "userId": "73a8f2d1-7e52-4979-b2d5-6eafad35e946",
     *           "verifyDec": "",
     *           "verifyState": 2,
     *           "createTime": 1584935967730,
     *           "verifyTime": 1584938872011,
     *           "verifyUserId": "admin",
     *           "merchanTitle": "摩力维修服务",
     *           "merchanUserName": "志",
     *           "merchanPhoneNumber": "18174424805",
     *           "authId": 0,
     *           "merchanPhotos": "http://images.weibangqm.com/2020032311535705402456-595-560",
     *           "merchanBaseTagId": 5,
     *           "serviceTime": "0,1,2,3,4|08:00,18:00",
     *           "city_merchan": "上海市-市辖区-杨浦区",
     *           "addressDetail_Merchan": "杨浦体育中心",
     *           "addressInfo": "专业对口，专业过硬，技术顶级，客户是第一位",
     *           "serverBusinessLicenseType": 1,
     *           "serverBusinessLicenseName": " ",
     *           "serverBusinessLicenseNumber": " ",
     *           "serverBusinessLicenseEndTime": 1624636800000,
     *           "serverBusinessLicensePhotos": "http://images.weibangqm.com/2020032311570076300138-683-960",
     *           "serverPermitLicenseNmae": " ",
     *           "serverPermitLicenseRange": " ",
     *           "serverPermitLicenseEndTime": 1627660800000,
     *           "serverPermitLicensePhotos": "http://images.weibangqm.com/2020032311572481800249-750-637"
     *         },
     *         "wopenIdx": "",
     *         "qopenIdq": "",
     *         "ban": false,
     *         "dispatcher": false
     *       },
     *       "serviceDec": "清洗空调加氟安装上门服务上海清洁清理中央空调拆装移机空调维修",
     *       "imgsUrl": "http://images.weibangqm.com/2020032317304949600156-113-600-600|http://images.weibangqm.com/2020032317304950400247-114-600-576|http://images.weibangqm.com/2020032317304951201459-115-560-600",
     *       "lat": 31.289096,
     *       "lng": 121.621621,
     *       "p_tag": 5,
     *       "s_tag": 71,
     *       "skill_type": 1,
     *       "distance": 9503,
     *       "title": "空调维修",
     *       "autoRegistration": false,
     *       "messageList": [],
     *       "specsList": [
     *         {
     *           "specsId": 1467,
     *           "skillId": 1466,
     *           "specsTitle": "加氟利昂",
     *           "specsPrice": 200.0,
     *           "createTime": 1584955848517,
     *           "createUserId": "73a8f2d1-7e52-4979-b2d5-6eafad35e946",
     *           "unit": "次"
     *         }
     *       ],
     *       "contentImages": "http://images.weibangqm.com/2020032317304939204789-101-750-568|http://images.weibangqm.com/2020032317304941901458-102-750-1171|http://images.weibangqm.com/2020032317304945502456-105-750-667|http://images.weibangqm.com/2020032317304946200178-106-750-760|http://images.weibangqm.com/2020032317304943901367-103-750-804|http://images.weibangqm.com/2020032317304946801579-107-750-543|http://images.weibangqm.com/2020032317304944602478-104-750-720|http://images.weibangqm.com/2020032317304947300268-108-750-578|http://images.weibangqm.com/2020032317304947801578-109-750-708|http://images.weibangqm.com/2020032317304948304689-110-750-488|http://images.weibangqm.com/2020032317304948701678-111-750-434|http://images.weibangqm.com/2020032317304949102457-112-750-871",
     *       "create_time": 1584955848313,
     *       "ptagBean": {
     *         "parentId": 5,
     *         "parentTagName": "家政维修",
     *         "orderByNumber": 1
     *       },
     *       "stagBean": {
     *         "subTagId": 71,
     *         "subTitle": "家电维修",
     *         "suggestPrice": 102.0,
     *         "tagUnit": "次",
     *         "tagImgUrl": "/Images/Skills/71.png",
     *         "orderByNumber": 0,
     *         "hotTag": 1,
     *         "tagIsShow": true
     *       },
     *       "ban": false
     *     },
     *     {
     *       "id": 365,
     *       "user": {
     *         "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *         "userType": 0,
     *         "phoneNumber": "15317026229  ",
     *         "userName": "153****6229",
     *         "sex": 0,
     *         "birthday": 788889600000,
     *         "userLogo": "http://images.weibangqm.com/FuuI3C0Zxh0-vO1U392ofIwjEspd",
     *         "wyy_accid": "yxaccid_wb_a3dcc5d7dd2f4787926fc",
     *         "wyy_token": "yxToken_wb_v2_15317026229",
     *         "indentity": {
     *           "id": 953,
     *           "idemyityName": "fZOK0lQ+qux1/cYfUrtU3N0MKH96W23qm64GlXfnVUci+ZFTwHodhOM0BFxWpo27NHbNKabg+PXw1cPPbvn/FlExcpzJEPYa6Mxf98K21i5DkBWXciDFIVJTnV0/59vCOi0uK85YiA+RxAgFMvOYt2Impam8A9oUVQltKCwKYH1gkmWPAhsytvE7Gfe1hQAsqOP1CSjZ0Gk8pCw/CSTk+hfNxKHErSwASzLZQDkfZDk6d8L2FqVyEi9pfyfmSfn6jJoIt++MwZUV0084oRN+ryErVGaQLmJXQDANUEAm0HawmuXDz1ljRwuU6seH59fTymefvNuh4kcp/fTc+M/GhA==",
     *           "idemyityCardNumber": "isPCNJJ2fd3Xldr8law01YhILbuPIKvvVP85FjnZs1uamlgdoEIpKHBhy34hVeWH9+fsvesnB1+DJhDqxnSxYW/59hLxL81omql71ffPtVPIFuDy33MCoet88im3X6/e9iqbcHrk7gx3LYI0mELbIDLoiYbUICDln5+bYB70zM2yI5jy2Kz8prw+d1oes6i+P7v+W8rbsd6n4WsEkNpANDt1yfpiVqw5TV2TyHmYxTYwnxWmN8n5QJ2MKqDqsv/MdwXDUBhTbQDvWjwQAFpNboBJWQVkStBuL8p7w+tF13Q0OEstkeRSBDqpI0n12Rr3ibfIHKl4bw9ns0C8bxMAKw=="
     *         },
     *         "createTime": 1582166845102,
     *         "merchanAuthState": 2,
     *         "merchanAuthBean": {
     *           "merchanId": 954,
     *           "userId": "eec21274-1a09-4729-a941-88215e279bd5",
     *           "verifyDec": "",
     *           "verifyState": 2,
     *           "createTime": 1584077848926,
     *           "merchanTitle": "阳光家政服务",
     *           "merchanUserName": "花",
     *           "merchanPhoneNumber": "181",
     *           "authId": 953,
     *           "merchanPhotos": "http://images.weibangqm.com/2020031313345331700356-600-600",
     *           "merchanBaseTagId": 5,
     *           "serviceTime": "0,1,2,3,4,5,6|01:00,12:00",
     *           "city_merchan": "上海市-市辖区-黄埔区",
     *           "addressDetail_Merchan": "呼呼",
     *           "addressInfo": "客户是上帝",
     *           "serverBusinessLicenseType": 0,
     *           "serverBusinessLicenseName": "  ",
     *           "serverBusinessLicenseNumber": "  ",
     *           "serverBusinessLicenseEndTime": 1608134400000,
     *           "serverBusinessLicensePhotos": "http://images.weibangqm.com/2020031313362585501378-600-600",
     *           "serverPermitLicenseNmae": "  ",
     *           "serverPermitLicenseRange": "  ",
     *           "serverPermitLicenseEndTime": 1610553600000,
     *           "serverPermitLicensePhotos": "http://images.weibangqm.com/2020031313370489800467-449-393"
     *         },
     *         "wopenIdx": "",
     *         "qopenIdq": "",
     *         "ban": false,
     *         "dispatcher": true
     *       },
     *       "serviceDec": "专业清洗和维修各种油烟机，集成灶，宾馆饭店油烟机，清洗维修各种洗衣机，热水器，各种家用空调和商用中央空调。从业多年，经验丰富，手工深度拆卸清洗。清洗细致彻底。诚信服务，价格合理",
     *       "imgsUrl": "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4072923099,3311824917&fm=26&gp=0.jpg|http://images.weibangqm.com/2020031616451931100456-105-680-510|http://images.weibangqm.com/2020031616451931800239-106-400-300",
     *       "lat": 31.289107,
     *       "lng": 121.621665,
     *       "p_tag": 5,
     *       "s_tag": 67,
     *       "skill_type": 1,
     *       "distance": 9508,
     *       "title": "全套保洁",
     *       "autoRegistration": false,
     *       "messageList": [],
     *       "specsList": [
     *         {
     *           "specsId": 366,
     *           "skillId": 365,
     *           "specsTitle": "全套保洁",
     *           "specsPrice": 20.0,
     *           "createTime": 1582713581137,
     *           "createUserId": "eec21274-1a09-4729-a941-88215e279bd5",
     *           "unit": "平方"
     *         }
     *       ],
     *       "contentImages": "http://images.weibangqm.com/2020031313382192900149-101-750-780|http://images.weibangqm.com/2020031313382193400258-102-750-862|http://images.weibangqm.com/2020031313382194000347-103-750-1088",
     *       "create_time": 1584348321191,
     *       "ptagBean": {
     *         "parentId": 5,
     *         "parentTagName": "家政维修",
     *         "orderByNumber": 1
     *       },
     *       "stagBean": {
     *         "subTagId": 67,
     *         "subTitle": "保洁",
     *         "suggestPrice": 30.0,
     *         "tagUnit": "小时",
     *         "tagImgUrl": "/Images/Skills/67.png",
     *         "orderByNumber": 0,
     *         "hotTag": 1,
     *         "tagIsShow": true
     *       },
     *       "ban": false
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2020/3/28 下午15:00
     */
    @PostMapping("/searchSkills")
    public HttpResponse<Object> searchSkills(@RequestBody ReqBeanSearchSkills mReqBeanSearchSkills,@RequestHeader String token) {
        return busunessService2.searchSkills( mReqBeanSearchSkills,token);
    }

    /**
     * 计算价格（同城配送与跑腿代办）
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param bType|同城传0 跑腿传1|Int|是
     * @param businessAddressId|同城传取货地址ID  跑腿传购买地址ID(跑腿代办可选)|Long|否
     * @param addresId|收获地址ID|Long|是
     * @param bTime|送达时间戳 尽快送传-1|Long|是
     * @title 计算价格（同城配送与跑腿代办）
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": {
     *     "price": 37.8,
     *     "distance": "8.4公里"
     *   }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/1/6 下午15:00
     */
    @PostMapping("/calculatePrice")
    public HttpResponse<Object> calculatePrice(@RequestBody CPrice mCPrice,@RequestHeader String token) {
        return busunessService2.calculatePrice( mCPrice,token);
    }
    /**
     * 获取商户列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取商户列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "merchanId": 48,
     *       "userId": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     *       "verifyDec": "测试",
     *       "verifyState": 2,
     *       "createTime": 1575259879280,
     *       "verifyTime": 1575270013195,
     *       "verifyUserId": "admin",
     *       "merchanTitle": "TestTitle",
     *       "merchanUserName": "Jason",
     *       "merchanPhoneNumber": "1888888888",
     *       "authId": 40,
     *       "merchanPhotos": "aaaaa|bbbb",
     *       "merchanBaseTagId": 5,
     *       "serviceTime": "lll",
     *       "city_merchan": "TestCity222222",
     *       "addressDetail_Merchan": "aaaaaa",
     *       "addressInfo": "testInfo",
     *       "serverBusinessLicenseType": 0,
     *       "serverBusinessLicenseName": "testName",
     *       "serverBusinessLicenseNumber": "111111",
     *       "serverBusinessLicenseEndTime": 10000111,
     *       "serverBusinessLicensePhotos": "aaa",
     *       "serverPermitLicenseNmae": "name",
     *       "serverPermitLicenseRange": "aaaaa",
     *       "serverPermitLicenseEndTime": 111,
     *       "serverPermitLicensePhotos": "ccccc"
     *     }
     *   ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/16 下午15:00
     */
    @PostMapping("/getMerchantList")
    public HttpResponse<Object> getMerchantList(@RequestHeader String token,@RequestBody ReqBeanGetAllMerchantList mReqBeanGetAllMerchantList) {
        return busunessService2.getMerchantList( token,mReqBeanGetAllMerchantList);
    }

    @PostConstruct
    private void init() {
        //防止用户订单的过期时间正好在服务器重启过程中，无法进入redis过期的回调，导致订单状态卡在1的情况
        logger.info("自动任务容错检查 ->> 订单表是否有过期订单...");
        logger.info("自动任务容错检查 ->> 订单表是否有超时未评价订单...");
        List<WBOrder> list = null;
        try {
            list = mOrderRepository.findAll();
            list.forEach(wbOrder -> {
                if (new Date().getTime() - wbOrder.getOrderCreateTime() >= (Constant.finalParams.ORDER_EXPIRETIME*1000) && wbOrder.getState() == 1) {
                    wbOrder.setState(-1);
                    mOrderRepository.saveAndFlush(wbOrder);
                    logger.info("自动取消订单" + wbOrder.getOrderId());
                }

                if (wbOrder.getCompilerOrderTime() != null && new Date().getTime() - wbOrder.getCompilerOrderTime() >= (Constant.finalParams.ORDER_COMM_EXPIRETIME*1000) && wbOrder.getState() == 3) {
                    wbOrder.setState(4);
                    logger.info("自动好评订单：" + wbOrder.getOrderId());
                    OrderCommTab commBean = mOrderCommRepository.findTop1ByOrderId(wbOrder.getOrderId());
                    if (commBean == null) {
                        commBean = new OrderCommTab(null, wbOrder.getOrderId(), null, null, null, null, 0, 0, null, Long.parseLong(wbOrder.getOrderBusinessId()), wbOrder.getUserIdFromBuy(), null);
                    }

                    if (commBean.getCommTimeFormBuy() == null) {
                        //买方未评
                        commBean.setCommTimeFormBuy(new Date().getTime());
                        commBean.setCommStarFromBuy(5);
                        commBean.setBuyCommContent("订单超时未评价，系统自动好评");
                        mOrderCommRepository.saveAndFlush(commBean);
                        logger.info("买家未评，自动好评");
                    }
                    if (commBean.getCommTimeFormServer() == null) {
                        //买方未评
                        commBean.setCommTimeFormServer(new Date().getTime());
                        commBean.setCommStarFromServer(5);
                        commBean.setServerCommContent("订单超时未评价，系统自动好评");
                        mOrderCommRepository.saveAndFlush(commBean);
                        logger.info("卖家未评，自动好评");
                    }
                    wbOrder.setCommId(mOrderCommRepository.findTop1ByOrderId(wbOrder.getOrderId()).getId());
                    mOrderRepository.save(wbOrder);
                    logger.info("订单自动好评成功");
                    mOrderRepository.saveAndFlush(wbOrder);
                }
            });
        } catch (Exception e) {
        }


        logger.info("自动任务容错检查 ->> 注册码表是否有过期未删除的注册码...");
        try {
            mRegisterCodeRepository.findAll().forEach(registerCode -> {
                if (new Date().getTime() - registerCode.getCreatTime() >= Constant.finalParams.REGISTER_CODE_EXPIRETIME*1000) {
                    if (registerCode.getRegisterCodeType() == 0) {
                        //一级代理商
                        String[] a = registerCode.getBoundAreaListSrt().split(",");
                        for (int i = 0; i < a.length; i++) {
                            //根据绑定的区 打开标记
                            if (!a[i].isEmpty()) {
                                County bb = mCountyRepository.findTop1ByCountyId(Long.valueOf(a[i]));
                                bb.setAgent(false);
                                mCountyRepository.saveAndFlush(bb);
                                logger.info("打开" + bb + "区域的已代理标记");
                            }
                        }
                    } else if (registerCode.getRegisterCodeType() == 1) {
                        //二级代理商
                        //根据绑定的区 打开标记
                        String[] a = registerCode.getBoundAreaListSrt().split(",");
                        for (int i = 0; i < a.length; i++) {
                            //根据绑定的区 打开标记
                            if (!a[i].isEmpty()) {
                                Town cc = mTownRepository.findTop1ByTownId(Long.valueOf(a[i]));
                                cc.setAgent(false);
                                mTownRepository.saveAndFlush(cc);
                                logger.info("打开" + cc + "区域的已代理标记");
                            }
                        }
                    }
                    mRegisterCodeRepository.delete(registerCode);
                    logger.info("删除过期的注册码:" + registerCode.getRegisterId());
                }
            });
        } catch (Exception e) {
        }

        logger.info("自动任务容错检查 ->> 需求表是否有过期的需求...");
        mDemandRepository.findAll().forEach(demand -> {
            if (demand.getExpiryDate()==0 && new Date().getTime() >= (demand.getCreateTime()+(86400*1000)) && demand.getDemandState()!=-2){
                demand.setDemandState(-2);
                logger.info("标注过期的需求记录 -> "+demand);
                mDemandRepository.saveAndFlush(demand);
            }
            if (demand.getExpiryDate()==1 && new Date().getTime() >= (demand.getCreateTime()+(604800*1000))&& demand.getDemandState()!=-2){
                logger.info("标注过期的需求记录 -> "+demand);
                demand.setDemandState(-2);
                mDemandRepository.saveAndFlush(demand);
            }
        });

        //MSG_CODE_15371026229_1576746584645
        //获取redis'中所有验证码信息
    }

    /**
     * 获取七牛云上传token
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取图片上传token  1
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": "nEydSRyh_lSqvstx0RSnBP-o4KzCDa_WOHEIFae_:yOKANpvNpLWEjfJ9ZanSZUPeIww=:eyJzY29wZSI6InNoY3lhcHAiLCJkZWFkbGluZSI6MTU3NTYyNTE4OH0="
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/6 上午10:28
     */
    @PostMapping("/getQNToken")
    public HttpResponse<Object> getQNToken() {
        return busunessService2.getQNToken();
    }
    /**
     * 跑腿代办服务方取货接口
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @title 已取货(跑腿)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": -1,
     *   "message": "Success",
     *   "data": "操作成功"
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/23 上午10:28
     */
    @PostMapping("/takeDelivery")
    public HttpResponse<Object> takeDelivery(@RequestBody WBOrder orderId,@RequestHeader String token) {
        return busunessService2.takeDelivery( orderId,token);
    }
    /**Take delivery
     * 抢单(跑腿代办,同城配送)
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @title 抢单(跑腿代办,同城配送)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": -1,
     *   "message": "订单已被抢走，下次要抓紧哦",
     *   "data": null
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/23 上午10:28
     */
    @PostMapping("/grabUserRunErrandSheet")
    public HttpResponse<Object> grabUserRunErrandSheet(@RequestBody WBOrder orderId,@RequestHeader String token) {
        return busunessService2.grabUserRunErrandSheet( orderId,token);
    }

    /**
     * 获取订单详情
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @title 获取订单详情
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @resqParam orderId|订单编号|String|是
     * @resqParam orderType|生成的订单类型 0 - 技能订单(即技能下单) 1 - 跑腿代办 2 - 同城配送|int|是
     * @resqParam orderBusinessId|订单类型对应的有误ID，比如为0时传入的为技能ID 1为跑腿代办ID 2为同城配送ID|String|是
     * @resqParam state|订单状态码 0 默认状态（未产生交易方/被交易方） 1 待付款  -1 已取消 2 已支付  -2 支付失败 3 订单完成 4 待服务(跑腿代办、同城配送) 5已取待送达(跑腿代办、同城配送)  6 已评价(双方已评)|Int|是
     * @resqParam number|购买的数量|Int|是
     * @resqParam orderDec|订单说明备注信息|String|是
     * @resqParam price|订单金额|Double|是
     * @resqParam orderCreateTime|订单创建时间|Long|是
     * @resqParam userFromBuy|买方信息|User|否
     * @resqParam address|服务地址(跑腿代办和同城配送中为收获地址)|String|是
     * @resqParam orderPayTime|订单支付时间|Long|否
     * @resqParam userFromServer|卖方信息|User|是
     * @resqParam getOrderTime|抢单时间|Long|否
     * @resqParam takeDeliveryTime|取货时间 同城配送 跑腿代办有值|Long|否
     * @resqParam transactionCode|取货码 同城配送 跑腿代办有值|Int|是
     * @resqParam orderBusinessBean|业务实体 根据订单类型返回|String|是
     * @resqParam commed|当前用户是否已经评价|boo|是
     * @resqParam payment|支付方式 0 余额支付  1-支付宝支付 2-微信支付|Int|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": {
     *     "orderId": "wb-1575524883993",
     *     "orderType": 1,
     *     "orderBusinessId": "178",
     *     "state": -1,
     *     "number": 1,
     *     "orderDec": "跑腿代办备注信息为空",
     *     "price": 1000.0,
     *     "userIdFromBuy": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     *     "userIdFromServer": "",
     *     "orderCreateTime": 1575524883993,
     *     "userFromBuy": {
     *       "userId": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     *       "userType": 0,
     *       "phoneNumber": "15317026229",
     *       "userName": "153****6229",
     *       "sex": -1,
     *       "birthday": 1575013752864,
     *       "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     *       "wyy_accid": "yxaccid_wb_b049bc4ce90d403cad074",
     *       "wyy_token": "yxToken_wb_v2_15317026229",
     *       "indentity": {
     *         "id": 40,
     *         "idemyityName": "王利香",
     *         "idemyityCardNumber": "430223199810138025"
     *       },
     *       "createTime": 1575013752864,
     *       "merchanAuthState": 0,
     *       "wopenIdx": "",
     *       "qopenIdq": "",
     *       "ban": false
     *     },
     *     "address": {
     *       "id": 73,
     *       "phoneNumber": "15317026229",
     *       "address_head": "陕西省西安市2222",
     *       "address_end": "长安区62222222",
     *       "name": "张四",
     *       "sex": "女",
     *       "tag": "家"
     *     },
     *     "addPrice": 0.0,
     *     "stime": -1,
     *     "saddressId": 73
     *   }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/5 上午10:28
     */
    @PostMapping("/getOrderDetail")
    public HttpResponse<Object> getOrderDetail(@RequestBody WBOrder orderId,@RequestHeader String token) {
        return busunessService2.getOrderDetail( orderId,token);
    }


    /**
     * 获取全部同城、跑腿列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param type|筛选字段 0 - 跑腿代办 1 - 同城配送 2 - 全部 |Int|是
     * @param page|请求的页数 必须从1开始 默认1|Int|否
     * @param number|请求的条数 默认Int最大值|Int|否
     * @title 获取全部同城、跑腿列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @resqParam bType|类型 0 - 跑腿代办  1 - 同城配送|Int|是
     * @resqParam tradeNames|交易名称|String|是
     * @resqParam tempoDiServizio|送达时间 -1表示尽快送|Long|是
     * @resqParam businessAddressId|跑腿代办中为购买地址ID  同城配送传取货地址ID 同城配送时一定有值|Long|否
     * @resqParam businessAddressBean|跑腿代办中为购买地址实体  同城配送传取货地址实体 同城配送时一定有值|Address|是
     * @resqParam addresId|同城配送收获地址ID|Long|是
     * @resqParam addresBean|同城配送收获地址实体|Address|是
     * @resqParam user|发单用户实体|User|是
     * @resqParam sendLat|该订单的发单经纬度|Float|是
     * @resqParam sendLng|该订单的发单经纬度|Float|是
     * @resqParam price|配送费|Float|是
     * @resqParam decInfo|备注信息|String|否
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "id": 49,
     *       "userId": "100f94d6-b937-4567-947c-4e149942fede",
     *       "tradeNames": "用户1发布的腿代办测试订单",
     *       "tempoDiServizio": -1,
     *       "sendLat": 100.0,
     *       "sendLng": 100.0,
     *       "addresId": 48,
     *       "price": 12.0,
     *       "decInfo": "用户1发布的腿代办测试订单",
     *       "addresBean": {
     *         "id": 48,
     *         "phoneNumber": "18020011121",
     *         "address_head": "天水市",
     *         "address_end": "天水市",
     *         "name": "赵",
     *         "sex": "1",
     *         "tag": "家",
     *         "addressLat": 105.76349,
     *         "addressLng": 34.615857,
     *         "defaultAddress": false
     *       },
     *       "createTime": 0,
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
     *         "boundCountId": 110101000000,
     *         "createTime": 1581863667616,
     *         "merchanAuthState": 0,
     *         "wopenIdx": "",
     *         "qopenIdq": "",
     *         "ban": false,
     *         "dispatcher": true
     *       },
     *       "btype": 0
     *     },
     *     {
     *       "id": 59,
     *       "intraType": 0,
     *       "userId": "100f94d6-b937-4567-947c-4e149942fede",
     *       "tradeNames": "用户1发布的同城配送测试订单",
     *       "tempoDiServizio": -1,
     *       "businessAddressId": 58,
     *       "sendLat": 100.0,
     *       "sendLng": 100.0,
     *       "addresId": 48,
     *       "price": 3470.0,
     *       "decInfo": "用户1发布的同城配送测试订单",
     *       "businessAddressBean": {
     *         "id": 58,
     *         "phoneNumber": "18020011121",
     *         "address_head": "宝达城",
     *         "address_end": "宝达城",
     *         "name": "赵",
     *         "sex": "1",
     *         "tag": "家",
     *         "addressLat": 121.258757,
     *         "addressLng": 31.043526,
     *         "defaultAddress": false
     *       },
     *       "addresBean": {
     *         "id": 48,
     *         "phoneNumber": "18020011121",
     *         "address_head": "天水市",
     *         "address_end": "天水市",
     *         "name": "赵",
     *         "sex": "1",
     *         "tag": "家",
     *         "addressLat": 105.76349,
     *         "addressLng": 34.615857,
     *         "defaultAddress": false
     *       },
     *       "createTime": 0,
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
     *         "boundCountId": 110101000000,
     *         "createTime": 1581863667616,
     *         "merchanAuthState": 0,
     *         "wopenIdx": "",
     *         "qopenIdq": "",
     *         "ban": false,
     *         "dispatcher": true
     *       },
     *       "btype": 1
     *     }
     *   ]
     * }
     *
     * @requestType post
     * @author Jason
     * @date 2020/2/18 上午10:28
     */
    @PostMapping("/getAllRunErrand")
    public HttpResponse<Object> getAllRunErrand(@RequestBody Req_GetAllRun mReq_GetAllRun,@RequestHeader String token) {
        return busunessService2.getAllRunErrand(mReq_GetAllRun,token);
    }

    /**
     * 发布跑腿代办和同城配送订单
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param tradeNames|商品名称|String|是
     * @param bType|类型 0 - 跑腿代办  1 - 同城配送|Int|是
     * @param tempoDiServizio|送达时间 -1表示尽快送|Long|是
     * @param businessAddressId|跑腿代办中传购买地址ID  同城配送传取货地址ID 同城配送时不能为空|Long|否
     * @param addresId|收获地址ID|Long|是
     * @param sendLat|当前位置经纬度|Float|是
     * @param sendLng|当前位置经纬度|Float|是
     * @param price|配送费|Float|是
     * @param decInfo|备注信息|String|否
     * @param intraType|同城配送的订单类型 0-帮我取 1-帮我送|Int|是
     * @title 发布跑腿代办和同城配送订单
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": {
     *     "orderId": "wb-1575524883993",
     *     "orderType": 1,
     *     "orderBusinessId": "178",
     *     "state": 1,
     *     "number": 1,
     *     "orderDec": "跑腿代办备注信息为空",
     *     "price": 1000.0,
     *     "userIdFromBuy": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     *     "userIdFromServer": "",
     *     "orderCreateTime": 1575524883993,
     *     "addPrice": 0.0,
     *     "stime": -1,
     *     "saddressId": 73
     *   }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/23 上午10:28
     */
    @PostMapping("/createRunErrand")
    public HttpResponse<Object> createRunErrand(@RequestBody UserRunErrand mUserRunErrand,@RequestHeader String token) {
        return busunessService2.createRunErrand( mUserRunErrand,token);
    }

    /**
     * 获取跑腿代办标签
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取跑腿代办标签
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|二级列表用逗号分开|Any|是
     * @respBody {
     *   "code": 9000,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "id": 1,
     *       "title": "买香烟",
     *       "subListStr": "玉溪软装,万宝路,硬中华,红双喜,利群,中南海,绿色爱喜,一包"
     *     },
     *     {
     *       "id": 2,
     *       "title": "买饮料",
     *       "subListStr": "脉动,冰红茶,养乐多,可乐,矿泉水,红牛,王老吉,一瓶"
     *     },
     *     {
     *       "id": 3,
     *       "title": "撸串儿",
     *       "subListStr": "羊肉串,脆骨,板筋,鸡胗,鸡翅,烤大蒜,烤韭菜,啤酒"
     *     },
     *     {
     *       "id": 4,
     *       "title": "买早餐",
     *       "subListStr": "茶叶蛋,豆腐脑,馄饨,油条,包子,煎饼果子,豆浆,蒸饺"
     *     },
     *     {
     *       "id": 5,
     *       "title": "买菜",
     *       "subListStr": "牛肉,鸡翅,黄瓜,西红柿,豆角,茄子,鸡蛋,葱姜蒜"
     *     },
     *     {
     *       "id": 6,
     *       "title": "KFC",
     *       "subListStr": "外带全家桶,圣代冰激凌,辣翅,汉堡,薯条,可乐,1份,2份"
     *     },
     *     {
     *       "id": 7,
     *       "title": "星巴克",
     *       "subListStr": "摩卡星冰乐,拿铁,香草星冰乐,大杯,中杯,焦糖玛奇朵,摩卡,卡布奇诺"
     *     },
     *     {
     *       "id": 8,
     *       "title": "买药",
     *       "subListStr": "皮炎平,体温计,阿莫西林,毓婷,验孕棒,云南白药,散利痛,一盒"
     *     },
     *     {
     *       "id": 9,
     *       "title": "超市代购",
     *       "subListStr": "家乐福,杜蕾斯,冈本003,垃圾袋,吹风机,电蚊香,卫生纸,一包"
     *     },
     *     {
     *       "id": 10,
     *       "title": "买水果",
     *       "subListStr": "冰西瓜,榴莲,芒果,车厘子,进口水果,哈密瓜,水蜜桃,一斤"
     *     },
     *     {
     *       "id": 11,
     *       "title": "买姨妈巾",
     *       "subListStr": "ABC,护舒宝,苏菲,七度空间,乐而雅,高洁丝,恩芝,一包"
     *     },
     *     {
     *       "id": 12,
     *       "title": "其他",
     *       "subListStr": ""
     *     }
     *   ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/5 上午10:28
     */
    @PostMapping("/getRunErrandsTags")
    public HttpResponse<Object> getRunErrandsTags(@RequestHeader String token) {
        return busunessService2.getRunErrandsTags( token);
    }
    /**
     * 商户详情
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userId|用户ID   查询自己的商户信息该字段可以为空|Long|否
     * @title 商户详情
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源参考商户认证上传字段|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "merchanId": 48,
     * "userId": "62d6a3a2-9371-48f2-bf20-1133debf124c",
     * "verifyDec": "测试",
     * "verifyState": 1,
     * "createTime": 1575259879280,
     * "verifyTime": 1575264420406,
     * "verifyUserId": "admin",
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
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/2 上午10:28
     */
    @PostMapping("/getMerchantDetail")
    public HttpResponse<Object> getMerchantDetail(@RequestBody MerchanAuthTab mMerchanAuthTab, @RequestHeader String token) {
        return busunessService2.getMerchantDetail(mMerchanAuthTab, token);
    }

    /**
     * 商户认证/更新
     *
     * @param token|⚠️请求头携带                                       由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param merchanId|商户资料编号，修改时必须传，不传会重新创建新商户导致异常|Long|否
     * @param merchanTitle|店铺名称|String|是
     * @param merchanUserName|联系人姓名|String|是
     * @param merchanPhoneNumber|联系人电话|String|是
     * @param idemyityName|实名的姓名|String|是
     * @param idemyityCardNumber|                                 实名的身份证号|String|是
     * @param merchanPhotos|店铺门头照Url                              多个用'竖杠'隔开|String|是
     * @param merchanBaseTagId|店铺服务品类                             标签表最外层的标签ID eg：家政服务(4)、社区服务(5) 传入对应ID|Long|是
     * @param serviceTime|服务时间，周一到周日                              用0..6表示 ，比如用户选择周一到周五，上传参数事例：0,1,2,3,4'竖杠'开始营业时间戳,结束营业时间戳|String|是
     * @param city_merchan|店铺地址                                   - 城市|String|是
     * @param addressDetail_Merchan|店铺地址                          - 详细地址|String|是
     * @param addressInfo|店铺介绍|String|是
     * @param serverBusinessLicenseType|营业执照类型                    0-有限责任公司  1 - 个体工商户|Int|是
     * @param serverBusinessLicenseName|营业执照名称|String|是
     * @param serverBusinessLicenseNumber|营业执照社会信用代码或注册号|String|是
     * @param serverBusinessLicenseEndTime|营业执照有效期截止时间            永久期限传-1|Long|是
     * @param serverBusinessLicensePhotos|营业执照照片                  多个用'竖杠'隔开|String|是
     * @param serverPermitLicenseNmae|许可证名称|String|是
     * @param serverPermitLicenseRange|许可证经营范围|String|是
     * @param serverPermitLicenseEndTime|许可证有效期截止时间               永久期限传-1|Long|是
     * @param serverPermitLicensePhotos|安全生产许可证照片                 多个用'竖杠'隔开|Long|是
     * @title 商户认证/更新
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data":""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/12/2 上午10:28
     */
    @PostMapping("/merchantAuth")
    public HttpResponse<Object> merchantAuth(@RequestBody MerchanAuthTab mMerchanAuthTab, @RequestHeader String token) {
        return busunessService2.merchantAuth(mMerchanAuthTab, token);
    }


    /**
     * 订单评价
     *
     * @param token|⚠️请求头携带         由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @param content|评价内容|String|是
     * @param commStar|评价星级|Int|是
     * @title 订单评价
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效 -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|Any|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data":""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/22 下午1:41
     */
    @PostMapping("/orderComm")
    public HttpResponse<Object> orderComm(@RequestBody RequestBean_OrderComm mRequestBean_OrderComm, @RequestHeader String token) {
        return busunessService.orderComm(mRequestBean_OrderComm, token);
    }

    /**
     * 完成订单
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @param tCode|收获码 跑腿代办必填|Int|否
     * @title 完成订单
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/23 上午1:41
     */
    @PostMapping("/finishOrder")
    public HttpResponse<Object> finishOrder(@RequestBody RequestBean_CancleOrder mRequestBean_CancleOrder, @RequestHeader String token) {
        return busunessService.finishOrder(mRequestBean_CancleOrder, token);
    }

    /**
     * 取消订单
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号|String|是
     * @title 取消订单
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/21 上午3:41
     */
    @PostMapping("/cancleOrder")
    public HttpResponse<Object> cancleOrder(@RequestBody RequestBean_CancleOrder mRequestBean_CancleOrder, @RequestHeader String token) {
        return busunessService.cancleOrder(mRequestBean_CancleOrder, token);
    }

    /**
     * 更新用户地理位置和推送的DeviceId,字段可以为空,为空时不更新
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 更新用户地理位置和token
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/updataUserLocationAndDeviceId")
    public HttpResponse<Object> updataUserLocationAndDeviceId(@RequestBody UserLocationAndDeviceIdTab reqBean, @RequestHeader String token) {
        return busunessService.updataUserLocationAndDeviceId(reqBean, token);
    }

    /**
     * 获取当前用户的订单列表
     *
     * @param token|请求头携带|String|⚠️由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送
     * @param state|状态筛选 -1 获取全部订单 1-待支付 2-待接单 4-待服务 3-待评价|Int|是
     * @param userId|用用户ID过滤|String|否
     * @param page|请求的页数|Int|否
     * @param number|请求的条数|Int|否
     * @param buyOrServers|userId不为空时必传 0 - 买方订单  1 - 卖方订单|Int|否
     * @title 获取订单列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 参考订单详情接口与登录接口|Order、User|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "orderId": "wb-1574146101755",
     * "orderType": 0,
     * "orderBusinessId": "380",
     * "state": 1,
     * "number": 1,
     * "orderDec": "11111",
     * "price": 100.0,
     * "userIdFromBuy": "admin",
     * "userIdFromServer": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "orderCreateTime": 1574146101755,
     * "userFromServer": {
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
     * "address": {
     * "id": 413,
     * "phoneNumber": "15317026229",
     * "address_head": "陕西省西安市2222",
     * "address_end": "长安区62222222",
     * "name": "张四",
     * "sex": "女",
     * "tag": "家"
     * },
     * "saddressId": 413,
     * "stime": 1111111
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getOrderList")
    public HttpResponse<Object> getOrderList(@RequestBody Req_GetMyOrderList mReq_GetMyOrderList,@RequestHeader String token) {
        return busunessService.getOrderList(mReq_GetMyOrderList,token);
    }


    /**
     * 支付回调地址
     */
    @RequestMapping(value = "/payBack{detailsId}.json")
    @Throws(exceptionClasses = IOException.class)
    private String payBack(HttpServletRequest request, @PathVariable String detailsId) throws IOException {
        logger.info("Enter pay back,detailsId:"+detailsId);
        return payManager.payBack(detailsId, request.getParameterMap(), request.getInputStream());
    }

    /**
     * 订单支付
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderId|订单号   |String|是
     * @param payType|支付方式  0 余额 1 微信 2 支付宝|Int|是
     * @param price|付款金额    单价*数量|String|是
     * @title 订单支付
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|余额支付时data为空，支付宝支付时获取alipayStr参数的值掉起支付，微信支付时获取weChatPayResult的对象掉起支付|PayBean|否
     * @resqParam 以下为PayBean实体的参数说明|以下为PayBean实体的参数说明|以下为PayBean实体的参数说明|以下为PayBean实体的参数说明
     * @resqParam alipayStr|支付宝支付字符串|String|支付方式为支付宝时出现
     * @resqParam weChatPayResult|状态说明|WeChatPayResult|支付方式为微信时出现
     * @resqParam 以下为WeChatPayResult实体的参数说明|以下为WeChatPayResult实体的参数说明|以下为WeChatPayResult实体的参数说明|以下为WeChatPayResult实体的参数说明
     * @resqParam appid|微信支付使用参数 详细说明查阅微信支付文档|String|是
     * @resqParam noncestr|微信支付使用参数 详细说明查阅微信支付文档|String|是
     * @resqParam package|微信支付使用参数 详细说明查阅微信支付文档|String| 是
     * @resqParam partnerid|微信支付使用参数 详细说明查阅微信支付文档|String| 是
     * @resqParam prepayid|微信支付使用参数 详细说明查阅微信支付文档|String| 是
     * @resqParam sign|微信支付使用参数 详细说明查阅微信支付文档|String| 是
     * @resqParam timestamp|微信支付使用参数 详细说明查阅微信支付文档|String| 是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "alipayStr": null,
     * "weChatPayResult": {
     * "appid": "wxd33a062ce8eca8b0",
     * "noncestr": "WwSeR6BqJ2N8uj9X",
     * "package": "Sign=WXPay",
     * "partnerid": "1556940831",
     * "prepayid": "wx1915004347631916d0bcf6161099038800",
     * "sign": "B6A10ECF33E6E0DD74DC570FD1504DC6",
     * "timestamp": 1574146843
     * }
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/pay")
    public HttpResponse<Object> pay(@RequestBody RequestBean_Pay mRequestBean_Pay, @RequestHeader String token) {
        return busunessService.pay(mRequestBean_Pay, token);
    }

    /**
     * 查询用户余额
     *
     * @param token|请求头携带|String|⚠️由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送
     * @title 查询用户余额
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 |BalanceBean|是
     * @resqParam 以下为BalanceBean实体的参数说明|以下为BalanceBean实体的参数说明|以下为BalanceBean实体的参数说明|以下为BalanceBean实体的参数说明
     * @resqParam userId|用户ID |String|是
     * @resqParam balance|余额 |Double|是
     * @resqParam freeBalance|冻结金额 |Double|是
     * @resqParam allFromPrice|总收入 |Double|是
     * @resqParam setPsw|是否设置了支付密码 |Boolean|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "userId": "admin",
     * "balance": 999900.0,
     * "freeBalance": 0.0,
     * "allFromPrice": 0.0,
     * "setPsw":false
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/getBalance")
    public HttpResponse<Object> getBalance(@RequestHeader String token) {
        return busunessService.getBalance(token);
    }


    /**
     * 生成订单
     *
     * @param token|⚠️请求头携带                 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param orderType|生成的订单类型             0-技能订单(即技能下单) |Int|是
     * @param orderBusinessId|订单类型对应的ID     技能下单时传技能ID|String|是
     * @param number|购买的数量                  默认为1|String|是
     * @param sTime|服务时间                    时间戳|String|是
     * @param sAddressId|服务地址的索引ID|String|是
     * @param orderDec|订单备注信息|String|是
     * @param price|价格                      订单单价|String|是
     * @param specsId|规格ID|Long|是
     * @param buyFromDemand|(购买需求必传)当前订单的购买方是否为需求发布方|Boolean|否
     * @param demandId|(购买需求必传)当buyFromDemand为true时有值 传 需求ID|Long|否
     * @title 生成订单(新)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 参考订单详情接口|Order|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "orderId": "wb-1574146174026",
     * "orderType": 0,
     * "orderBusinessId": "380",
     * "state": 1,
     * "number": 1,
     * "orderDec": "111222211",
     * "price": 100.0,
     * "userIdFromBuy": "admin",
     * "userIdFromServer": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "orderCreateTime": 1574146174026,
     * "saddressId": 413,
     * "stime": 1111111
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/29 下午4:27
     */
    @PostMapping("/createOrder")
    public HttpResponse<Object> createOrder(@RequestBody WBOrder mRequestBean_CreateOrder, @RequestHeader String token) {
        return busunessService.createOrder(mRequestBean_CreateOrder, token);
    }

    /**
     * 技能留言
     *
     * @param token|⚠️请求头携带      由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param content|留言内容       |String|是
     * @param parentId|上条留言的ID   如果为回复留言时必填 给技能添加留言时无需填写|Int|否
     * @param skillId|技能ID|Int|是
     * @title 技能留言
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/addSkillMessage")
    public HttpResponse<Object> addSkillMessage(@RequestBody RequestBean_AddSkillMessage mRequestBean_AddSkillMessage, @RequestHeader String token) {
        return busunessService.addSkillMessage(mRequestBean_AddSkillMessage, token);
    }

    /**
     * 收藏取消收藏技能
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param skillId|技能ID  |Int|是
     * @param doType|0-收藏   1-取消|Int|是
     * @title 收藏/取消收藏技能
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @PostMapping("/likeOrUnUnLikeSkill")
    public HttpResponse<Object> likeOrUnUnLikeSkill(@RequestBody RequestBean_LikeOrDisLikeSkill mRequestBean_LikeOrDisLikeSkill, @RequestHeader String token) {
        return busunessService.likeOrUnUnLikeSkill(mRequestBean_LikeOrDisLikeSkill, token);
    }


    /**
     * 获取收藏的技能列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param lat|经纬度|String|是
     * @param lng|经纬度|String|是
     * @param page|分页字段 从1 开始|String|是
     * @param number|分页字段|String|是

     * @title 获取收藏的技能列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源 参考技能详情与登录接口|User、UserSkill|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
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
     * "liked_skill": true,
     * "autoRegistration": true,
     * "liked": false
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @ApiOperation(value = "获取收藏的技能列表")
    @PostMapping("/getLikedSkills")
    public HttpResponse<Object> getLikedSkills(@RequestHeader String token,@RequestBody ReqBeanGetLikedSkills mReqBeanGetLikedSkills) {
        return busunessService.getLikedSkills(token,mReqBeanGetLikedSkills);
    }


    /**
     * 获取技能详情
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param skillId|技能ID  |Int|是
     * @title 获取技能详情(新)
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|UserSkill|是
     * @resqParam 以下为UserSkill实体的参数说明|以下为UserSkill实体的参数说明|以下为UserSkill实体的参数说明|以下为UserSkill实体的参数说明
     * @resqParam id_| 技能的ID，键去掉后面的下划线 | Int |是
     * @resqParam user_|发布技能的用户实体,参考登录接口，键去掉后面的下划线|User|是
     * @resqParam subTagsStr|用户发布技能时选择的小类别以及数据 采用：标签,tag1$tag2@标签2,tag3$tag4$tag5 的形式传递($用竖杠替换)|String|是
     * @resqParam serviceDec|服务介绍|String|是
     * @resqParam imgsUrl|图片路径 多个图片路径用 '竖杠'分开 与发布技能时保持一致|String|是
     * @resqParam unitStr|金额的单位|String|是
     * @resqParam price|金额|Double|是
     * @resqParam stockNumber|库存 默认999|Int|否
     * @resqParam lat|经纬度|Float|是
     * @resqParam lng|经纬度|Float|是
     * @resqParam p_tag|技能大标签的ID|Long|是
     * @resqParam s_tag|技能子标签的ID|Long|是
     * @resqParam skill_type|服务类型 0 TA来找我 1 我去找TA 2 线上服务  3 邮寄|Int|是
     * @resqParam distance|距离|Long|是
     * @resqParam isLiked|发布技能的用户是否已收藏|Boolean|是
     * @resqParam liked_skill|技能是否已收藏|Boolean|是
     * @resqParam autoRegistration|是否接受被自动报名|Boolean|是
     * @resqParam messageList|评价列表|List<SkillMessageTab>|否
     * @resqParam 以下为SkillMessageTab实体的参数说明|以下为SkillMessageTab实体的参数说明|以下为SkillMessageTab实体的参数说明|以下为SkillMessageTab实体的参数说明
     * @resqParam id|留言ID|Int|是
     * @resqParam userId|发布留言的用户ID|String|是
     * @resqParam content|留言内容|String|是
     * @resqParam messageCreateTime|留言时间 时间戳|String|是
     * @resqParam user|发布留言的用户实体 参考登录接口|User|是
     * @resqParam subMessageList|二级留言列表 参考一级留言实体|List<SkillMessageTab>|否
     * @resqParam specsList|规格列表|List<SpecsBean>|是
     * @resqParam 以下为SpecsBean实体的参数说明|以下为SpecsBean实体的参数说明|以下为SpecsBean实体的参数说明|以下为SpecsBean实体的参数说明
     * @resqParam specsId|规格ID 添加新的规格无需填写 修改规格时必填|Long|否
     * @resqParam specsTitle|规格对应的标题|String|是
     * @resqParam specsPrice|规格对应的价格|Float|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": {
     * "id": 576,
     * "user": {
     * "userId": "c051d44c-7b9b-4927-b7e8-491eb11b8fc6",
     * "userType": 0,
     * "phoneNumber": "18800000000",
     * "userName": "188****0000",
     * "sex": -1,
     * "birthday": 1574992345077,
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "wyy_accid": "yxaccid_wb_eb58902851ab4a89a5d44",
     * "wyy_token": "yxToken_wb_v2_18800000000",
     * "createTime": 1574992345077,
     * "ban": false,
     * "qopenIdq": "",
     * "wopenIdx": ""
     * },
     * "serviceDec": "(服务)爱犬丢失，望好心人士看到后告知我，定有重礼相送，左侧为我家二狗子的照片",
     * "imgsUrl": "http://111222.jpg|http://222222.jpg",
     * "unitStr": "元",
     * "price": 5000.0,
     * "stockNumber": 999,
     * "lat": 111.0,
     * "lng": 111.0,
     * "p_tag": 4,
     * "s_tag": 99,
     * "skill_type": -1,
     * "distance": 0,
     * "liked_skill": false,
     * "autoRegistration": true,
     * "messageList": [],
     * "specsList": [
     * {
     * "specsId": 577,
     * "skillId": 576,
     * "specsTitle": "第二个技能对应的规格1",
     * "specsPrice": 1000.0,
     * "createTime": 1575007407401,
     * "createUserId": "c051d44c-7b9b-4927-b7e8-491eb11b8fc6"
     * },
     * {
     * "specsId": 578,
     * "skillId": 576,
     * "specsTitle": "第二个技能对应的规格2",
     * "specsPrice": 2000.0,
     * "createTime": 1575007407410,
     * "createUserId": "c051d44c-7b9b-4927-b7e8-491eb11b8fc6"
     * }
     * ]
     * }
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/29 下午2:11
     */
    @ApiOperation(value = "获取技能详情")
    @PostMapping("/getSkillDetail")
    public HttpResponse<Object> getSkillDetail(@RequestBody RequestBean_GetSkillDetail requestSkillDetailBean, @RequestHeader String token) {
        return busunessService.getSkillDetail(requestSkillDetailBean, token);
    }

    /**
     * 发布/修改技能 ⚠️：修改技能时请携带技能ID字段，不带表示发布新技能
     *
     * @param token|⚠️请求头携带                        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param specsList|服务规格                       List<SpecsBean> eg:[{"specsId":1000,"specsTitle":"单间","specsPrice":"100.0","unit":"件"}] 修改技能或标签时ID必须传|List|是
     * @param id|技能ID                              修改时请携带该字段，不带该字段时将默认为发布新的技能|Long|否
     * @param serviceDec|服务介绍|String|是
     * @param imgsUrl|图片路径                         多个图片路径用 '竖杠'分开 与发布技能时保持一致|String|是
     * @param lat|经纬度|Float|是
     * @param title|技能名称|String|是
     * @param lng|经纬度|Float|是
     * @param p_tag|技能大标签的ID|Long|是
     * @param s_tag|技能子标签的ID|Long|是
     * @param skill_type|服务类型                      0 TA来找我 1 我去找TA 2 线上服务  3 邮寄|Int|是
     * @param autoRegistration|是否接受被自动报名|Boolean|是
     * @title 发布/修改技能(新)
     * @resqParam code|状态码(9000:操作成功-0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|数据源|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": ""
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/29 下午2:41
     */
    @ApiOperation(value = "发布技能")
    @PostMapping("/sendSkill")
    public HttpResponse<Object> sendSkill(@RequestBody UserSkill userSkill, @RequestHeader String token) {
        return busunessService.sendSkill(userSkill, token);
    }

    /**
     * 获取用户发布的技能列表
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param userId|用户ID   为空时返回自己的技能列表 |Int|否
     * @param lat|经纬度 排序 |Int|否
     * @param lng|经纬度 排序 |Int|否
     * @param page|分页page 从1开始 |Int|否
     * @param number|分页字段 默认Int最大值 |Int|否
     * @title 获取用户发布的技能列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|技能列表|List<UserSkill>|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 384,
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
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @ApiOperation(value = "获取用户发布的技能列表")
    @PostMapping("/getSkills")
    public HttpResponse<Object> getSkills(@RequestHeader String token, @RequestBody RequestBean_GetSkills mRequstSkillsBean) {
        return busunessService.getSkills(token, mRequstSkillsBean);
    }

    /**
     * 获取全部技能列表
     *
     * @param token|⚠️请求头携带        由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param subTagId|根据技能副标签筛选数据 为空时默认获取全部类型数据 |Int|是
     * @param lat|经纬度为空时排序会出问题     |Float|是
     * @param lng|经纬度为空时排序会出问题     |Float|是
     * @param page|请求的页码           默认 1 必须从1开始 |Int|否
     * @param number|请求的条数         默认返回全部数据 |Int|否
     * @title 获取全部技能列表
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|技能列表|List<UserSkill>|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 384,
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
     * @requestType post
     * @author Jason
     * @date 2019/11/29 下午2:41
     */
    @ApiOperation(value = "获取全部技能列表")
    @PostMapping("/getAllSkills")
    public HttpResponse<Object> getAllSkills(@RequestHeader String token, @RequestBody RequestBean_GetAllSkill mRequestBean_GetAllSkill) {
        return busunessService.getAllSkills(token, mRequestBean_GetAllSkill);
    }

    /**
     * 获取Banner
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @param bannerType|0  表示获取首页轮播图 其他情况待定 |Int|是
     * @title 获取Banner
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|Banner列表|List<Banner>|是
     * @resqParam 以下为Banner实体的参数说明|以下为Banner实体的参数说明|以下为Banner实体的参数说明|以下为Banner实体的参数说明
     * @resqParam id|banner的ID|Long|是
     * @resqParam clickType|该banner图点击的事件类型，默认值-1表示无需点击事件  0表示打开WebView|Int|是
     * @resqParam imgUrl|图片链接地址|String|是
     * @resqParam businessValue|clickType不为0时有值，对应各个type需要的值 如 clickType为0时 该值为需要跳转的webView的URL|String|否
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "id": 21,
     * "clickType": -1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324842&di=ded329f4eb849fa8fa7c9c15a9483dd9&imgtype=0&src=http%3A%2F%2Fwww.33lc.com%2Farticle%2FUploadPic%2F2012-8%2F2012811713810418.jpg"
     * },
     * {
     * "id": 19,
     * "clickType": 0,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=1e02333b89690648c3eca134358d4348&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130728%2F7447430_143257805000_2.jpg",
     * "businessValue": "http://www.baidu.com"
     * },
     * {
     * "id": 20,
     * "clickType": 1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=2a8fbc80ebf3b5e55e05fea1c31eb0eb&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130314%2F11899688_192542628000_2.jpg",
     * "businessValue": "f775ac05-6ddd-4d15-83f9-215a0ff98abf"
     * },
     * {
     * "id": 12,
     * "clickType": -1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324842&di=ded329f4eb849fa8fa7c9c15a9483dd9&imgtype=0&src=http%3A%2F%2Fwww.33lc.com%2Farticle%2FUploadPic%2F2012-8%2F2012811713810418.jpg"
     * },
     * {
     * "id": 11,
     * "clickType": 1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=2a8fbc80ebf3b5e55e05fea1c31eb0eb&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130314%2F11899688_192542628000_2.jpg",
     * "businessValue": "f775ac05-6ddd-4d15-83f9-215a0ff98abf"
     * },
     * {
     * "id": 10,
     * "clickType": 0,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=1e02333b89690648c3eca134358d4348&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130728%2F7447430_143257805000_2.jpg",
     * "businessValue": "http://www.baidu.com"
     * },
     * {
     * "id": 22,
     * "clickType": 0,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=1e02333b89690648c3eca134358d4348&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130728%2F7447430_143257805000_2.jpg",
     * "businessValue": "http://www.baidu.com"
     * },
     * {
     * "id": 23,
     * "clickType": 1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324843&di=2a8fbc80ebf3b5e55e05fea1c31eb0eb&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130314%2F11899688_192542628000_2.jpg",
     * "businessValue": "f775ac05-6ddd-4d15-83f9-215a0ff98abf"
     * },
     * {
     * "id": 24,
     * "clickType": -1,
     * "imgUrl": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1569163324842&di=ded329f4eb849fa8fa7c9c15a9483dd9&imgtype=0&src=http%3A%2F%2Fwww.33lc.com%2Farticle%2FUploadPic%2F2012-8%2F2012811713810418.jpg"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @ApiOperation(value = "获取banner图", notes = "请求参数详见\"RequestBean_GetBanner\"model")
    @PostMapping("/getBanner")
    public HttpResponse<Object> getBanner(@RequestBody RequestBean_GetBanner mRequestBean_GetBanner) {
        return busunessService.getBanner(mRequestBean_GetBanner);
    }


    /**
     * 获取动态
     *
     * @param token|⚠️请求头携带 由登录返回的userId与token 拼接而成(中间使用英文下的竖杠),使用token键上送|String|是
     * @title 获取动态
     * @resqParam code|状态码(9000:操作成功 -0x01:一般错误 -0x02:用户身份失效  -0x03:用户被封禁   -0x04：用户不存在)|Int|是
     * @resqParam message|状态说明|String|是
     * @resqParam data|技能列表|List<ResponseBean_Dynamic>|是
     * @resqParam 以下为ResponseBean_Dynamic实体的参数说明|以下为ResponseBean_Dynamic实体的参数说明|以下为ResponseBean_Dynamic实体的参数说明|以下为ResponseBean_Dynamic实体的参数说明
     * @resqParam userId|发布该动态的用户ID|String|是
     * @resqParam b_Type|发布需求为0 发布技能为1|Int|是
     * @resqParam b_Name|发布的技能或者需求名称|String|是
     * @resqParam userLogo|发布该动态的用户头像|String|是
     * @resqParam userName|发布该动态的用户昵称|String|是
     * @respBody {
     * "code": 9000,
     * "message": "Success",
     * "data": [
     * {
     * "userId": "admin",
     * "b_Type": 0,
     * "b_Name": "二手车过户代办",
     * "userLogo": "http://e.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
     * "userName": "系统管理员"
     * },
     * {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "b_Type": 0,
     * "b_Name": "短租房",
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "userName": "153****6229"
     * },
     * {
     * "userId": "139bd705-0209-4450-94cf-46286fc2b96a",
     * "b_Type": 0,
     * "b_Name": "合租房",
     * "userLogo": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570877538519&di=ba3ef360a29ffd791db78b83107d7ce1&imgtype=0&src=http%3A%2F%2Fimg.xgo-img.com.cn%2Fpics%2F1549%2Fa1548895.jpg",
     * "userName": "153****6229"
     * }
     * ]
     * }
     * @requestType post
     * @author Jason
     * @date 2019/11/19 上午10:11
     */
    @ApiOperation(value = "获取动态")
    @PostMapping("/getDynamic")
    public HttpResponse<Object> getDynamic() {
        return busunessService.getDynamic();
    }

}
