package com.xujichang.nocrash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ErrorFilesAdapter(private val fileSelect: (String) -> Unit) :
    PagingDataAdapter<String, ErrorFileViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

        }
    }

    override fun onBindViewHolder(holder: ErrorFileViewHolder, position: Int) =
        holder.bind(getItem(position), fileSelect)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErrorFileViewHolder =
        ErrorFileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_error_files, parent, false)
        )
}

class ErrorFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(data: String?, fileSelect: (String) -> Unit) {
        data?.also {
            itemView.findViewById<Button>(R.id.file_name).apply {
                text = data
                setOnClickListener {
                    fileSelect(data)
                }
            }
        }
    }
}
