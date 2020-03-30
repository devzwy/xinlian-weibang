package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.User
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface UserRepository : JpaRepository<User, Long> {

    /**
     * 使用电话号码查找用户
     */
    fun findTop1ByPhoneNumber(phoneNumber: String): User?

    /**
     * 匹配正确的用户名和密码
     */
    fun findUserByPhoneNumberAndPassword(phoneNumber: String, password: String): User?


    /**
     * 使用微信openId查找用户
     */
    fun findTopByWOpenIdx(wx_openid: String): User?

    /**
     * 使用QQ openId查找用户
     */
    fun findTopByQOpenIdq(qOpenIdq: String): User?

    /**
     * 使用用户ID获取一个用户实体
     */
    fun findTop1ByUserId(userId: String): User?


    /**
     * 删除一个用户
     */
    fun deleteUserByUserId(userId: String)


    /**
     * 根据创建时间排序
     */
    fun findTop10ByOrderByCreateTime(): List<User>?
}

