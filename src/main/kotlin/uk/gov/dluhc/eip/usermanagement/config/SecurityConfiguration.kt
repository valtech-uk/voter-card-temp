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
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(private val jwtConfig: JwtConfig, private val jwtAuthenticationConverter: JwtAuthenticationConverter) {

	@Bean
	fun SecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
			http
					.exceptionHandling()
					.authenticationEntryPoint { exchange, ex -> Mono.fromRunnable { exchange.response.statusCode = UNAUTHORIZED } }
					.accessDeniedHandler { exchange, ex -> Mono.fromRunnable { exchange.response.statusCode = FORBIDDEN } }
					.and().formLogin().disable()
					.httpBasic().disable()
					.authorizeExchange()
						.pathMatchers(OPTIONS).permitAll()
						.anyExchange().authenticated()
					.and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter)
					.publicKey(readPublicKey(jwtConfig.publicKey))
					.and()
					.and().build()

	private fun readPublicKey(publicKeyString: String): RSAPublicKey =
			X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString))
					.let { KeyFactory.getInstance("RSA").generatePublic(it) as RSAPublicKey }

}

