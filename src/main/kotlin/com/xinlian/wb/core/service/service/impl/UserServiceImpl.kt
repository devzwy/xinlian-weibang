package com.xinlian.wb.core.service.service.impl

import com.xinlian.wb.core.config.OkHttpCli
import com.xinlian.wb.core.entity.*
import com.xinlian.wb.core.service.service.IUserService
import com.xinlian.wb.jdbc.repositorys.*
import com.xinlian.wb.jdbc.tabs.*
import com.xinlian.wb.util.ApiTimer
import com.xinlian.wb.util.Constant
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.decrypt
import com.xinlian.wb.util.ktx.encrypt
import com.xinlian.wb.util.ktx.getErrorRespons
import com.xinlian.wb.util.ktx.isPhone
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service


@Service
@Configuration
open class UserServiceImpl : IUserService {


    /**
     * redis操作对象
     *  redis[0] 验证码
     *  redis[1] token
     *  redis[2] 日志
     */


    /**
     * 用户表
     */
    @Autowired
    lateinit var userRepository: UserRepository

    /**
     * 送货地址表
     */
    @Autowired
    lateinit var mAddressRepository: AddressRepository


    /**
     * 标签表
     */
    @Autowired
    lateinit var mSkillParentTabRepository: SkillParentTabRepository


    /**
     * 奖品表
     */
    @Autowired
    lateinit var mLuckDrawTabRepository: LuckDrawTabRepository


    @Autowired
    lateinit var mLikeTabRepository: LikeTabRepository

    /**
     * 身份证认证的错误缓存表
     */
    @Autowired
    lateinit var mIdentityCardAuthCacheRepository: IdentityCardAuthCacheRepository

    @Autowired
    lateinit var okHttpCli: OkHttpCli

    /**
     * 身份证认证成功存储表
     */
    @Autowired
    lateinit var mIdentityCardAuthenticationRepository: IdentityCardAuthenticationRepository

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Value("\${tagVersion}")
    private val tagVersion: Int? = null


    @Autowired
    lateinit var mMerchanAuthRepository: MerchanAuthRepository

    /**
     * 用户身份证认证
     */
    override fun authCard(mrequestbeanAuthcard: RequestBean_AuthCard, token: String): HttpResponse<Any> {

        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        logger.info("用户身份认证")
        //解密校验
        val name = mrequestbeanAuthcard.userRelName.decrypt()
        val number = mrequestbeanAuthcard.cardNumber.decrypt()

        if (name == null || number == null) {
            return "安全校验不通过".getErrorRespons()
        }
        mrequestbeanAuthcard.cardNumber = number
        mrequestbeanAuthcard.userRelName = name

        //首先在当前用户查找是否有实名信息
        if (user.indentity != null) {
            //已认证 无需重复认证
            logger.info("用户实名信息已存在于一对一表中，直接回传已认证信息")
            return "已认证，请勿重复认证".getErrorRespons()
        }

        logger.info("查询缓存表")

        //用户实名信息不存在时在失败的缓存表中查询
        val bean = mIdentityCardAuthCacheRepository.findTopByCardNumberAndUserRelName(mrequestbeanAuthcard.cardNumber, mrequestbeanAuthcard.userRelName)
        if (bean != null) {
            logger.info("曾认证失败的缓存表中存在数据，返回认证失败信息")
            return "认证失败，请重试".getErrorRespons()
        }

        //如果还不存在时在第三方平台查询，查询到的结果根据校验状态存入不同的表  成功时存入用户一对一的实名信息表中  失败时存入缓存表

        logger.info("查询第三方平台，实名认证")
        val beanAuthYy = Helper.checkCardNumber(okHttpCli, mrequestbeanAuthcard)

        if (beanAuthYy.code == 400100 && beanAuthYy.success == true) {
            logger.info("三方回传认证成功")
            //认证成功
            val ideBean = IdentityCardAuthenticationTab(idemyityCardNumber = mrequestbeanAuthcard.cardNumber, idemyityName = mrequestbeanAuthcard.userRelName)

            user.indentity = ideBean
            ideBean.user = user

            userRepository.save(user)
            logger.info("认证信息写入用户一对一实名表成功")

            return HttpResponse(true)
        } else if (beanAuthYy.code == 400111) {
            logger.info("三方回传认证失败,网络异常")
            //认证失败 beanAuthYy.message  写入缓存表
            return "网络异常，请稍后再试".getErrorRespons()
        } else {
            //认证失败 beanAuthYy.message  写入缓存表
            mIdentityCardAuthCacheRepository.save(IdentityCardAuthCacheTab(userRelName = mrequestbeanAuthcard.userRelName, cardNumber = mrequestbeanAuthcard.cardNumber))
            logger.info("认证失败信息写入失败缓存表成功")
            return "验证失败,${beanAuthYy.message}".getErrorRespons()
        }
    }


    /**
     * 关注。取消关注
     */
    override fun likeOrDisLikeUser(mRequestBean_LikeOrDisLike: RequestBean_LikeOrDisLike, token: String): HttpResponse<Any> {
        if (mRequestBean_LikeOrDisLike.userId.isNullOrEmpty()) {
            return "关注的用户ID为空".getErrorRespons()
        }
        //收藏quxiao用户
        val thisUser = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
        val willBeLikedUser = userRepository.findTop1ByUserId(mRequestBean_LikeOrDisLike.userId ?: "")
                ?: return "用户不存在".getErrorRespons(Constant.Code.USER_NOT_EXIST_CODE)

        when (mRequestBean_LikeOrDisLike.doType) {
            0 -> {
                //收藏
                return doLike(thisUser = thisUser, willBeLikedUser = willBeLikedUser)
            }

            1 -> {
                //取消收藏
                return doUnLike(thisUser = thisUser, willBeLikedUser = willBeLikedUser)
            }
            else -> {
                return "操作类型不存在".getErrorRespons()
            }
        }
    }

    private fun doUnLike(thisUser: User?, willBeLikedUser: User): HttpResponse<Any> {
        //不为空表示当前用户已经收藏了该用户
        val likeTab = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(thisUser?.userId ?: "", willBeLikedUser.userId)
        if (likeTab == null) {
            return "This operation cannot be performed. Please bookmark the user and try again".getErrorRespons()
        } else {
            mLikeTabRepository.delete(likeTab)
            return HttpResponse("UnLiked user success")
        }
    }

    private fun doLike(thisUser: User?, willBeLikedUser: User): HttpResponse<Any> {
        //不为空表示当前用户已经收藏了该用户
        val likeTab = mLikeTabRepository.findTop1ByUserIdAndLikedUserId(thisUser?.userId ?: "", willBeLikedUser.userId)
        if (likeTab != null) {
            return "User was liked".getErrorRespons()
        } else {
            mLikeTabRepository.saveAndFlush(LikeTab(userId = thisUser?.userId, likedUserId = willBeLikedUser.userId))
            return HttpResponse("Liked user success")
        }
    }

    /**
     * 获取关注列表
     */
    override fun getUserLikedList(token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)
        val list = mLikeTabRepository.findAllByUserId(uid)
        val uList = ArrayList<User>()
        list.forEach {
            val u = userRepository.findTop1ByUserId(it.likedUserId!!)!!
            if (u.indentity != null) {
                u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
            }
            uList.add(u)
        }
        return HttpResponse(uList)
    }


    /**
     * 获取标签
     */
    @ApiTimer
    override fun getTags(): HttpResponse<Any> {
        val list = mSkillParentTabRepository.findAll()
        list.forEach {
            val subTagList = it.subSkills!!.toMutableList()
            val subTagList2 = it.subSkills!!.toMutableList()

            subTagList.forEach {
                if (!it.tagIsShow) {
                    subTagList2.remove(it)
                }
            }
            it.subSkills = subTagList2
        }
        //读取配置文件中的tag号
        return HttpResponse(ResponseBean_Tag(tagVersion!!, list))
    }

    /**
     * 获取标签版本号
     */
    override fun getTagsVersion(): HttpResponse<Any> {
        //读取配置文件中的tag号
        return HttpResponse(tagVersion!!)
    }

    /**
     * 更新用户资料
     */
    override fun updateUserInfo(user: RequestBean_UpdateUserInfo, token: String): HttpResponse<Any> {

        val user_db = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))

        val userLogoUrl = user.userLogo

        if (userLogoUrl != null && userLogoUrl.isNotEmpty()) {
            user_db?.userLogo = userLogoUrl
        }
        user_db?.birthday = user.birthday

        if (user.sex != null && (user.sex == 0 || user.sex == 1)) {
            user_db?.sex = user.sex ?: -1
        }
        if (user.userName != null && user.userName!!.isNotEmpty()) {
            user_db?.userName = user.userName!!
        }
        if (user.userProfiles != null) {
            user_db?.userProfiles = user.userProfiles
        }
        user_db?.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(user_db?.userId ?: "")?.verifyState ?: -1
        userRepository.saveAndFlush(user_db)

        return HttpResponse("")
    }

    /**
     * 获取用户保存的收货地址
     */
    override fun getUserAddressList(token: String): HttpResponse<Any> {
        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
                ?: return "User doesn't exit.".getErrorRespons()
        val list = user.address ?: arrayListOf()
        list.forEach {
            it.user = null
            if (it.isDefaultAddress == null) it.isDefaultAddress = false
        }
        return HttpResponse(list)
    }


    /**
     * 添加收货地址
     */
    override fun addAddress(address: Address, token: String): HttpResponse<Any> {
        if (address.phoneNumber.isNullOrEmpty() || !address.phoneNumber!!.isPhone()) return "电话号码格式不正确".getErrorRespons()
        if (address.name.isNullOrEmpty()) return "姓名为空".getErrorRespons()
        if (address.address_head.isNullOrEmpty()) return "地址为空".getErrorRespons()
        if (address.address_end.isNullOrEmpty()) return "地址为空".getErrorRespons()
        if (address.sex.isNullOrEmpty()) return "性别为空".getErrorRespons()
        try {
            address.sex!!.toInt()
        } catch (e: Exception) {
            return "性别格式错误".getErrorRespons()
        }
        if (address.tag.isNullOrEmpty()) return "标签为空".getErrorRespons()
        if (address.isDefaultAddress == null) return "是否默认收获地址为空".getErrorRespons()
        if (address.addressLat == null || address.addressLng == null) return "经纬度为空".getErrorRespons()
        if (address.isDefaultAddress==true){
            //筛选所有的数据，并将所有标记为false
            mAddressRepository.findAllByUser(userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))!!).forEach {
                it.isDefaultAddress = false
                mAddressRepository.saveAndFlush(it)
            }
        }

        //将地址信息写入address表
        var ad = Address(id = address.id, phoneNumber = address.phoneNumber, address_head = address.address_head, address_end = address.address_end, name = address.name, sex = address.sex, tag = address.tag, isDefaultAddress = address.isDefaultAddress, addressLat = address.addressLat, addressLng = address.addressLng)

        val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
        if (ad.id != 0L) {
            val arrayList = ArrayList<Address>()
            user?.address?.forEach {
                if (it.id.equals(ad.id)) {
                    arrayList.add(ad)
                } else {
                    arrayList.add(it)
                }
            }
            ad.user = user
//            arrayList.add(ad)
            user?.address = arrayList

            userRepository.saveAndFlush(user!!)
        } else {
            val arrayList = ArrayList<Address>()
            user?.address?.forEach {
                arrayList.add(it)
            }
            ad.user = user
            arrayList.add(ad)
            user?.address = arrayList

            userRepository.saveAndFlush(user!!)

        }

        return HttpResponse(userRepository.findTop1ByUserId(user.userId)?.address!!)
    }

    /**
     * 获取用户资料
     */
    override fun getUserInfo(requestbeanGetUserInfo: RequestBean_GetUserInfo, token: String): HttpResponse<Any> {
        val uid = requestbeanGetUserInfo.userId

        if (uid == null || uid.isEmpty()) {
            //查询当前用户的
            val user = userRepository.findTop1ByUserId(Helper.getUserIdByToken(token))
            user?.password = null
            return HttpResponse(user!!)
        } else {
            val u = userRepository.findTop1ByUserId(uid)
            if (u == null) return "User not found".getErrorRespons()
            u.userType = null
            u.password = null
            u.userType = null
            u.indentity = null
            u.merchanAuthState = mMerchanAuthRepository.findTop1ByUserId(u.userId)?.verifyState ?: -1
            if (u.indentity != null) {
                u.indentity!!.idemyityCardNumber = u.indentity!!.idemyityCardNumber.toString().encrypt()
                u.indentity!!.idemyityName = u.indentity!!.idemyityName.toString().encrypt()
            }
            return HttpResponse(u)
        }
    }

}
