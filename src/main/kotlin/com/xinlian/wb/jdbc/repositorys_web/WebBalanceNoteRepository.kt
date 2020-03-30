package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.WebBalanceNoteTab
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional


/**
 * 代理商和公司流水
 */
@Transactional
interface WebBalanceNoteRepository : JpaRepository<WebBalanceNoteTab, Long> {

    /**
     * 根据状态筛选
     */
    @Query(value = "SELECT * FROM web_balance_note_tab WHERE b_type = :bType ORDER BY create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByBType(@Param("bType") bType: Int = 2, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WebBalanceNoteTab>

    /**
     * 查询全流水
     */
    @Query(value = "SELECT * FROM web_balance_note_tab  ORDER BY create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WebBalanceNoteTab>

    /**
     * 根据状态筛选
     */
    @Query(value = "SELECT * FROM web_balance_note_tab WHERE b_type = :bType AND user_id = :userId ORDER BY create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserIdAndBType(@Param("userId") userId: String, @Param("bType") bType: Int = 2, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WebBalanceNoteTab>

    /**
     * 查询代理商全流水
     */
    @Query(value = "SELECT * FROM web_balance_note_tab  WHERE user_id = :userId ORDER BY create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserId(@Param("userId") userId: String, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<WebBalanceNoteTab>

}

