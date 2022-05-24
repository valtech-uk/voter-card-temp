package uk.gov.dluhc.eip.usermanagement.rest

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@RestController
class UserManagementController {

	@GetMapping("/user")
	fun getCurrentUser(authentication: Authentication): Mono<UserResponse> {
		return Mono.just(UserResponse(
				userId = authentication.name!!,
				roles = authentication.authorities.map { it.authority }.toSet()
		))
	}
}

data class UserResponse(
		val userId: String,
		val roles: Set<String>
)
