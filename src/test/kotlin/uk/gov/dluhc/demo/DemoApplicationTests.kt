package uk.gov.dluhc.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import uk.gov.dluhc.demo.repository.Address
import uk.gov.dluhc.demo.repository.AddressRepository
import uk.gov.dluhc.demo.repository.Person
import uk.gov.dluhc.demo.repository.PersonRepository

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Test
    fun `create, update and delete a Person`() {
        val address1 = Address("Any Street", "Any District", "Any Town", "XX11 1XX")
        val person = personRepository.save(Person("Nathan", address1))

        println(person)
        assertPersisted(person)

        val address2 =
            Address("Any Street 2", "Any District 2", "Any Town 2", "ABD CED", person.personId)
        val updatedPerson = person.copy(
            name = "Updated name",
            address = address2
        )
        personRepository.save(updatedPerson)

        println(updatedPerson)
        assertPersisted(updatedPerson)

        personRepository.delete(updatedPerson)

        assertThat(personRepository.findById(updatedPerson.personId!!)).isNotPresent
        assertThat(addressRepository.findById(updatedPerson.personId!!)).isNotPresent
    }

    private fun assertPersisted(expected: Person) {
        val persisted = personRepository.findById(expected.personId!!)

        assertThat(persisted).isPresent
        assertThat(persisted.get()).usingRecursiveComparison().isEqualTo(expected)
    }

}
