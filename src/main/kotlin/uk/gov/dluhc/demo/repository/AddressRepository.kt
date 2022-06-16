package uk.gov.dluhc.demo.repository

import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Repository
interface AddressRepository : JpaRepository<Address, UUID>

@Entity
@Audited
data class Address(
    val line1: String,

    val line2: String,

    val town: String,

    val postcode: String,

    @Id
    @Type(type = "uuid-char")
    val personId: UUID = UUID.randomUUID(),
)
