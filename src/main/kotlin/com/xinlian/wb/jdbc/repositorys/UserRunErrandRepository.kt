package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.UserRunErrand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface UserRunErrandRepository : JpaRepository<UserRunErrand, Long> {
    fun findTop1ByUserId(userId: String): UserRunErrand?

    fun findTop1ById(id: Long): UserRunErrand?


//    /**
//     * web端查询全部技能列表
//     */
//    @Query(value = "SELECT * FROM user_run_errand ORDER BY create_time ASC limit :page, :number1 ;",
//            nativeQuery = true)
//    fun findAll(@Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserRunErrand>

    /**
     * web端查询全部技能列表
     */
    @Query(value = "SELECT user_run_errand.* FROM user_run_errand,wborder WHERE wborder.order_business_id=user_run_errand.id AND wborder.state= 2 ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number") number: Int = 20): List<UserRunErrand>


    /**
     * web端查询全部技能列表
     */
    @Query(value = "SELECT user_run_errand.* FROM user_run_errand,wborder WHERE wborder.order_business_id=user_run_errand.id AND wborder.state= 2 AND user_run_errand.b_type =:b_type ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("b_type") b_type: Int = 0, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<UserRunErrand>

}