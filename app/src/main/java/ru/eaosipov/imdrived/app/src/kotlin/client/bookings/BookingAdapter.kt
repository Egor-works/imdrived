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

class BookingAdapter(private val onItemClick: (BookingWithCar) -> Unit) :
    ListAdapter<BookingWithCar, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCarInfo: TextView = itemView.findViewById(R.id.tvCarInfo)
        private val tvDateInfo: TextView = itemView.findViewById(R.id.tvDateInfo)

        fun bind(bookingWithCar: BookingWithCar) {
            val car = bookingWithCar.car
            val booking = bookingWithCar.booking

            // Формат даты
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

            /// Даты начала и завершения аренды
            val startDate = dateFormat.format(Date(booking.startDate))
            val endDate = dateFormat.format(Date(booking.endDate))

            // Показываем марку и модель автомобиля
            tvCarInfo.text = "${car.brand} ${car.model}"

            // В случае завершенного бронирования, показываем информацию о дате завершения
            if (booking.isEnded) {
                tvDateInfo.text = "Аренда завершено, $endDate"
                //tvDateInfo.visibility = View.VISIBLE
            } else {
                tvDateInfo.text = "Начало аренды: $startDate"
                //tvDateInfo.visibility = View.GONE
            }

            tvCarInfo.text = "${car.brand} ${car.model}"

            itemView.setOnClickListener {
                onItemClick(bookingWithCar)
            }
        }
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<BookingWithCar>() {
    override fun areItemsTheSame(oldItem: BookingWithCar, newItem: BookingWithCar): Boolean {
        return oldItem.booking.id == newItem.booking.id
    }
    override fun areContentsTheSame(oldItem: BookingWithCar, newItem: BookingWithCar): Boolean {
        return oldItem == newItem
    }
}
