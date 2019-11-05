package com.example.chaos.monkey.shopping.gateway;

import com.example.chaos.monkey.shopping.domain.Product;
import com.example.chaos.monkey.shopping.domain.ProductBuilder;
import com.example.chaos.monkey.shopping.domain.ProductCategory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/hotdeals**").filters(f ->
						f.hystrix(c -> c.setName("hotdeals").setFallbackUri("forward:/fallback")))
						.uri("lb://hot-deals"))
				.route(p -> p.path("/fashion/**").filters(f ->
						f.hystrix(c -> c.setName("fashion").setFallbackUri("forward:/fallback")))
						.uri("lb://fashion-bestseller"))
				.route(p -> p.path("/toys/**").filters(f ->
						f.hystrix(c -> c.setName("toys").setFallbackUri("forward:/fallback")))
						.uri("lb://toys-bestseller"))
				.build();
	}

	@GetMapping("/fallback")
	public ResponseEntity<List<Product>> fallback() {
		System.out.println("fallback enabled");
		HttpHeaders headers = new HttpHeaders();
		headers.add("fallback", "true");
		ProductBuilder productBuilder = new ProductBuilder();

		Product cachedResponse = productBuilder.setCategory(ProductCategory.BOOKS).setId(1L).setName("Cached Product")
				.createProduct();

		return ResponseEntity.ok().headers(headers).body(Collections.singletonList(cachedResponse));
	}

}

@Configuration
@Profile("cloud")
@EnableWebFluxSecurity
class SecurityConfiguration  {

	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		return http
				.csrf().disable()
				.authorizeExchange().anyExchange().permitAll()
				.and()
				.httpBasic().disable()
				.build();
	}

}

