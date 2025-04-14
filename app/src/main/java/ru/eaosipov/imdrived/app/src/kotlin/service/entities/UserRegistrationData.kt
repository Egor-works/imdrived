package ru.eaosipov.imdrived.app.src.kotlin.service.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * UserRegistrationData - сущность для хранения данных регистрации пользователя.
 */
@Entity(tableName = "user_registration")
data class UserRegistrationData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,             // Электронная почта (например, из первого шага)
    val password: String,          // Пароль (если требуется)
    val firstName: String,         // Имя
    val lastName: String,          // Фамилия
    val middleName: String?,       // Отчество (необязательно)
    val birthDate: String,         // Дата рождения (например, в формате MM/dd/yyyy)
    val gender: String,            // Пол ("Мужской" или "Женский")
    val licenseNumber: String,     // Номер водительского удостоверения
    val issueDate: String,         // Дата выдачи (например, в формате DD/MM/yyyy)
    val licensePhotoUri: String,   // URI фото водительского удостоверения (сохраняется как строка)
    val passportPhotoUri: String,  // URI фото паспорта
    val profilePhotoUri: String?   // URI фото профиля (необязательное поле)
)