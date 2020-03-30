package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 跑腿代办表
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
data class RunErrandsTag(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var title: String = "",
        var subListStr: String = "",
        @JsonIgnore
        var orderByType: Int = 0
)