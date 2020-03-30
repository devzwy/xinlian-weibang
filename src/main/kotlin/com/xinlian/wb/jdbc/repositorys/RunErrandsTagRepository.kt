package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.RunErrandsTag
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface RunErrandsTagRepository : JpaRepository<RunErrandsTag, Long> {

}