 package com.group1.project.swp_project.config;

 import org.springframework.context.annotation.Configuration;
 import org.springframework.web.servlet.config.annotation.CorsRegistry;
 import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
 import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

 @Configuration
 public class WebConfig implements WebMvcConfigurer {
// @Override
// public void addCorsMappings(CorsRegistry registry) {
// registry.addMapping("/**") // Cho phép tất cả API
// .allowedOrigins("http://localhost:5173") // Cổng frontend React (Vite)
// .allowedMethods("*")
// .allowedHeaders("*")
// .allowCredentials(true);
// }
     @Override
     public void addResourceHandlers(ResourceHandlerRegistry registry) {
         String userDir = System.getProperty("user.dir");
         registry.addResourceHandler("/avatars/**")
                 .addResourceLocations("file:" + userDir + "/uploads/avatar/");
     }
 }
