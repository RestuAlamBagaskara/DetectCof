package com.example.detektifkopi.fargment

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.R
import com.example.detektifkopi.adapter.ListArtikelAdapter
import com.example.detektifkopi.data.Artikel
import com.google.android.material.bottomnavigation.BottomNavigationView


class FragmentArtikel : Fragment(), ListArtikelAdapter.OnItemClickCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artikel, container, false)

        val dataJudul =resources.getStringArray(R.array.data_judul)
        val dataSubJudul = resources.getStringArray(R.array.data_sub)
        val dataLink = resources.getStringArray(R.array.data_link)
        val dataGambar = resources.obtainTypedArray(R.array.data_photo)

        val itemList = mutableListOf<Artikel>()
        for (i in dataJudul.indices) {
            val judul = dataJudul[i]
            val subJudul = dataSubJudul[i]
            val link = dataLink[i]
            val gambar = dataGambar.getResourceId(i, 0)
            val item = Artikel(judul, subJudul, gambar, link)
            itemList.add(item)
        }

        val padding = resources.getDimensionPixelSize(R.dimen.item_margin)

        val recyclerView: RecyclerView = view.findViewById(R.id.all_artikel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(object :RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = padding // Atur padding kanan
            }
        })

        dataGambar.recycle()

        val adapter = ListArtikelAdapter(itemList)
        adapter.setOnItemClickCallback(this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onItemClicked(data: Artikel) {
        val articleLink = data.link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleLink))
        startActivity(intent)
//        if (intent.resolveActivity(requireContext().packageManager) != null) {
//            startActivity(intent)
//        } else {
//            // Handle the case where a web browser is not available
//            Toast.makeText(requireContext(), "Web browser is not available", Toast.LENGTH_SHORT).show()
//        }
    }
}