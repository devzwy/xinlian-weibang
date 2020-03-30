package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.City
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional


/**
 * 一级代理商表
 */
@Transactional
interface CityRepository : JpaRepository<City, Long> {

    /**
     * 根据城市ID查询一个实体
     */
    fun findTop1ByCityId(cityId: Long): City?

    /**
     * 根据省ID查询城市集合
     */
    fun findAllByProvinceId(provinceId: Long): List<City>?


}

