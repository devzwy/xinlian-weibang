package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*
import javax.persistence.*

/**
 * 商户认证
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class MerchanAuthTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var merchanId: Long? = null,//商户认证编号
        var userId: String? = null,//对应的用户ID
        var verifyDec: String = "",//认证失败时为失败的原因
        var verifyState: Int? = 0,//审核状态 0 -等待审核  1-审核拒绝 2-审核成功
        var createTime: Long = Date().time,//记录创建的时间
        var verifyTime: Long? = null,//审核通过的时间
        var verifyUserId: String? = null,//审核的用户
        var merchanTitle: String? = "",//店铺名称
        var merchanUserName: String? = null,//联系人姓名
        var merchanPhoneNumber: String? = null,//联系人电话

        var authId: Long? = null,//实名信息ID 注意：此处写入参数时同步更新两个表 一个用户表的实名ID 还有此处的实名ID

        var merchanPhotos: String? = null,//店铺门头照Url 多个用|隔开
        var merchanBaseTagId: Long? = null,//店铺服务品类 标签表最外层的标签ID eg：家政服务(4)、社区服务(5) 传入对应ID
        var serviceTime: String? = null,//服务时间，周一到周日 用0..6表示 ，比如用户选择周一到周五，上传参数事例：0,1,2,3,4|开始营业时间戳,结束营业时间戳
        var city_merchan: String? = null,//店铺地址 - 城市
        var addressDetail_Merchan: String? = null,//店铺地址 - 详细地址
        var addressInfo: String? = null,//店铺介绍

        var serverBusinessLicenseType: Int? = null,//营业执照类型 0-有限责任公司  1 - 个体工商户
        var serverBusinessLicenseName: String? = null,//营业执照名称
        var serverBusinessLicenseNumber: String? = null,//营业执照社会信用代码或注册号
        var serverBusinessLicenseEndTime: Long? = null,//营业执照有效期截止时间 永久期限传-1
        var serverBusinessLicensePhotos: String? = null,//营业执照照片 多个用|隔开

        var serverPermitLicenseNmae: String? = null,//许可证名称
        var serverPermitLicenseRange: String? = null,//许可证经营范围
        var serverPermitLicenseEndTime: Long? = null,//许可证有效期截止时间 永久期限传-1
        var serverPermitLicensePhotos: String? = null,//安全生产许可证照片 多个用|隔开
        @Transient
        var idemyityName: String? = null, //实名姓名
        @Transient
        var idemyityCardNumber: String? = null, //实名身份证号码
        @Transient
        var merchanBaseTagBean: SkillParentTab? = null //大标签实体

)