package uk.gov.dluhc.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import uk.gov.dluhc.demo.service.PersonService

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private lateinit var personService: PersonService

	@Test
	fun `create, update and delete a Person`() {
		val person = personService.createPerson("Nathan", "Any Street", "Any District", "Any Town", "XX11 1XX")
		println(person)

		val updatedPerson = person.copy(
				line1 = "31 Devon Street",
				town = "Brixham"
		)
		personService.savePerson(updatedPerson)
		println(updatedPerson)

		personService.deletePerson(updatedPerson)
	}

}
