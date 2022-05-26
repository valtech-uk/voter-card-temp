package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import uk.gov.dluhc.eip.usermanagement.cognito.UserPool.ERO
import java.util.*

@Configuration
class TestPropertySourcesConfiguration {

	@Bean
	fun propertySourcesPlaceholderConfigurer(localStackContainerSettings: LocalStackContainerSettings): PropertySourcesPlaceholderConfigurer =
			PropertySourcesPlaceholderConfigurer().apply {
				setLocalOverride(true)
				setProperties(Properties().apply {
					setProperty("jwt.publicKey", localStackContainerSettings.userPoolCognitoSettings[ERO]!!.publicKey)
				})
			}


}