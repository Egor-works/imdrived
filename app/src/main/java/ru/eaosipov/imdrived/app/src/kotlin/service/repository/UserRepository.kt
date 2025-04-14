package ru.eaosipov.imdrived.app.src.kotlin.service.repository

import ru.eaosipov.imdrived.app.src.kotlin.service.dao.UserRegistrationDao
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData


class UserRepository(private val dao: UserRegistrationDao) {
    suspend fun insertUserData(userData: UserRegistrationData): Long {
        return dao.insert(userData)
    }
    suspend fun getUserByEmail(email: String): UserRegistrationData? {
        return dao.getUserByEmail(email)
    }
    suspend fun updateUser(user: UserRegistrationData) {
        dao.updateUser(user)
    }
}