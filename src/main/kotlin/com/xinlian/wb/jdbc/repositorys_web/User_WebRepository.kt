package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.User_Web
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface User_WebRepository : JpaRepository<User_Web, Long> {

    /**
     * 根据用户ID查询一个用户
     */
    fun findTop1ByUserId(userId: String): User_Web?

    /**
     * 根据电话号码查询一个用户
     */
    fun findTop1ByPhoneNumber(phoneNumber: String): User_Web?


    /**
     * 匹配用户名密码
     */
    fun findTopByPhoneNumberAndPwd(phoneNumber: String, pwd: String): User_Web?

    /**
     * 获取全部代理商列表
     */
    fun findAllByUserTypeOrUserType(userType: Int, userType2: Int): List<User_Web>

    /**
     * 传入一级代理ID 查询线下所有二级代理商
     */
    fun findAllBySuperAgentId(supId: String): List<User_Web>


}

