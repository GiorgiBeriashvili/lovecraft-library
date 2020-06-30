package dev.beriashvili.exams.lovecraftlibrary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.activities.LibraryActivity
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import kotlinx.android.synthetic.main.library_recyclerview_layout.view.*

class LibraryRecyclerViewAdapter(
    private val entries: List<Entry>,
    private val origin: Context
) : RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.library_recyclerview_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var entry: Entry

        fun onBind() {
            entry = entries[adapterPosition]

            itemView.titleTextView.text = entry.title
            itemView.descriptionTextView.text = entry.description

            itemView.setOnClickListener {
                if (origin is LibraryActivity) {
                    Toast.makeText(origin, "Unimplemented function.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}