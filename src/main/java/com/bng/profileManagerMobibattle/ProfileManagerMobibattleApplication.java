package com.bng.profileManagerMobibattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.bng.profileManagerMobibattle.filter.PostLoginAuthorizationFilter;
import com.bng.profileManagerMobibattle.filter.PreLoginAuthorizationFilter;


@SpringBootApplication
public class ProfileManagerMobibattleApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ProfileManagerMobibattleApplication.class, args);
	}

//	@Bean
//	public FilterRegistrationBean<PreLoginAuthorizationFilter> preLoginFilter(){
//	    FilterRegistrationBean<PreLoginAuthorizationFilter> registrationBean 
//	      = new FilterRegistrationBean<>();
//	    registrationBean.setFilter(new PreLoginAuthorizationFilter());
//	    registrationBean.addUrlPatterns("/prelogin/*");
//	    return registrationBean;    
//	}
//	
//	@Bean
//	public FilterRegistrationBean<PostLoginAuthorizationFilter> postLoginFilter(){
//	    FilterRegistrationBean<PostLoginAuthorizationFilter> registrationBean 
//	      = new FilterRegistrationBean<>();
//	    registrationBean.setFilter(new PostLoginAuthorizationFilter());
//	    registrationBean.addUrlPatterns("/postlogin/*");
//	    return registrationBean;    
//	}

}
