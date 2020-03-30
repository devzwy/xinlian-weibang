package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.LuckDrawTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface LuckDrawTabRepository : JpaRepository<LuckDrawTab, Long> {

}