package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.Town
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional


/**
 * 二级代理商表
 */
@Transactional
interface TownRepository : JpaRepository<Town, Long> {

    /**
     * 根据城市ID查询一个实体
     */
    fun findTop1ByTownId(townId: Long): Town?

    /**
     * 根据区ID查询小镇集合
     */
    fun findAllByCountyId(countyId: Long): List<Town>?


}

