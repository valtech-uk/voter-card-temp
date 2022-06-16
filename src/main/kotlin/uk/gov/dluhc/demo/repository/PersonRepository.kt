package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Repository
interface PersonRepository : JpaRepository<Person, UUID> {
    override fun findById(id: UUID): Optional<Person>
}

@Entity
@Audited
data class Person(
    val name: String,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @PrimaryKeyJoinColumn
    val address: Address?,

    @Id
    @Type(type = "uuid-char")
    val personId: UUID = UUID.randomUUID(),
)

