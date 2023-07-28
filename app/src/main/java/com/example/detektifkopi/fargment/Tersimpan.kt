package com.example.detektifkopi.fargment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.DetailActivity
import com.example.detektifkopi.HasilScanActivity
import com.example.detektifkopi.R
import com.example.detektifkopi.adapter.ScanSaveAdapter
import com.example.detektifkopi.data.DatabaseSave
import com.example.detektifkopi.data.ScanResult


class Tersimpan : Fragment(), ScanSaveAdapter.OnItemClickListener,
    ScanSaveAdapter.ScanSaveAdapterCallback {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScanSaveAdapter
    private lateinit var emptyImageView: ImageView
    private lateinit var emptyText: TextView
    private lateinit var scanResults: MutableList<ScanResult> // Initialize with an empty list


    override fun onItemClick(scanResult: ScanResult) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("capturedImage", BitmapFactory.decodeFile(scanResult.imagePath))
        intent.putExtra("resultLabel", scanResult.penyakit) // Ganti 'virus' dengan atribut yang sesuai
        startActivity(intent)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tersimpan, container, false)
        recyclerView = view.findViewById(R.id.rv_inventory_result)
        emptyImageView = view.findViewById(R.id.img_empty)
        emptyText = view.findViewById(R.id.tv_empty)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Ambil data dari SQLite menggunakan DatabaseHandler
        val dbHandler = DatabaseSave(requireContext())
        scanResults = dbHandler.getAllScanResults().toMutableList()


        // Inisialisasi adapter dan atur RecyclerView
        adapter = ScanSaveAdapter(scanResults, dbHandler, this, this)
        recyclerView.adapter = adapter

        updateVisibility()

        return view
    }

    override fun onItemRemoved() {
        updateVisibility()
    }

    private fun updateVisibility(){
        if (scanResults.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyImageView.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyImageView.visibility = View.GONE
            emptyText.visibility = View.GONE
        }

    }


}