package uk.gov.dluhc.eip.usermanagement.rest

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@RestController
class UserManagementController {

	@GetMapping("/user/roles")
	fun getRoles(authentication: Authentication): Flux<String> {
		return authentication.authorities.map { it.authority }.toFlux()
	}
}