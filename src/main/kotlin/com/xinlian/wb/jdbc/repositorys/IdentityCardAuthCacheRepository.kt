package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.IdentityCardAuthCacheTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface IdentityCardAuthCacheRepository : JpaRepository<IdentityCardAuthCacheTab, Long> {

//
//    /**
//     * 是否已经存在该记录的错误值
//     */
//    fun checked(mRequestBean_AuthCard: RequestBean_AuthCard): Boolean {
//        if (mRequestBean_AuthCard.cardNumber.isEmpty() || mRequestBean_AuthCard.userRelName.isEmpty()) {
//            return false
//        }
//        return findTopByCardNumberAndUserRelName(mRequestBean_AuthCard.cardNumber, mRequestBean_AuthCard.userRelName) != null
//    }

    fun findTopByCardNumberAndUserRelName(cardNumber: String, userRelName: String): IdentityCardAuthCacheTab?
}