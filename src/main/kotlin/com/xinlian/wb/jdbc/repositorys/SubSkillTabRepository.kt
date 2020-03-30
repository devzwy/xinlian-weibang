package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SubSkillTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface SubSkillTabRepository : JpaRepository<SubSkillTab, Long> {

    fun findOneBySubTagId(subId: Long): SubSkillTab?
}