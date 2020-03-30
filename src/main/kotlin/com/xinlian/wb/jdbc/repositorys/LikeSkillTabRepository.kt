package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.LikeSkillTab
import com.xinlian.wb.jdbc.tabs.UserSkill
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface LikeSkillTabRepository : JpaRepository<LikeSkillTab, Long> {
    /**
     * 获取全部收藏列表
     */
    fun findAllByUserId(userId: String): List<LikeSkillTab>
//
    /**
     * 查询当前用户是否收藏了该技能
     */
    fun findTop1ByUserIdAndUserSkillId(userId: String, userskillId: Long): LikeSkillTab?

    @Query(value = "SELECT id,user_skill_id,lat,lng,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM like_skill_tab WHERE user_id=:user_id ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUserId(@Param("user_id")user_id: String, @Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<LikeSkillTab>



//    @Query(value = "SELECT id,lat,content_images,create_time,is_ban,lng,auto_registration,imgs_url,skill_type,p_tag,s_tag,service_dec,title,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM user_skill WHERE user_id=:user_id AND is_ban=false ORDER BY distance ASC limit :page, :number1 ;",
//            nativeQuery = true)
//    fun findAllByUser(@Param("user_id")user_id: String, @Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


}