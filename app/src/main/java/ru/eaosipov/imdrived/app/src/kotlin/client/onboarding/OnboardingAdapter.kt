package ru.eaosipov.imdrived.app.src.kotlin.client.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.eaosipov.imdrived.R

/**
 * OnboardingAdapter - адаптер для отображения элементов онбординга в RecyclerView.
 * Каждый элемент содержит изображение, заголовок и описание.
 */
class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    /**
     * OnboardingViewHolder - внутренний класс для хранения ссылок на элементы интерфейса.
     * @param itemView корневое View элемента списка.
     */
    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView для отображения изображения онбординга
        val imageView: ImageView = itemView.findViewById(R.id.ivOnboardingImage)
        // TextView для отображения заголовка онбординга
        val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        // TextView для отображения описания онбординга
        val descText: TextView = itemView.findViewById(R.id.tvDescription)
    }

    /**
     * Создает новый ViewHolder для элемента списка.
     * @param parent ViewGroup, в которую будет добавлен новый View.
     * @param viewType тип View (не используется в данном случае).
     * @return экземпляр OnboardingViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        // Надуваем макет элемента онбординга из XML
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    /**
     * Привязывает данные к ViewHolder на указанной позиции.
     * @param holder ViewHolder, который нужно обновить.
     * @param position позиция элемента в списке.
     */
    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        // Получаем элемент онбординга по позиции
        val item = items[position]
        // Устанавливаем изображение из ресурсов
        holder.imageView.setImageResource(item.imageRes)
        // Устанавливаем заголовок
        holder.titleText.text = item.title
        // Устанавливаем описание
        holder.descText.text = item.description
    }

    /**
     * Возвращает общее количество элементов в списке.
     * @return количество элементов.
     */
    override fun getItemCount(): Int = items.size
}