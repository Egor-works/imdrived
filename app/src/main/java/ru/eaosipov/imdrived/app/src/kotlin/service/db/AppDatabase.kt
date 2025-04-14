package ru.eaosipov.imdrived.app.src.kotlin.service.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.dao.BookingDao
import ru.eaosipov.imdrived.app.src.kotlin.service.dao.CarDao
import ru.eaosipov.imdrived.app.src.kotlin.service.dao.CarDetailsDao
import ru.eaosipov.imdrived.app.src.kotlin.service.dao.UserRegistrationDao
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Booking
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.CarDetailsPartial
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData

@Database(entities = [UserRegistrationData::class, Car::class, CarDetailsPartial::class, Booking::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userRegistrationDao(): UserRegistrationDao
    abstract fun carDao(): CarDao
    abstract fun carDetailsDao(): CarDetailsDao
    abstract fun bookingDao(): BookingDao // Добавляем DAO для бронирований



    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 🔧 Миграция с версии 4 на версию 5, создаёт таблицу car_details
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Добавляем таблицу bookings при миграции
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `bookings` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`car_id` INTEGER NOT NULL, " +
                            "`user_id` INTEGER NOT NULL, " +
                            "`start_date` INTEGER NOT NULL, " +
                            "`end_date` INTEGER NOT NULL, " +
                            "`total_price` INTEGER NOT NULL, " +
                            "`insurance_price` INTEGER NOT NULL, " +
                            "`deposit` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cars ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_car_db.db"
                )
                    .createFromAsset("databases/my_car_db.db")
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration() // 💥 ключевая строка
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}