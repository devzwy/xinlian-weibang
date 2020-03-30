package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.UserDynamic
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface DynamicRepository : JpaRepository<UserDynamic, Long> {


}