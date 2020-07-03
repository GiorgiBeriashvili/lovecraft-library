package dev.beriashvili.exams.lovecraftlibrary.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.activities.ManuscriptActivity
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import kotlinx.android.synthetic.main.manuscript_recyclerview_layout.view.*
import java.util.*

class ArchiveRecyclerViewAdapter(
    private var entries: MutableList<Entry>,
    private val origin: Context
) : RecyclerView.Adapter<ArchiveRecyclerViewAdapter.ViewHolder>(), Filterable {
    private val filterableEntries = entries.toMutableList()

    private val filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Entry>()

            if (charSequence.toString().isBlank()) {
                filteredList.addAll(entries)
            } else {
                entries.forEach { entry: Entry ->
                    entry.apply {
                        if (title.toLowerCase(Locale.ROOT).contains(
                                charSequence.toString().toLowerCase(Locale.ROOT)
                            ) || description.toLowerCase(Locale.ROOT)
                                .contains(charSequence.toString().toLowerCase(Locale.ROOT))
                        ) {
                            filteredList.add(entry)
                        }
                    }
                }
            }

            return FilterResults().apply {
                values = filteredList
            }
        }

        override fun publishResults(
            charSequence: CharSequence?,
            filterResults: FilterResults?
        ) {
            filterableEntries.clear()

            @Suppress("UNCHECKED_CAST")
            filterableEntries.addAll(filterResults?.values as Collection<Entry>)

            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.manuscript_recyclerview_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = filterableEntries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var entry: Entry

        fun onBind(position: Int) {
            entry = filterableEntries[position]

            itemView.apply {
                entryTitleTextView.text = entry.title
                entryDescriptionTextView.text = entry.description
                entryCategoryTextView.text = entry.category

                setOnClickListener {
                    val intent = Intent(origin, ManuscriptActivity::class.java)

                    intent.putExtra("entry", entry)

                    origin.startActivity(intent)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return filter
    }
}