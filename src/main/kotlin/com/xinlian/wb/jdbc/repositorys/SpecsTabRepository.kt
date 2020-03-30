package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.SpecsTabBean
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface SpecsTabRepository : JpaRepository<SpecsTabBean, Long> {

    /**
     * 根据规格ID查询一个规格
     */
    fun findTopBySpecsId(specsId: Long): SpecsTabBean?

    fun findAllBySkillId(skillId: Long): List<SpecsTabBean>
}