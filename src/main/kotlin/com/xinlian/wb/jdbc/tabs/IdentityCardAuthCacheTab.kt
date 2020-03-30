package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import springfox.documentation.annotations.ApiIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


/**
 * 用户身份认证缓存表
 */
@ApiIgnore
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class IdentityCardAuthCacheTab(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = 0,
        val userRelName: String,
        val cardNumber: String)