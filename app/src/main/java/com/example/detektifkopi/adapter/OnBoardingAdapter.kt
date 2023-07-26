package com.example.detektifkopi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.R
import com.example.detektifkopi.data.OnBoardingData

class OnBoardingAdapter(private val onBoardDatas: List<OnBoardingData>) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>(){
    inner class OnBoardingViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val logo = view.findViewById<ImageView>(R.id.logo)
        private val judul = view.findViewById<TextView>(R.id.judul)
        private val deskripsi = view.findViewById<TextView>(R.id.deskripsi)

        fun bind(onBoardingData: OnBoardingData){
            logo.setImageResource(onBoardingData.image)
            judul.text = onBoardingData.judul
            deskripsi.text = onBoardingData.deskripsi
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
       return OnBoardingViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.content_onboarding,
           parent,
               false
           )
       )
    }

    override fun getItemCount(): Int {
        return onBoardDatas.size
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
       holder.bind(onBoardDatas[position])
    }
}