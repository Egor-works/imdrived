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

class FavoriteCarAdapter(
    private val onItemClick: (Car, Action) -> Unit
) : RecyclerView.Adapter<FavoriteCarAdapter.CarViewHolder>() {

    enum class Action { BOOK, DETAILS }

    private var cars: List<Car> = listOf()

    fun submitList(cars: List<Car>) {
        this.cars = cars
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.bind(car)
    }

    override fun getItemCount(): Int = cars.size

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        private val tvCarTitle: TextView = itemView.findViewById(R.id.tvCarTitle)
        private val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice)
        private val tvCarSpecs: TextView = itemView.findViewById(R.id.tvCarSpecs)
        private val btnBook: Button = itemView.findViewById(R.id.btnBook)
        private val btnDetails: Button = itemView.findViewById(R.id.btnDetails)

        fun bind(car: Car) {
            // Загружаем изображение через Picasso
            Picasso.get()
                .load(car.imageUri)
                .placeholder(R.drawable.ic_car)
                .into(ivCarImage)

            tvCarTitle.text = "${car.brand} ${car.model}"
            tvCarPrice.text = "Цена: ${car.pricePerHour} руб/час"
            // Пример: если у Car есть дополнительные характеристики:
            tvCarSpecs.text = "${car.fuelType}, ${car.transmissionType}"

            btnBook.setOnClickListener {
                onItemClick(car, Action.BOOK)
            }

            btnDetails.setOnClickListener {
                onItemClick(car, Action.DETAILS)
            }
        }
    }
}
