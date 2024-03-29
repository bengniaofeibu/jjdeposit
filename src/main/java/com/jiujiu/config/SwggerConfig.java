package com.jiujiu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Created by t_hz on 2019/5/15.
 */
@Configuration
public class SwggerConfig {

    @Bean
    public Docket createRestApi(){
       return new Docket(DocumentationType.SWAGGER_2)
              .apiInfo(apiInfo())
              .select()
              .apis(RequestHandlerSelectors.basePackage("com.jiujiu.controller"))
              .paths(PathSelectors.any())
              .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("swgger")
                .description("调试接口")
                .version("1.0")
                .build();
    }

}
