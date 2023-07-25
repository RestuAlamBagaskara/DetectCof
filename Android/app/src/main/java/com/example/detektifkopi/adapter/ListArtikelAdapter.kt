package com.example.detektifkopi.adapter

import android.content.Context
import android.text.DynamicLayout
import android.text.Layout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.R
import com.example.detektifkopi.data.Artikel

class ListArtikelAdapter(private val listArtikel: List<Artikel>) : RecyclerView.Adapter<ListArtikelAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_artikel)
        val tvJudul: TextView = itemView.findViewById(R.id.judul_artikel)
        val tvSub: TextView = itemView.findViewById(R.id.sub_artikel)

        fun bind(item: Artikel) {
            imgPhoto.setImageResource(item.photo)
            val max1 = 1
            val max2 = 2
            tvJudul.text = getEllipsizedText(itemView.context, item.judul, max1)
            tvSub.text = getEllipsizedText(itemView.context, item.sub_judul, max2)
        }

        private fun getEllipsizedText(context: Context, text: String, maxLines: Int): String {
            val textView = TextView(context)
            textView.textSize = 14f // Adjust text size as needed
            textView.maxLines = maxLines
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.text = text

            // Wait for the layout to be created
            textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val layout = textView.layout
                    val lines = layout.lineCount
                    if (lines > 0) {
                        val ellipsizedText = layout.getEllipsisCount(lines - 1)
                        if (ellipsizedText > 0) {
                            val charCount = layout.getEllipsisStart(lines - 1)
                            val ellipsizedString = text.substring(0, charCount - ellipsizedText) + "..."
                            textView.text = ellipsizedString
                        }
                    }
                }
            })

            return textView.text.toString()
        }
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_artikel, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listArtikel.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = listArtikel[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(item)
        }
    }

    private var itemClickListener: AdapterView.OnItemClickListener? = null


    interface OnItemClickCallback {
        fun onItemClicked(data: Artikel)
    }
}
