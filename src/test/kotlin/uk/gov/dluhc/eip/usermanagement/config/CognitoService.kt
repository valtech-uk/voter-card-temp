package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType
import java.net.URI
import java.util.*

/**
 * Service class that uses the AWS SDK to perform cognito functions.
 */
@Service
class CognitoService(private val localStackContainerSettings: LocalStackContainerSettings) {

	private val cognitoClient = CognitoIdentityProviderClient.builder()
			.endpointOverride(URI.create(localStackContainerSettings.apiUrl))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
			.build()

	/**
	 * Create a cognito user.
	 * If password is not specified a random (UUID) password will be assigned.
	 * If groups are not specified the user will not be assigned to any groups. Any groups that are specified need to have been previously created
	 * via [CognitoService.createGroups] or [CognitoService.createGroup].
	 *
	 * @return a [CognitoUser] representing the created user, their password, and assigned groups.
	 */
	fun createUser(username: String, password: String = UUID.randomUUID().toString(), groups: Set<String> = emptySet()): CognitoUser =
			with(cognitoClient) {
				adminCreateUser {
					it.userPoolId(localStackContainerSettings.cognito.userPoolId)
							.username(username)
							.messageAction("SUPPRESS")
				}
				adminSetUserPassword {
					it.userPoolId(localStackContainerSettings.cognito.userPoolId)
							.username(username)
							.password(password)
							.permanent(true)
				}

				groups.forEach { groupName ->
					adminAddUserToGroup {
						it.userPoolId(localStackContainerSettings.cognito.userPoolId)
								.username(username)
								.groupName(groupName)
					}
				}

				CognitoUser(username, password, groups)
			}

	fun authenticateUser(cognitoUser: CognitoUser) = authenticateUser(cognitoUser.username, cognitoUser.password)

	fun authenticateUser(username: String, password: String): String {
		var authResponse = cognitoClient.initiateAuth {
			it.clientId(localStackContainerSettings.cognito.clientId)
					.authFlow(AuthFlowType.USER_PASSWORD_AUTH)
					.authParameters(mapOf(
							"USERNAME" to username,
							"PASSWORD" to password
					))
		}
		return authResponse.authenticationResult().accessToken()
	}

	/**
	 * Creates Cognito groups that can subsequently be used as part of creating a user.
	 */
	fun createGroups(groups: Set<String>) = groups.forEach { createGroup(it) }

	/**
	 * Creates a Cognito group that can subsequently be used as part of creating a user.
	 */
	fun createGroup(groupName: String) {
		cognitoClient.createGroup {
			it.userPoolId(localStackContainerSettings.cognito.userPoolId)
					.groupName(groupName)
		}
	}

}

/**
 * Simple class encapsulating the username, password and assigned groups of a Cognito user.
 */
data class CognitoUser(
		val username: String,
		val password: String,
		val groups: Set<String> = emptySet()
)
