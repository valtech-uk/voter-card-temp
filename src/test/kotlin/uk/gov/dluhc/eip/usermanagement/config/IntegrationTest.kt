package uk.gov.dluhc.eip.usermanagement.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Base class used to bring up the entire Spring ApplicationContext including web context and server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal abstract class IntegrationTest {

	@Autowired
	protected lateinit var webTestClient: WebTestClient

}
