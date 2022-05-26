package uk.gov.dluhc.eip.usermanagement.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import uk.gov.dluhc.eip.usermanagement.cognito.UserPool
import uk.gov.dluhc.eip.usermanagement.cognito.UserPool.DLUHC
import uk.gov.dluhc.eip.usermanagement.cognito.UserPool.ERO
import java.util.*

/**
 * Configuration class exposing beans for the LocalStack (AWS) environment
 */
@Configuration
class LocalStackContainerConfiguration {

	private companion object {
		val objectMapper = ObjectMapper()
	}

	/**
	 * Creates and starts LocalStack configured with basic (empty) Cognito and S3 AWS services.
	 * Returns the container that can subsequently be used for further setup and configuration.
	 */
	@Bean
	fun localstackContainer(): GenericContainer<*> {
		return GenericContainer(
				DockerImageName.parse("localstack/localstack:latest")
		).withEnv(mapOf(
				"SERVICES" to "cognito, s3",
				"AWS_DEFAULT_REGION" to "eu-west-2",
				"LOCALSTACK_API_KEY" to "1yheM9LVe3"
		)).withExposedPorts(4566)
				.apply {
					start()
				}
	}

	/**
	 * Uses the localstack container to configure the various services.
	 * @return a [LocalStackContainerSettings] bean encapsulating the various IDs etc of the configured container and services.
	 */
	@Bean
	fun localStackContainerSettings(localStackContainer: GenericContainer<*>): LocalStackContainerSettings {
		val eroUserPoolId = localStackContainer.createCognitoUserPool("ero")
		val eroClientId = localStackContainer.createCognitoUserPoolClient(eroUserPoolId, "ero")
		val dluchUserPoolId = localStackContainer.createCognitoUserPool("dluch")
		val dluchClientId = localStackContainer.createCognitoUserPoolClient(dluchUserPoolId, "dluch")

		val apiUrl = "http://${localStackContainer.host}:${localStackContainer.getMappedPort(4566)}"

		return LocalStackContainerSettings(
				apiUrl = apiUrl,
				userPoolCognitoSettings = mapOf(
						ERO to Cognito(eroUserPoolId, eroClientId, "$apiUrl/$eroUserPoolId/.well-known/jwks.json"),
						DLUHC to Cognito(dluchUserPoolId, dluchClientId, "$apiUrl/$dluchUserPoolId/.well-known/jwks.json"),
				)
		)
	}

	private fun GenericContainer<*>.createCognitoUserPool(userPoolName: String): String {
		return execInContainer(
				"awslocal", "cognito-idp", "create-user-pool", "--pool-name", userPoolName
		).stdout.let {
			objectMapper.readValue(it, Map::class.java)
		}.let {
			(it["UserPool"] as Map<String, String>)["Id"]!!
		}
	}

	private fun GenericContainer<*>.createCognitoUserPoolClient(userPoolId: String, userPoolName: String): String =
			execInContainer(
					"awslocal", "cognito-idp", "create-user-pool-client", "--client-name", "$userPoolName-client", "--user-pool-id", userPoolId
			).stdout.let {
				objectMapper.readValue(it, Map::class.java)
			}.let {
				(it["UserPoolClient"] as Map<String, String>)["ClientId"]!!
			}

	private fun GenericContainer<*>.createS3Bucket(bucketName: String) =
			execInContainer(
					"awslocal", "s3", "mb", "s3://$bucketName"
			)
}

data class LocalStackContainerSettings(
		val apiUrl: String,
		val userPoolCognitoSettings: Map<UserPool, Cognito>,
)

data class Cognito(
		val userPoolId: String,
		val clientId: String,
		val jwtIssuerUri: String,
)