package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Repository
interface PersonRepository : JpaRepository<Person, UUID>

@Entity
@Audited
data class Person(
    val name: String,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    val addresses: List<Address>,

    @Id
    @Type(type = "uuid-char")
    val id: UUID = UUID.randomUUID(),
)

