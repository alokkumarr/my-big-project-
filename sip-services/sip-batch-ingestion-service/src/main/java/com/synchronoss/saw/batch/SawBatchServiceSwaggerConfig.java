package com.synchronoss.saw.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SawBatchServiceSwaggerConfig extends WebMvcConfigurationSupport {
  /**
   * Docket Bean.
   * 
   * @return Docket
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
    .apiInfo(getApiInfo()).select()
        .apis(RequestHandlerSelectors.basePackage("com.synchronoss.saw"))
        .paths(PathSelectors.any()).build();

  }

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
  
  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder().title("Synchronoss Analytics Batch Service").description(
        "This REST API has been developed to provision creating channel & set up routes to "
            + " consume data from outside.You can find more "
            + "about Synchrnoss Insight Platform at [http://www.synchronoss.com]")
        .version("1.0.0").contact(new Contact("Synchronoss Technologies",
            "http://www.synchronoss.com", "suren.nathan@synchronoss.com"))
        .build();
  }
}
