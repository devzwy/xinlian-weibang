package com.xinlian.wb.jdbc

import org.hibernate.dialect.MySQL5InnoDBDialect

open class MySQL5InnoDBDialectUTF8 : MySQL5InnoDBDialect() {
    override fun getTableTypeString(): String {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}