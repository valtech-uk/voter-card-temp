package uk.gov.dluhc.eip.usermanagement.rest

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import uk.gov.dluhc.eip.usermanagement.models.UserEro

@RestController
@RequestMapping("/usermanagement")
class UserManagementController {

	@GetMapping("/ero/user")
	fun getCurrentUser(authentication: Authentication): Flux<UserEro> {
		return authentication.authorities.map {
			// extract the eroId from the authority name (cognito group)
			with(it.authority) {
				this.substring(this.lastIndexOf("-") + 1)
			}
		}.toSet().map {
			UserEro(eroId = it, eroName = "TBC") // lookup eroName here
		}.toFlux()
	}
}

