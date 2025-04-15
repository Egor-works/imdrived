package ru.eaosipov.imdrived.app.src.kotlin.client.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car

/**
 * Адаптер для отображения списка автомобилей в RecyclerView.
 * @param onItemClick лямбда-функция, вызываемая при клике на элементы (принимает Car и Action)
 */
class CarAdapter(private val onItemClick: (Car, Action) -> Unit) :
    ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    /**
     * Enum для типов действий с автомобилем:
     * BOOK - бронирование
     * DETAILS - просмотр деталей
     */
    enum class Action { BOOK, DETAILS }

    /**
     * Создает новый ViewHolder при необходимости.
     * @param parent родительская ViewGroup
     * @param viewType тип View (не используется в данном случае)
     * @return новый экземпляр CarViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        // Инфлейтим макет элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    /**
     * Привязывает данные автомобиля к ViewHolder.
     * @param holder ViewHolder для заполнения
     * @param position позиция в списке данных
     */
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder для отображения данных одного автомобиля.
     * @param itemView корневая View элемента списка
     */
    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // View элементы из макета
        private val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        private val tvCarTitle: TextView = itemView.findViewById(R.id.tvCarTitle)
        private val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice)
        private val tvCarSpecs: TextView = itemView.findViewById(R.id.tvCarSpecs)
        private val btnBook: Button = itemView.findViewById(R.id.btnBook)
        private val btnDetails: Button = itemView.findViewById(R.id.btnDetails)

        /**
         * Привязывает данные автомобиля к View элементам.
         * @param car объект с данными автомобиля
         */
        fun bind(car: Car) {
            // Загружаем изображение автомобиля с помощью Picasso
            Picasso.get()
                .load(car.imageUri)
                .placeholder(R.drawable.ic_car) // Заглушка при загрузке
                .into(ivCarImage)

            // Устанавливаем текстовые данные
            tvCarTitle.text = "${car.brand} ${car.model}"
            tvCarPrice.text = "Цена: ${car.pricePerHour} руб/час"
            tvCarSpecs.text = "${car.fuelType}, ${car.transmissionType}"

            // Обработчики кликов на кнопки
            btnBook.setOnClickListener { onItemClick(car, Action.BOOK) }
            btnDetails.setOnClickListener { onItemClick(car, Action.DETAILS) }
        }
    }
}

/**
 * Callback для сравнения элементов списка при обновлении данных.
 */
class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
    /**
     * Проверяет, ссылаются ли объекты на один и тот же элемент.
     * @param oldItem старый элемент
     * @param newItem новый элемент
     * @return true если id элементов совпадают
     */
    override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Проверяет, одинаково ли содержимое элементов.
     * @param oldItem старый элемент
     * @param newItem новый элемент
     * @return true если содержимое элементов идентично
     */
    override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem == newItem
    }
}