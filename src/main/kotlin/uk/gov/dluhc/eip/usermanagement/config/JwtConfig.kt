package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
data class JwtConfig(
		val eroJwtIssuerUri: String,
		val dluhcJwtIssuerUri: String,
)
