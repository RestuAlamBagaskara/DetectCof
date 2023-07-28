package com.example.detektifkopi.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.R
import com.example.detektifkopi.data.DatabaseSave
import com.example.detektifkopi.data.ScanResult
import com.example.detektifkopi.fargment.Tersimpan

class ScanSaveAdapter(
    private var scanResults: MutableList<ScanResult>,
    private val database: DatabaseSave,
    private val itemClickListener: OnItemClickListener,
    private val adapterCallback: ScanSaveAdapterCallback
) :
    RecyclerView.Adapter<ScanSaveAdapter.ViewHolder>() {

    interface ScanSaveAdapterCallback {
        fun onItemRemoved()
    }
    interface OnItemClickListener {
        fun onItemClick(scanResult: ScanResult)
    }


    class ViewHolder(itemView: View, private val  itemClickListener: OnItemClickListener, private val scanResults: List<ScanResult>) : RecyclerView.ViewHolder(itemView) {
        val penyakitTextView: TextView = itemView.findViewById(R.id.card_penyakit)
        val virusTextView: TextView = itemView.findViewById(R.id.card_virus)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(scanResults[position])
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view, itemClickListener, scanResults)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val scanResult = scanResults.get(position)
        val scanResult = scanResults[position]
        holder.penyakitTextView.text = scanResult.penyakit
        holder.virusTextView.text = scanResult.virus

        val bitmap = BitmapFactory.decodeFile(scanResult.imagePath)
        holder.imageView.setImageBitmap(bitmap)

        holder.deleteButton.setOnClickListener {
            // Handle the delete button click here
            database.deleteScanResult(scanResult.id) // Call the method to delete from the database
            scanResults.removeAt(position) // Remove the item from the dataset
            notifyItemRemoved(position) // Notify RecyclerView about the removal

            adapterCallback.onItemRemoved()
        }
    }

    override fun getItemCount(): Int {
        return scanResults.size
    }


}
