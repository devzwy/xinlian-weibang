package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 添加用户收货地址实体
 *
 */
@ApiModel(description = "添加用户收货地址实体")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class WithDrawal(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var createTime: Long = Date.from(Instant.now()).time,//记录生成时间
        var state: Int,//状态 0-待处理 1-处理成功 2-处理失败
        var price: Double,
        var alipayUserName: String,
        var aliaAccount: String,
        var errMsg: String? = null,//处理失败的原因
        var userId: String,
        @Transient
        var user: Any? = null,
        var userType: Int //0=用户 1=代理商
)

interface WithDrawalRepository : JpaRepository<WithDrawal, Long> {


    /**
     * 获取指定记录
     */
    fun findTop1ById(id: Long): WithDrawal?

    /**
     * 获取用户或代理商或管理员自己的全部提现记录
     */
    @Query(value = "SELECT * FROM with_drawal where user_id = :user_id ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("user_id") user_id: String, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取用户或代理商或管理员自己的全部提现记录 并用state筛选
     */
    @Query(value = "SELECT * FROM with_drawal where user_id = :user_id AND state = :state ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("user_id") user_id: String, @Param("state") state: Int, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取全部提现记录
     */
    @Query(value = "SELECT * FROM with_drawal  ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取全部提现记录并用state筛选
     */
    @Query(value = "SELECT * FROM with_drawal where state = :state ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAll(@Param("state") state: Int = 0, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取用户全部提现记录
     */
    @Query(value = "SELECT * FROM with_drawal where user_type = 0 ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAllByUserType(@Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取用户全部提现记录 并用state筛选
     */
    @Query(value = "SELECT * FROM with_drawal where user_type = 0 AND state = :state ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAllByUserType(@Param("state") state: Int = 0, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>


    /**
     * 获取代理商全部提现记录
     */
    @Query(value = "SELECT * FROM with_drawal where user_type = 1 ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAllByUserType2(@Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>

    /**
     * 获取代理商全部提现记录 并用state筛选
     */
    @Query(value = "SELECT * FROM with_drawal where user_type = 1 AND state = :state ORDER BY create_time ASC limit :page, :number ;",
            nativeQuery = true)
    fun findAllByUserType2(@Param("state") state: Int = 0, @Param("page") page: Int = 0, @Param("number") number: Int = 20): List<WithDrawal>


}
