package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.County
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface CountyRepository : JpaRepository<County, Long> {

    /**
     * 根据区域编号来查询是否存在
     */
    fun findTop1ByCountyId(countyId: Long): County?

    /**
     * 根据cityid查询所有的实体
     */
    fun findAllByCityId(cityId: Long): List<County>?
}

