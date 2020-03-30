package com.xinlian.wb.jdbc.repositorys_web

import com.xinlian.wb.jdbc.tabs_web.WebTabTitleBean
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface WebTabRepository : JpaRepository<WebTabTitleBean, Long> {

    /**
     * 根据权限查找页面加载的数据
     */
    fun findAllByPermission(permission: Int): List<WebTabTitleBean>

    /**
     * 根据权限 和显示的标题查询tab是否存在
     */
    fun findTop1ByTitleAndPermission(title: String, permission: Int): WebTabTitleBean?

    /**
     * 根据ID查找一个实体
     */
    fun findTop1ById(id: Long): WebTabTitleBean?

    /**
     * 查询二级目录
     */
    fun findAllByParentTabId(parentId: Long): List<WebTabTitleBean>

    /**
     * 删除ID
     */
    fun deleteAllByIdOrParentTabId(id: Long, parentId: Long?)

}

