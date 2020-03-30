package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.Province
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface ProvinceRepository : JpaRepository<Province, Long> {

    fun findTop1ByProvinceId(provinceId: Long): Province?
}

