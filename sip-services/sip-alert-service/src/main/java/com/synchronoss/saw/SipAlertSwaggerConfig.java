package com.synchronoss.saw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SipAlertSwaggerConfig extends WebMvcConfigurationSupport {

  /**
   * Swagger api.
   *
   * @return Docket docket
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(getApiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.synchronoss.saw"))
        // .paths(PathSelectors.regex("/internal/proxy.*"))
        .build();
  }

  /**
   * Get Swagger Api Info.
   *
   * @return ApiInfo
   */
  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder()
        .title("Synchronoss Insights Alert Service")
        .description(
            "This REST API has been developed for Alert Service to be accessible. "
                + "Currently its manages Alert. You can find more "
                + "about Synchronoss Insight Platform at [http://www.synchronoss.com]")
        .version("3.X.X")
        .contact(
            new Contact(
                "Synchronoss Technologies Inc.",
                "http://www.synchronoss.com",
                "contact@synchronoss.com"))
        .build();
  }

  /**
   * addResourceHandlers for Swagger.
   *
   * @param registry registry
   */
  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
