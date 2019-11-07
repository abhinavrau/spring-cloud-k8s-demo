package com.example.chaos.monkey.shopping.hotdeals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableDiscoveryClient
public class HotDealsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotDealsApplication.class, args);
	}
}
@Configuration
@Profile("cloud")
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests().anyRequest().permitAll()
				.and()
				.httpBasic().disable()
				.csrf().disable();
	}

}