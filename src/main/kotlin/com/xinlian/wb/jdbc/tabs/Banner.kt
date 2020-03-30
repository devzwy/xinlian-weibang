package com.xinlian.wb.jdbc.tabs

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


/**
 * banner接口回传数据 回传该对象的集合
 * [clickType] 该banner图点击的事件类型，默认值-1表示无需点击事件  0表示打开WebView
 * [imgUrl] 图片链接地址
 * [businessValue] clickType不为0时有值，对应各个type需要的值 如 clickType为0时 该值为需要跳转的webView的URL
 */
@ApiModel(description = "获取banner回传数据集合")
@Entity
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
data class Banner(@Id
                  @GeneratedValue(strategy = GenerationType.AUTO)
                  val id: Long = 0,
                  @ApiModelProperty(value = "该banner图点击的事件类型，默认值-1表示无需点击事件  0表示打开WebView")
                  val clickType: Int = -1,
                  @ApiModelProperty(value = "图片链接地址")
                  val imgUrl: String,
                  @ApiModelProperty(value = "clickType不为0时有值，对应各个type需要的值 如 clickType为0时 该值为需要跳转的webView的URL")
                  var businessValue: String? = "")