package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType
import uk.gov.dluhc.eip.usermanagement.cognito.UserPool
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
	 * Create a cognito user in the specified user pool.
	 * If password is not specified a random (UUID) password will be assigned.
	 * If groups are not specified the user will not be assigned to any groups. Any groups that are specified need to have been previously created
	 * via [CognitoService.createGroups] or [CognitoService.createGroup].
	 *
	 * @return a [CognitoUser] representing the created user, their password, and assigned groups.
	 */
	fun createUser(userPool: UserPool, username: String, password: String = UUID.randomUUID().toString(), groups: Set<String> = emptySet()): CognitoUser =
			with(cognitoClient) {
				val userPoolId = when(userPool) {
					UserPool.ERO -> localStackContainerSettings.userPoolCognitoSettings[UserPool.ERO]!!.userPoolId
					UserPool.DLUHC -> localStackContainerSettings.userPoolCognitoSettings[UserPool.DLUHC]!!.userPoolId
				}
				adminCreateUser {
					it.userPoolId(userPoolId)
							.username(username)
							.messageAction("SUPPRESS")
				}
				adminSetUserPassword {
					it.userPoolId(userPoolId)
							.username(username)
							.password(password)
							.permanent(true)
				}

				groups.forEach { groupName ->
					adminAddUserToGroup {
						it.userPoolId(userPoolId)
								.username(username)
								.groupName(groupName)
					}
				}

				CognitoUser(userPool, username, password, groups)
			}

	fun authenticateUser(cognitoUser: CognitoUser) = authenticateUser(cognitoUser.userPool, cognitoUser.username, cognitoUser.password)

	fun authenticateUser(userPool: UserPool, username: String, password: String): String =
		cognitoClient.initiateAuth {
			val clientId = when(userPool) {
				UserPool.ERO -> localStackContainerSettings.userPoolCognitoSettings[UserPool.ERO]!!.clientId
				UserPool.DLUHC -> localStackContainerSettings.userPoolCognitoSettings[UserPool.DLUHC]!!.clientId
			}
			it.clientId(clientId)
					.authFlow(AuthFlowType.USER_PASSWORD_AUTH)
					.authParameters(mapOf(
							"USERNAME" to username,
							"PASSWORD" to password
					))
		}.authenticationResult().accessToken()


	/**
	 * Creates Cognito groups that can subsequently be used as part of creating a user.
	 */
	fun createGroups(userPool: UserPool, groups: Set<String>) = groups.forEach { createGroup(userPool, it) }

	/**
	 * Creates a Cognito group that can subsequently be used as part of creating a user.
	 */
	fun createGroup(userPool: UserPool, groupName: String) {
		cognitoClient.createGroup {
			val userPoolId = when(userPool) {
				UserPool.ERO -> localStackContainerSettings.userPoolCognitoSettings[UserPool.ERO]!!.userPoolId
				UserPool.DLUHC -> localStackContainerSettings.userPoolCognitoSettings[UserPool.DLUHC]!!.userPoolId
			}
			it.userPoolId(userPoolId)
					.groupName(groupName)
		}
	}

}

/**
 * Simple class encapsulating the username, password and assigned groups of a Cognito user.
 */
data class CognitoUser(
		val userPool: UserPool,
		val username: String,
		val password: String,
		val groups: Set<String> = emptySet()
)
