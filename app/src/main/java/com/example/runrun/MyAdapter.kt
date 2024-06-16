package com.example.runrun

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runrun.databinding.ItemRecyclerviewBinding
import kotlin.contracts.contract

class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, private val datas: MutableList<NotificationItem>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = datas[position]
        holder.binding.apply {
            recyNotiNm.text = item.notiNm
            recyBus.text = item.busNm
            recyStation.text = item.staNm
            recySelectedDays.text = item.selectedDays
            recyWhentoWhen.text = item.whenToWhen
        }

        // 이미지 업로드 구현 중..
        val imageRef = MyApplication.storage.reference.child("images/${item.docId}.jpg")
        imageRef.downloadUrl.addOnCompleteListener { task ->
            if(task.isSuccessful){
                holder.binding.recyImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.recyImage)
            }
        }
    }
}
