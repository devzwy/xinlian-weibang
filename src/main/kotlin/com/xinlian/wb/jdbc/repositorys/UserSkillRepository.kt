package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.User
import com.xinlian.wb.jdbc.tabs.UserSkill
import org.hibernate.validator.constraints.ISBN
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface UserSkillRepository : JpaRepository<UserSkill, Long> {

    fun findTop1ById(id: Long): UserSkill?

    @Query(value = "SELECT id,lat,content_images,create_time,is_ban,lng,auto_registration,skill_type,imgs_url,p_tag,s_tag,service_dec,title,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM user_skill WHERE is_ban=false ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


    @Query(value = "SELECT id,lat,content_images,create_time,is_ban,lng,auto_registration,imgs_url,skill_type,p_tag,s_tag,service_dec,title,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM user_skill WHERE s_tag = :subTagId AND is_ban=false ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllBySubTagId(@Param("subTagId") subTagId: Int, @Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


    /**
     * web端查询全部技能列表
     */
    @Query(value = "SELECT * FROM user_skill ORDER BY create_time DESC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


    @Query(value = "SELECT id,lat,content_images,create_time,is_ban,lng,auto_registration,imgs_url,skill_type,p_tag,s_tag,service_dec,title,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM user_skill WHERE user_id=:user_id AND is_ban=false ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAllByUser(@Param("user_id")user_id: String, @Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


    @Query(value = "SELECT id,lat,content_images,create_time,is_ban,lng,auto_registration,imgs_url,skill_type,p_tag,s_tag,service_dec,title,user_id,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM user_skill WHERE title like :keyWords or service_dec like :keyWords AND is_ban=false ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("keyWords")keyWords: String, @Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<UserSkill>


}

