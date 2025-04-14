package ru.eaosipov.imdrived.app.src.kotlin.service.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Сущность Booking отражает данные о бронировании автомобиля:
 * содержит уникальный идентификатор, ссылки на автомобиль и пользователя,
 * а также сведения о сроках аренды и её стоимости.
 */
@Entity(tableName = "bookings")
data class Booking(
    /**
     * Уникальный идентификатор бронирования. Генерируется автоматически.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Идентификатор автомобиля, для которого создаётся бронирование.
     */
    @ColumnInfo(name = "car_id")
    val carId: Long,

    /**
     * Идентификатор пользователя. Может использоваться для хранения
     * связи между бронированием и конкретным клиентом.
     */
    @ColumnInfo(name = "user_id")
    val userId: Int,

    /**
     * Начальная дата аренды в формате Unix timestamp (миллисекунды).
     */
    @ColumnInfo(name = "start_date")
    val startDate: Long,

    /**
     * Финальная дата аренды в формате Unix timestamp (миллисекунды).
     */
    @ColumnInfo(name = "end_date")
    val endDate: Long,

    /**
     * Итоговая сумма оплаты за период аренды.
     */
    @ColumnInfo(name = "total_price")
    val totalPrice: Double,

    /**
     * Стоимость аренды за все дни без учёта дополнительных услуг.
     */
    @ColumnInfo(name = "car_price")
    val carPrice: Double,

    /**
     * Сколько дней длится аренда.
     */
    @ColumnInfo(name = "book_days")
    val bookDays: Int,

    /**
     * Размер страховки при аренде автомобиля.
     */
    @ColumnInfo(name = "insurance_price")
    val insurancePrice: Int,

    /**
     * Сумма залога, необходимого для оформления брони.
     */
    @ColumnInfo(name = "deposit")
    val deposit: Int,

    /**
     * Флаг, показывающий, завершено (или отменено) данное бронирование.
     */
    @ColumnInfo(name = "is_ended")
    val isEnded: Boolean = false
)