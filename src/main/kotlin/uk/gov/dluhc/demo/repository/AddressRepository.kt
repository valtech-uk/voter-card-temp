package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Repository
interface AddressRepository : JpaRepository<Address, UUID>

@Table
@Entity
@Audited
data class Address(
    val line1: String,

    val line2: String,

    val town: String,

    val postcode: String,

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val personId: UUID? = null,
)
