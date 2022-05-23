package uk.gov.dluhc.eip.usermanagement.rest

import org.junit.jupiter.api.Test
import uk.gov.dluhc.eip.usermanagement.config.IntegrationTest

internal class UserManagementControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `should get user roles given request with a valid jwt`() {
		// Given

		// When / Then
		webTestClient.get()
				.uri("/user/roles")
				.headers {
					it["authorization"] = listOf("Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQHdpbHRzaGlyZS5nb3YudWsiLCJpYXQiOjE1MTYyMzkwMjIsImF1dGhvcml0aWVzIjpbImVyby1hZG1pbiJdfQ.D34dNR4H7AafF5YKxicNQTOiPL63dbgMBsCmjEpYjBlgPSCVakRu4bdXDiGeZbrk5KRV92h7fuCwkwNM7GA-OyES3CV96qhT33S0rR1zFPAu2fd8yjqf_3Kat6p1L-9g4TLnFxBXVAuWdIMVYJNWKqvfo7J4Vk-QCchPulDoi4w")
				}
				.exchange()
				.expectStatus().isOk
				.expectBodyList(String::class.java).contains("ero-admin")
	}

	@Test
	fun `should not get user roles given request with no authorisation header`() {
		// Given

		// When / Then
		webTestClient.get()
				.uri("/user/roles")
				.exchange()
				.expectStatus().isUnauthorized
	}

	@Test
	fun `should not get user roles given request with a jwt whose signature cannot be verified`() {
		// Given

		// When / Then
		webTestClient.get()
				.uri("/user/roles")
				.headers {
					it["authorization"] = listOf("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQHdpbHRzaGlyZS5nb3YudWsiLCJpYXQiOjE1MTYyMzkwMjIsImF1dGhvcml0aWVzIjpbImVyby1hZG1pbiJdfQ.-pxW8z2xb-AzNLTRP_YRnm9fQDcK6CLt6HimtS8VcDY")
				}
				.exchange()
				.expectStatus().isUnauthorized
	}
}