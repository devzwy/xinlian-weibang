package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SkillParentTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface SkillParentTabRepository : JpaRepository<SkillParentTab, Long> {
    /**
     * 根据ID查询一个实体
     */
    fun findTop1ByParentId(parentId: Long): SkillParentTab?
}