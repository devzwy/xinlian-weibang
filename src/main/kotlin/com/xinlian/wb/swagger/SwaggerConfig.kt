package com.xinlian.wb.swagger

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
open class SwaggerConfig {
    val logger = LoggerFactory.getLogger(SwaggerConfig::class.java)

    @Bean
    open fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(ApiInfoBuilder()
                        .title("威帮Api文档系统")
                        .description("毫无违和感的破文档，已放弃使用，文档请访问:https://114.116.213.202/wb/api")
//                        .termsOfServiceUrl("http://blog.csdn.net/saytime")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xinlian.wb.controller"))
                .paths(PathSelectors.any())
                .build();
    }


}

