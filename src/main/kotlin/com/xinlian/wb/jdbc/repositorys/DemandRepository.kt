package com.xinlian.wb.jdbc.repositorys

import com.xinlian.wb.jdbc.tabs.Demand
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface DemandRepository : JpaRepository<Demand, Long> {

//    /**
//     * 查询当前用户发布的全部技能列表(Web端自己用，筛选数据)
//     */
//    fun findAllByUserId(userId: String): List<Demand>?

    /**
     * 根据技能ID查询一个技能实体
     */
    fun findTop1ById(id: Long): Demand?

    @Query(value = "SELECT id,lat,is_ban,create_time,demand_state,lng,user_id,p_tag,s_tag,service_time,expiry_date,gender_requirements,service_mode,demand_describe,registration_skills,round(6378.1370 * 2 * asin(sqrt(pow(sin((:lat * pi() / 180 - lat * pi() / 180) / 2), 2) +cos(:lat * pi() / 180) * cos(lat * pi() / 180) * pow(sin((:lng * pi() / 180 - lng * pi() / 180) / 2), 2))) *1000) AS distance FROM demand  Where is_ban = false AND demand_state !=-1 AND demand_state!=-2 ORDER BY distance ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("lat") lat: Float? = 0f, @Param("lng") lng: Float? = 0f, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<Demand>


    /**
     * web端查询全部需求列表
     */
    @Query(value = "SELECT * FROM demand ORDER BY create_time ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<Demand>


    /**
     * web端查询筛选全部需求列表
     */
    @Query(value = "SELECT * FROM demand WHERE demand_state = :demand_state ORDER BY create_time ASC limit :page, :number1 ;",
            nativeQuery = true)
    fun findAll(@Param("demand_state") demand_state: Int = 0, @Param("page") page: Int = 0, @Param("number1") number: Int = 20): List<Demand>


    @Query(value = "SELECT * FROM demand WHERE user_id = :userId AND is_ban = false AND demand_state !=-1 AND demand_state!=-2 ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("userId") userId: String, @Param("page") page: Int = 0, @Param("number") number: Int = Int.MAX_VALUE): List<Demand>

    fun findAllByUserIdAndBan(userId: String,ban:Boolean = false,pageRequest: PageRequest)

}