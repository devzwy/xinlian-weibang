package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.LikeTab
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface LikeTabRepository : JpaRepository<LikeTab, Long> {
    /**
     * 获取全部收藏列表
     */
    fun findAllByUserId(userId: String): List<LikeTab>

    /**
     * 查询当前用户是否收藏了该用户
     */
    fun findTop1ByUserIdAndLikedUserId(userId: String, likedUserId: String): LikeTab?


}