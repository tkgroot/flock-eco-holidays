package community.flock.eco.workday.services

import community.flock.eco.core.utils.toNullable
import community.flock.eco.feature.user.repositories.UserRepository
import community.flock.eco.workday.forms.PersonForm
import community.flock.eco.workday.model.Person
import community.flock.eco.workday.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersonService(
    private val repository: PersonRepository,
    private val userRepository: UserRepository
) {
    private fun Person.render(it: PersonForm): Person {
        val user = when (it.userCode) {
            is String -> userRepository.findByCode(it.userCode).toNullable()
            else -> null
        }

        return Person(
            id = this.id,
            code = this.code,
            firstname = it.firstname,
            lastname = it.lastname,
            email = it.email,
            position = it.position,
            user = user
        )
    }

    private fun Person.save(): Person = repository.save(this)

    fun findAll(pageable: Pageable): Page<Person> = repository
        .findAll(pageable)

    fun findByCode(code: String): Person? = repository
        .findByCode(code)

    fun findByUserCode(userCode: String) = repository
        .findByUserCode(userCode)

    fun create(form: PersonForm): Person? {
        val user = when (form.userCode) {
            is String -> userRepository
                .findByCode(form.userCode)
                .toNullable()
            else -> null
        }

        return Person(
            firstname = form.firstname,
            lastname = form.lastname,
            email = form.email,
            position = form.position,
            user = user
        ).save()
    }

    fun update(code: String, form: PersonForm): Person? {
        val obj = this.findByCode(code)

        return when (obj) {
            is Person -> obj.render(form).save()
            else -> null
        }
    }

    @Transactional
    fun deleteByCode(code: String) = repository.deleteByCode(code)
}
