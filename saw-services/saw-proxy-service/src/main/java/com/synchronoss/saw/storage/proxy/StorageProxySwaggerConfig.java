package com.synchronoss.saw.storage.proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class StorageProxySwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo())  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("com.synchronoss.saw.storage.proxy.controller"))              
          .paths(PathSelectors.any())                          
          .build();
                   
    }
    private ApiInfo getApiInfo() {
      Contact contact = new Contact("Saurav Paul", "http://www.synchronoss.com", "saurav.paul@synchronoss.com");
      return new ApiInfoBuilder()
              .title("Storage Proxy REST API")
              .description("This REST API has been developed for storage layer to be accessible without any complexity. Currently it only support ElasticSearch")
              .version("1.0.0")
              .contact(contact)
              .build();
    } 
    
}