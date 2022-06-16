package uk.gov.dluhc.demo.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.demo.repository.Person
import uk.gov.dluhc.demo.repository.PersonRepository
import javax.transaction.Transactional

@Service
class PersonService(
    private val personRepository: PersonRepository
) {
    fun createPerson(person: Person): Person {
        personRepository.save(person.copy(address = null))
        return savePerson(person)
    }

    @Transactional
    fun savePerson(person: Person): Person {
        return personRepository.save(person.copy(address = person.address?.copy(personId = person.personId)))
    }

    @Transactional
    fun deletePerson(person: Person) {
        personRepository.delete(person.copy(address = null))
    }
}
