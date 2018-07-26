
package com.oyo.activitiworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class ActivitiWorkflowApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(ActivitiWorkflowApplication.class);
	}

	public static void main(String[] args) {

		SpringApplication.run(ActivitiWorkflowApplication.class, args);
	}

	@Configuration
	public class MyConfiguration {

		@Bean
		public WebMvcConfigurer corsConfigurer() {

			return new WebMvcConfigurerAdapter() {

				@Override
				public void addCorsMappings(CorsRegistry registry) {

					registry.addMapping("/**");
				}
			};
		}
	}
}
