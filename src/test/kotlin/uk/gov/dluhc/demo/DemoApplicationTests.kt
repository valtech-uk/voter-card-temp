package uk.gov.dluhc.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import uk.gov.dluhc.demo.repository.Address
import uk.gov.dluhc.demo.service.PersonService

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private lateinit var personService: PersonService

    @Test
    fun `create, update and delete a Person`() {
        val address1 = Address("Any Street", "Any District", "Any Town", "XX11 1XX")
        val address2 = Address("Any Street 2", "Any District 2", "Any Town 2", "ABD CED")
        val address3 = Address("Any Street 3", "Any District 3", "Any Town 3", "ABD CED 3")
        val address4 = Address("Any Street 4", "Any District 4", "Any Town 4", "ABD CED 4")
        val person = personService.createPerson("Nathan", listOf(address1, address2))
        println(person)

        val updatedPerson = person.copy(
            name = "Updated name",
            addresses = listOf(address3, address4)
        )
        personService.savePerson(updatedPerson)
        println(updatedPerson)

        personService.deletePerson(updatedPerson)
    }

}
