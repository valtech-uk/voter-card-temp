package uk.gov.dluhc.eip.usermanagement.rest

import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import uk.gov.dluhc.eip.usermanagement.config.IntegrationTest
import uk.gov.dluhc.eip.usermanagement.models.UserEro

internal class UserManagementControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `should get current user given request with a valid jwt`() {
		// Given
		cognitoService.createGroup("ero-admin-1234")
		val cognitoUser = cognitoService.createUser(username = "user@wiltshire.gov.uk", groups = setOf("ero-admin-1234"))
		val bearerToken = cognitoService.authenticateUser(cognitoUser)

		val expectedResponseBody = listOf(UserEro("1234", "TBC"))

		// When / Then
		webTestClient.get()
				.uri("/usermanagement/ero/user")
				.headers {
					it["authorization"] = listOf("Bearer $bearerToken")
				}
				.exchange()
				.expectStatus().isOk
				.expectBodyList<UserEro>()
				.isEqualTo<WebTestClient.ListBodySpec<UserEro>>(expectedResponseBody)
	}

	@Test
	fun `should not get current user given request with no authorisation header`() {
		// Given

		// When / Then
		webTestClient.get()
				.uri("/usermanagement/ero/user")
				.exchange()
				.expectStatus().isUnauthorized
	}

	@Test
	fun `should get current user roles given request with a jwt whose signature cannot be verified`() {
		// Given

		// When / Then
		webTestClient.get()
				.uri("/usermanagement/ero/user")
				.headers {
					it["authorization"] = listOf("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQHdpbHRzaGlyZS5nb3YudWsiLCJpYXQiOjE1MTYyMzkwMjIsImF1dGhvcml0aWVzIjpbImVyby1hZG1pbiJdfQ.-pxW8z2xb-AzNLTRP_YRnm9fQDcK6CLt6HimtS8VcDY")
				}
				.exchange()
				.expectStatus().isUnauthorized
	}
}