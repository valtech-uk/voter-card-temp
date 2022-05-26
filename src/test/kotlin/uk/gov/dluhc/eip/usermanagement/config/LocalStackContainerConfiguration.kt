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
		val eroCognitoPublicKey = localStackContainer.getCognitoUserPoolPublicKey(eroUserPoolId)
		val dluchUserPoolId = localStackContainer.createCognitoUserPool("dluch")
		val dluchClientId = localStackContainer.createCognitoUserPoolClient(dluchUserPoolId, "dluch")
		val dluchCognitoPublicKey = localStackContainer.getCognitoUserPoolPublicKey(dluchUserPoolId)

		return LocalStackContainerSettings(
				apiUrl = "http://${localStackContainer.host}:${localStackContainer.getMappedPort(4566)}",
				userPoolCognitoSettings = mapOf(
						ERO to Cognito(eroUserPoolId, eroClientId, eroCognitoPublicKey),
						DLUHC to Cognito(dluchUserPoolId, dluchClientId, dluchCognitoPublicKey),
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

	private fun GenericContainer<*>.getCognitoUserPoolPublicKey(userPoolId: String): String {
		return execInContainer(
				"curl", "http://localhost:4566/$userPoolId/.well-known/jwks.json"
		).stdout.let {
			objectMapper.readValue(it, Map::class.java)
		}.let {
			(it["keys"] as List<Map<String, String>>)[0]
		}.let {
			RSAKey.parse(it)
		}.let {
			Base64.getEncoder().encodeToString(it.toPublicKey().encoded)
		}
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
		val publicKey: String,
)