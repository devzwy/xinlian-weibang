package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SkillMessageTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface SkillMessageRepository : JpaRepository<SkillMessageTab, Long> {
    /**
     * 查找一条留言
     */
    fun findTop1ById(id: Long): SkillMessageTab?

    /**
     * 根据技能id查找对应的留言
     */
    fun findAllBySkillIdOrderByMessageCreateTimeDesc(skillId: String): List<SkillMessageTab>

}