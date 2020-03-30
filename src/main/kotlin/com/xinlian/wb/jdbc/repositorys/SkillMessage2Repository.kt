package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SkillMessage2Tab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface SkillMessage2Repository : JpaRepository<SkillMessage2Tab, Long> {
}