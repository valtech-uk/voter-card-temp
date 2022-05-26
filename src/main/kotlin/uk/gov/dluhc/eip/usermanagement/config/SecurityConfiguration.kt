package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(private val jwtConfig: JwtConfig, private val jwtAuthenticationConverter: JwtAuthenticationConverter) {

	@Bean
	fun SecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
			http.apply {
				exceptionHandling {
					it.authenticationEntryPoint { exchange, ex -> Mono.fromRunnable { exchange.response.statusCode = UNAUTHORIZED } }
					it.accessDeniedHandler { exchange, ex -> Mono.fromRunnable { exchange.response.statusCode = FORBIDDEN } }
				}
				formLogin { it.disable() }
				httpBasic { it.disable() }
				authorizeExchange {
					it.pathMatchers(OPTIONS).permitAll()
					it.anyExchange().authenticated()
				}
				oauth2ResourceServer {
					it.jwt {
						it.jwtAuthenticationConverter(jwtAuthenticationConverter)
						it.jwkSetUri(jwtConfig.eroJwtIssuerUri)
					}
				}
			}.build()
}
