package uk.gov.dluhc.eip.usermanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class UserManagementApplication

fun main(args: Array<String>) {
	runApplication<UserManagementApplication>(*args)
}
