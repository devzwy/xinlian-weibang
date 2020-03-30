package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.BalanceNotes
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface BalanceNotesRepository : JpaRepository<BalanceNotes, Long> {
    /**
     * 获取当前用户的收入明细
     */
    fun findAllByUserIdAndBType(userId: String, bType: Int? = 0): List<BalanceNotes>

    /**
     * 查询用户全部流水
     */
    @Query(value = ("SELECT * FROM balance_notes WHERE user_id=:user_id ORDER BY create_time ASC limit :page, :number1"), nativeQuery = true)
    fun findAll(@Param("user_id") userId: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<BalanceNotes>

    /**
     * 查询用户收入流水
     */
    @Query(value = ("SELECT * FROM balance_notes WHERE user_id=:user_id AND b_type= :bType ORDER BY create_time ASC limit :page, :number1"), nativeQuery = true)
    fun findAll(@Param("user_id") userId: String, @Param("bType") bType: Int, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<BalanceNotes>


    /**
     * 查询用户全部充值流水 web
     */
    @Query(value = ("SELECT * FROM balance_notes WHERE b_type=2 ORDER BY create_time ASC limit :page, :number1"), nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<BalanceNotes>


}