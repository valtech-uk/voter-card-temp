package uk.gov.dluhc.demo.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.demo.repository.Address
import uk.gov.dluhc.demo.repository.Person
import uk.gov.dluhc.demo.repository.PersonRepository
import javax.transaction.Transactional

@Service
class PersonService(private val personRepository: PersonRepository){

	fun createPerson(name: String, addresses:List<Address>): Person {

		val person = Person(
				name = name,
				addresses= addresses
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
