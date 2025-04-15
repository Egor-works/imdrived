package ru.eaosipov.imdrived.app.src.kotlin.client.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.BookingWithCar
import java.text.SimpleDateFormat
import java.util.*

/**
 * BookingAdapter — адаптер для отображения списка бронирований в RecyclerView.
 * Каждый элемент списка содержит информацию о бронировании автомобиля,
 * включая марку и модель автомобиля, даты начала и завершения аренды.
 * При нажатии на элемент списка вызывается переданный колбэк `onItemClick`.
 */
class BookingAdapter(private val onItemClick: (BookingWithCar) -> Unit) :
    ListAdapter<BookingWithCar, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    /**
     * Создает новый ViewHolder для элемента списка.
     * @param parent Родительский ViewGroup, в который будет добавлен новый View.
     * @param viewType Тип View (не используется в данном случае).
     * @return Новый экземпляр BookingViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        // Инфлейтим макет элемента списка (item_booking.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    /**
     * Привязывает данные бронирования к ViewHolder.
     * @param holder ViewHolder, к которому привязываются данные.
     * @param position Позиция элемента в списке.
     */
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * BookingViewHolder — внутренний класс, представляющий ViewHolder для элемента списка.
     * Содержит ссылки на TextView для отображения информации о бронировании.
     */
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCarInfo: TextView = itemView.findViewById(R.id.tvCarInfo)
        private val tvDateInfo: TextView = itemView.findViewById(R.id.tvDateInfo)

        /**
         * Привязывает данные бронирования к View.
         * @param bookingWithCar Объект, содержащий информацию о бронировании и автомобиле.
         */
        fun bind(bookingWithCar: BookingWithCar) {
            val car = bookingWithCar.car
            val booking = bookingWithCar.booking

            // Форматирование даты для отображения
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

            // Получаем даты начала и завершения аренды
            val startDate = dateFormat.format(Date(booking.startDate))
            val endDate = dateFormat.format(Date(booking.endDate))

            // Отображаем марку и модель автомобиля
            tvCarInfo.text = "${car.brand} ${car.model}"

            // Проверяем, завершено ли бронирование
            if (booking.isEnded) {
                // Если завершено, показываем дату завершения
                tvDateInfo.text = "Аренда завершено, $endDate"
            } else {
                // Если не завершено, показываем дату начала
                tvDateInfo.text = "Начало аренды: $startDate"
            }

            // Устанавливаем обработчик клика на элемент списка
            itemView.setOnClickListener {
                onItemClick(bookingWithCar)
            }
        }
    }
}

/**
 * BookingDiffCallback — класс для сравнения элементов списка бронирований.
 * Используется DiffUtil для оптимизации обновления RecyclerView.
 */
class BookingDiffCallback : DiffUtil.ItemCallback<BookingWithCar>() {
    /**
     * Проверяет, являются ли два элемента одним и тем же объектом.
     * @param oldItem Старый элемент.
     * @param newItem Новый элемент.
     * @return true, если идентификаторы бронирований совпадают.
     */
    override fun areItemsTheSame(oldItem: BookingWithCar, newItem: BookingWithCar): Boolean {
        return oldItem.booking.id == newItem.booking.id
    }

    /**
     * Проверяет, совпадают ли содержимое двух элементов.
     * @param oldItem Старый элемент.
     * @param newItem Новый элемент.
     * @return true, если содержимое элементов идентично.
     */
    override fun areContentsTheSame(oldItem: BookingWithCar, newItem: BookingWithCar): Boolean {
        return oldItem == newItem
    }
}