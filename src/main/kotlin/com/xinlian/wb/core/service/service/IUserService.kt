package com.xinlian.wb.core.service.service

import com.xinlian.wb.core.entity.*
import com.xinlian.wb.jdbc.tabs.Address


interface IUserService {

    /**
     * 更新用户资料
     */
    fun updateUserInfo(user: RequestBean_UpdateUserInfo, token: String): HttpResponse<Any>

    /**
     * 获取用户资料
     */
    fun getUserInfo(requestbeanGetUserInfo: RequestBean_GetUserInfo, token: String): HttpResponse<Any>

    /**
     * 添加收货地址
     */
    fun addAddress(address: Address, token: String): HttpResponse<Any>

    /**
     * 获取用户保存的收货地址
     */
    fun getUserAddressList(token: String): HttpResponse<Any>

    /**
     * 获取标签
     */
    fun getTags(): HttpResponse<Any>

    /**
     * 获取标签版本号
     */
    fun getTagsVersion(): HttpResponse<Any>

    /**
     * 用户身份证认证
     */
    fun authCard(mrequestbeanAuthcard: RequestBean_AuthCard, token: String): HttpResponse<Any>

    /**
     * 关注。取消关注
     */
    fun likeOrDisLikeUser(mRequestBean_LikeOrDisLike: RequestBean_LikeOrDisLike, token: String): HttpResponse<Any>

    /**
     * 获取关注列表
     */
    fun getUserLikedList(token: String): HttpResponse<Any>


}