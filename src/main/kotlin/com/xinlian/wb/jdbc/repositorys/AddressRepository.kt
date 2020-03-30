package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.Address
import com.xinlian.wb.jdbc.tabs.User
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface AddressRepository : JpaRepository<Address, Long> {

    /**
     * 跟酒ID查询地址
     */
    fun findTop1ById(id: Long): Address?


    fun findAllByUser(user: User):List<Address>

}