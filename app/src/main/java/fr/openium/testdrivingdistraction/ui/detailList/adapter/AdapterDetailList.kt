package fr.openium.testdrivingdistraction.ui.detailList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.utils.DateUtils
import kotlinx.android.synthetic.main.item_detail.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject


class AdapterDetailList(
    private var data: MutableList<Trip>,
    private var onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {

    private val dateUtils: DateUtils by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        holder.itemView.textViewDetailBeginDate.text =
            dateUtils.format(dateUtils.parse(item.beginDate, DateUtils.Format.DATE_FULL)?.time ?: 0L, DateUtils.Format.DATE_SHORT_TEXT)

        holder.itemView.textViewDetailEndDate.text =
            dateUtils.format(dateUtils.parse(item.endDate ?: "", DateUtils.Format.DATE_FULL)?.time ?: 0L, DateUtils.Format.DATE_SHORT_TEXT)

        holder.itemView.textViewDetailNbEventsRecorded.text =
            (item.events.count() + item.sensorEvents.count()).toString()

        holder.itemView.setOnClickListener {
            onItemClicked.invoke(item.beginDate)
        }
    }

    override fun getItemCount(): Int =
        data.count()

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}