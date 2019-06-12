package com.synchronoss.saw.export;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration

/*
 * Swagger configuration.
 */
public class SwaggerConfig {
  /**
   * Docker configuration base package.
   * @return
   */
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select().apis(
        RequestHandlerSelectors.basePackage("com.synchronoss.saw.export.controller"))
        .paths(PathSelectors.any()).build();
  }
}