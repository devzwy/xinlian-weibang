package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.ShowWebBoradTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface ShowWebBoradRepository : JpaRepository<ShowWebBoradTab, Long> {

    /**
     * 查询公告栏
     */
    fun findTop1ById(id: Long): ShowWebBoradTab?
}

