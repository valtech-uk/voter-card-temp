package uk.gov.dluhc.eip.usermanagement.cognito

/**
 * Enumeration of Cognito User Pools that the system supports.
 * Different classes of user (DLUHC and ERO users) are managaed in different Cognito User Pools.
 */
enum class UserPool {
	ERO,
	DLUHC
}