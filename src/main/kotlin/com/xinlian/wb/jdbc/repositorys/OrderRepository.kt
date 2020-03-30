package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.WBOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface OrderRepository : JpaRepository<WBOrder, Long> {

    /**
     * 跟酒订单id查询订单
     */
    fun findTop1ByOrderId(orderId: String): WBOrder?

    /**
     * 获取当前用户的订单
     */
    fun findAllByUserIdFromBuyOrUserIdFromServer(userIdFromBuy: String, userIdFromServer: String): List<WBOrder>

    /**
     * 查询前10个订单
     */
    fun findTop10ByOrderByOrderCreateTime(): List<WBOrder>?


    /**
     * 根据状态筛选
     */
    @Query(value = "SELECT * FROM wborder WHERE state = :state ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByState(@Param("state") state: Int, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    /**
     * 根据传入的userId 和state筛选
     */
    @Query(value = "SELECT * FROM wborder WHERE state = :state AND user_id_from_buy = :userId OR user_id_from_server = :userId ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByStateAndUserIdFromBuyOrUserIdFromServer(@Param("state") state: Int, @Param("userId") userId: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    /**
     * 用用户ID筛选
     */
    @Query(value = "SELECT * FROM wborder WHERE user_id_from_buy = :userIdFromBuy OR user_id_from_server = :userIdFromServer ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserIdFromBuyOrUserIdFromServer(@Param("userIdFromBuy") userIdFromBuy: String, @Param("userIdFromServer") userIdFromServer: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    /**
     * 查询买方全部订单
     */
    @Query(value = "SELECT * FROM wborder WHERE user_id_from_buy = :userIdFromBuy ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("userIdFromBuy") userIdFromBuy: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    /**
     * 查询买方筛选订单
     */
    @Query(value = "SELECT * FROM wborder WHERE user_id_from_buy = :userIdFromBuy AND state = :state ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("userIdFromBuy") userIdFromBuy: String, @Param("state") state: Int, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    /**
     * 查询卖方全部订单
     */
    @Query(value = "SELECT * FROM wborder WHERE user_id_from_server = :userIdFromServer AND state >= 2 ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserIdFromServer(@Param("userIdFromServer") userIdFromServer: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>

    /**
     * 查询卖方筛选订单
     */
    @Query(value = "SELECT * FROM wborder WHERE user_id_from_server = :userIdFromServer AND state = :state ORDER BY order_create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserIdFromServer(@Param("userIdFromServer") userIdFromServer: String, @Param("state") state: Int, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WBOrder>


    fun findTop1ByOrderBusinessId(orderBusinessId: String): WBOrder

}