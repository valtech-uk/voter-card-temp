package uk.gov.dluhc.demo.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.demo.repository.Person
import uk.gov.dluhc.demo.repository.PersonRepository
import javax.transaction.Transactional

@Service
class PersonService(private val personRepository: PersonRepository){

	fun createPerson(name: String, line1: String, line2: String, town: String, postcode: String): Person {
		val person = Person(
				name = name,
				line1 = line1,
				line2 = line2,
				town = town,
				postcode = postcode
		)
		return savePerson(person)
	}

	@Transactional
	fun savePerson(person: Person): Person {
		return personRepository.save(person)
	}

	@Transactional
	fun deletePerson(person: Person) {
		personRepository.delete(person)
	}
}
