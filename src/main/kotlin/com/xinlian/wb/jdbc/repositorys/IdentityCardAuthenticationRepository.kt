package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.IdentityCardAuthenticationTab
import com.xinlian.wb.jdbc.tabs.User
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface IdentityCardAuthenticationRepository : JpaRepository<IdentityCardAuthenticationTab, Long> {

    fun findTop1ByUser(user: User): IdentityCardAuthenticationTab?
}