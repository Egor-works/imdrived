package ru.eaosipov.imdrived.app.src.kotlin.client.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import com.squareup.picasso.Picasso

/**
 * Адаптер для отображения списка избранных автомобилей в RecyclerView.
 * @param onItemClick Лямбда-функция, вызываемая при взаимодействии с элементами списка.
 */
class FavoriteCarAdapter(
    private val onItemClick: (Car, Action) -> Unit
) : RecyclerView.Adapter<FavoriteCarAdapter.CarViewHolder>() {

    /**
     * Действия, которые можно выполнить с автомобилем:
     * - BOOK: переход к бронированию.
     * - DETAILS: просмотр деталей автомобиля.
     */
    enum class Action { BOOK, DETAILS }

    private var cars: List<Car> = listOf() // Список автомобилей для отображения

    /**
     * Обновляет список автомобилей и уведомляет адаптер об изменениях.
     * @param cars Новый список автомобилей.
     */
    fun submitList(cars: List<Car>) {
        this.cars = cars
        notifyDataSetChanged() // Обновляем весь список
    }

    /**
     * Создает новый ViewHolder для элемента списка.
     * @param parent Родительская ViewGroup.
     * @param viewType Тип View (не используется в данном случае).
     * @return Новый экземпляр CarViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        // Инфлейтим макет элемента списка из XML
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_car, parent, false)
        return CarViewHolder(view)
    }

    /**
     * Привязывает данные автомобиля к ViewHolder.
     * @param holder ViewHolder, к которому привязываются данные.
     * @param position Позиция элемента в списке.
     */
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.bind(car) // Заполняем View данными автомобиля
    }

    /**
     * Возвращает количество элементов в списке.
     * @return Размер списка автомобилей.
     */
    override fun getItemCount(): Int = cars.size

    /**
     * ViewHolder для отображения данных об автомобиле.
     * @param itemView Корневой View элемента списка.
     */
    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage) // Изображение автомобиля
        private val tvCarTitle: TextView = itemView.findViewById(R.id.tvCarTitle) // Название автомобиля
        private val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice) // Цена аренды
        private val tvCarSpecs: TextView = itemView.findViewById(R.id.tvCarSpecs) // Характеристики автомобиля
        private val btnBook: Button = itemView.findViewById(R.id.btnBook) // Кнопка бронирования
        private val btnDetails: Button = itemView.findViewById(R.id.btnDetails) // Кнопка деталей

        /**
         * Заполняет View данными автомобиля.
         * @param car Автомобиль, данные которого нужно отобразить.
         */
        fun bind(car: Car) {
            // Загружаем изображение автомобиля с помощью Picasso
            Picasso.get()
                .load(car.imageUri)
                .placeholder(R.drawable.ic_car) // Заглушка, если изображение не загружено
                .into(ivCarImage)

            // Устанавливаем название автомобиля
            tvCarTitle.text = "${car.brand} ${car.model}"
            // Устанавливаем цену аренды
            tvCarPrice.text = "Цена: ${car.pricePerHour} руб/час"
            // Устанавливаем характеристики (например, тип топлива и коробки передач)
            tvCarSpecs.text = "${car.fuelType}, ${car.transmissionType}"

            // Обработчик нажатия на кнопку бронирования
            btnBook.setOnClickListener {
                onItemClick(car, Action.BOOK) // Передаем действие BOOK
            }

            // Обработчик нажатия на кнопку деталей
            btnDetails.setOnClickListener {
                onItemClick(car, Action.DETAILS) // Передаем действие DETAILS
            }
        }
    }
}