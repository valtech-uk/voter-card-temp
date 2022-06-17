package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.GenericGenerator
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

@Table
@Entity
@Audited
data class Person(
    val name: String,

    @OneToOne(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JoinColumn(name = "person_id")
    @MapsId
    val address: Address? = null,

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val personId: UUID? = null
)

