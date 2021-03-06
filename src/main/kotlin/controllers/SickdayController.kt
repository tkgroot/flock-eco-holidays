package community.flock.eco.workday.controllers

import community.flock.eco.core.utils.toResponse
import community.flock.eco.feature.user.model.User
import community.flock.eco.feature.user.services.UserService
import community.flock.eco.workday.authorities.SickdayAuthority
import community.flock.eco.workday.forms.SickdayForm
import community.flock.eco.workday.model.Sickday
import community.flock.eco.workday.model.SickdayStatus
import community.flock.eco.workday.services.PersonService
import community.flock.eco.workday.services.SickdayService
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
@RequestMapping("/api/sickdays")
class SickdayController(
    private val service: SickdayService,
    private val personService: PersonService,
    private val userService: UserService
) {
    @GetMapping
    @PreAuthorize("hasAuthority('SickdayAuthority.READ')")
    fun getAll(
        @RequestParam(required = false) status: SickdayStatus?,
        @RequestParam(required = false) code: String?,
        principal: Principal
    ): ResponseEntity<Iterable<Sickday>> = principal
        .findUser()
        ?.let {
            return@let when {
                it.isAdmin() -> service.findAll(status, code)
                else -> {
                    val person = personService.findByUserCode(it.code) ?: throw ResponseStatusException(UNAUTHORIZED)
                    service.findAll(status, person.code)
                }
            }.toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @GetMapping("/{code}")
    @PreAuthorize("hasAuthority('SickdayAuthority.READ')")
    fun findByCode(@PathVariable code: String, principal: Principal): ResponseEntity<Sickday> = principal
        .findUser()
        ?.let {
            return@let when {
                it.isAdmin() -> service.findByCode(code)
                else -> {
                    val person = personService.findByUserCode(it.code) ?: throw ResponseStatusException(UNAUTHORIZED)
                    service.findByCode(person.code)
                }
            }.toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @PostMapping
    @PreAuthorize("hasAuthority('SickdayAuthority.WRITE')")
    fun post(@RequestBody form: SickdayForm, principal: Principal) = principal
        .findUser()
        ?.let {
            // if user has no Admin Authorities the forms personCode is updated to the personCode of the posting user
            // meaning that the user can only create its own sickdays, preventing creations of other users sickdays
            if (it.isNotAdmin()) {
                val person = personService.findByUserCode(it.code) ?: throw ResponseStatusException(UNAUTHORIZED)
                form.copy(personCode = person.code)
            }
            return@let service.create(form).toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('SickdayAuthority.WRITE')")
    fun put(
        @PathVariable code: String,
        @RequestBody form: SickdayForm,
        principal: Principal
    ) = principal
        .findUser()
        ?.let {
            // if user has no Admin Authorities the forms personCode is updated to the personCode of the posting user
            // meaning that the user can only update its own sickdays, preventing updating of other users sickdays
            if (it.isNotAdmin()) {
                val person = personService.findByUserCode(it.code) ?: throw ResponseStatusException(UNAUTHORIZED)
                form.copy(personCode = person.code)
            }
            return@let service.update(code, form).toResponse()
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('SickdayAuthority.ADMIN')")
    fun delete(@PathVariable code: String, principal: Principal) = principal
        .findUser()
        ?.let {
            when {
                it.isAdmin() -> return@let service.delete(code).toResponse()
                else -> throw ResponseStatusException(UNAUTHORIZED)
            }
        }
        ?: throw ResponseStatusException(UNAUTHORIZED)

    // *-- utility functions --*
    /**
     * add findUser() function to Principal
     * @return <code>User?</code> a user if found with given user code in the db
     */
    private fun Principal.findUser(): User? = userService.findByCode(this.name)

    /**
     * Evaluate if user has admin authorities on Sickday
     * @return <code>true</code> if user is admin or has admin authorities
     */
    private fun User.isAdmin(): Boolean = this.authorities.contains(SickdayAuthority.ADMIN.toName())

    /**
     * Evaluate if user has no admin authorities on Sickday
     * @return <code>true</code> if user is not admin or has admin authorities
     */
    private fun User.isNotAdmin(): Boolean = !this.isAdmin()
}
