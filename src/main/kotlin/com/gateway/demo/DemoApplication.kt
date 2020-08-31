package com.gateway.demo

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class AppConfiguration {

	@Bean
	@Qualifier("myClient")
	fun myClient(
			webClientBuilder: WebClient.Builder
	) = webClientBuilder
			.baseUrl("http://test-backend:8080")
			.build()
}

@Component
class BadFilter(val myClient: WebClient) : GatewayFilter {
	override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
		return myClient.get().uri("/user/{userId}", "o13ht9jwei81m6uboo72wqkf31gcm7ay")
				.retrieve()
				.bodyToMono(String::class.java)
//		return Mono.error<String> { IllegalStateException("My ex") }
//				.onErrorResume { throwable -> exchange.request.body.map(DataBufferUtils::release).then(Mono.error(throwable)) }
				.flatMap { Mono.defer { chain.filter(exchange) } }
	}
}

@Configuration
class Routes {

	@Bean
	fun routeLocator(
			builder: RouteLocatorBuilder,
			context: ConfigurableApplicationContext
	): RouteLocator {
		return builder.routes {
			route("all") {
				path("/**")
				filters {
					filter(context.getBean(BadFilter::class.java))
				}
				uri("https://httpbin.org/status/204")
			}
		}
	}
}


@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
