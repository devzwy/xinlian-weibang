package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.Banner
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface BannerRepository : JpaRepository<Banner, Long> {
//    fun findAndSorp(pageNumber: Int = 0, pageSize: Int = 10): List<Banner> {
//        return findAll(PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "clickType")).content
//    }
}