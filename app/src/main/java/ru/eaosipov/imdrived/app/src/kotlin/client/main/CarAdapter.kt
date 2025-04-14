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

class CarAdapter(private val onItemClick: (Car, Action) -> Unit) :
    ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    enum class Action { BOOK, DETAILS }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        private val tvCarTitle: TextView = itemView.findViewById(R.id.tvCarTitle)
        private val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice)
        private val tvCarSpecs: TextView = itemView.findViewById(R.id.tvCarSpecs)
        private val btnBook: Button = itemView.findViewById(R.id.btnBook)
        private val btnDetails: Button = itemView.findViewById(R.id.btnDetails)

        fun bind(car: Car) {
            // Загружаем изображение через Glide (работает с URI assets)
            Picasso.get()
                .load(car.imageUri)
                .placeholder(R.drawable.ic_car) // Изображение-заглушка
                //.error(R.drawable.error_image) // Изображение при ошибке загрузки
                .into(ivCarImage)
            tvCarTitle.text = "${car.brand} ${car.model}"
            tvCarPrice.text = "Цена: ${car.pricePerHour} руб/час"
            tvCarSpecs.text = "${car.fuelType}, ${car.transmissionType}"

            btnBook.setOnClickListener { onItemClick(car, Action.BOOK) }
            btnDetails.setOnClickListener { onItemClick(car, Action.DETAILS) }
        }
    }
}

class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
    override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem == newItem
    }
}
