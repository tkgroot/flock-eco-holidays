package community.flock.eco.workday.controllers

import community.flock.eco.core.utils.toResponse
import community.flock.eco.feature.user.model.User
import community.flock.eco.feature.user.services.UserService
import community.flock.eco.workday.authorities.HolidayAuthority
import community.flock.eco.workday.forms.HolidayForm
import community.flock.eco.workday.model.Holiday
import community.flock.eco.workday.services.HolidayService
import community.flock.eco.workday.services.PersonService
import java.security.Principal
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/holidays")
class HolidayController(
    private val service: HolidayService,
    private val personService: PersonService,
    private val userService: UserService
) {
    @GetMapping
    @PreAuthorize("hasAuthority('HolidayAuthority.READ')")
    fun findAll(
        @RequestParam(required = false) personCode: String?,
        principal: Principal
    ): ResponseEntity<Iterable<Holiday>> = principal
        .findUser()
        ?.let {
            return@let when {
                it.isAdmin() && personCode != null -> service.findAllByPersonCode(personCode)
                else -> personService
                    .findByUserCode(it.code)
                    .run { service.findAllByPersonCode(this!!.code) }
            }
        }
        ?.toResponse()
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @GetMapping("/{code}")
    @PreAuthorize("hasAuthority('HolidayAuthority.READ')")
    fun findByCode(@PathVariable code: String, principal: Principal): ResponseEntity<Holiday> = principal
        .findUser()
        ?.let {
            service.findByCode(code)
        }.toResponse()

    @PostMapping
    @PreAuthorize("hasAuthority('HolidayAuthority.WRITE')")
    fun post(@RequestBody form: HolidayForm, principal: Principal): ResponseEntity<Holiday> = principal
        .findUser()
        ?.let {
            val personCode = when {
                it.isAdmin() -> form.personCode
                else -> personService.findByUserCode(it.code)?.code ?: throw ResponseStatusException(UNAUTHORIZED)
            }
            form.copy(personCode = personCode)
            return@let service.create(form).toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('HolidayAuthority.WRITE')")
    fun put(
        @PathVariable code: String,
        @RequestBody form: HolidayForm,
        principal: Principal
    ): ResponseEntity<Holiday> = principal
        .findUser()
        ?.let {
            val personCode = when {
                it.isAdmin() -> form.personCode
                else -> personService.findByUserCode(it.code)?.code ?: throw ResponseStatusException(UNAUTHORIZED)
            }
            form.copy(personCode = personCode)
            return@let service.update(code, form, it.isAdmin()).toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('HolidayAuthority.ADMIN')")
    fun delete(@PathVariable code: String, principal: Principal) = principal
        .findUser()
        ?.let {
            service.delete(code).toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    // *-- utility functions --*
    /**
     * add findUser() function to Principal
     * @return <code>User?</code> a user if found with given user code in the db
     */
    private fun Principal.findUser(): User? = userService.findByCode(this.name)

    /**
     * Evaluate if user has admin authorities on Holiday
     * @return <code>true</code> if user is admin or has admin authorities
     */
    private fun User.isAdmin(): Boolean = this.authorities.contains(HolidayAuthority.ADMIN.toName())
}
