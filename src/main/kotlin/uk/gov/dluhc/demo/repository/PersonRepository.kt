package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.persistence.*

@Repository
interface PersonRepository : JpaRepository<Person, UUID>

@Entity
@Audited
data class Person(
		val name: String,

		val line1: String,

		val line2: String,

		val town: String,

		val postcode: String,

		@Id
		@Type(type = "uuid-char")
		val id: UUID = UUID.randomUUID(),
)

