package com.example.mymaps.adaptor

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Placeholder
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.databinding.RecycleRowBinding
import com.example.mymaps.model.place
import com.example.mymaps.view.MapsActivity

class placeadaptor(val placeList : List<place>) : RecyclerView.Adapter<placeadaptor.Placeholder>() {

    class Placeholder(val recycleRowBinding: RecycleRowBinding) : RecyclerView.ViewHolder(recycleRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Placeholder {
        val recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Placeholder(recycleRowBinding)
    }

    override fun onBindViewHolder(holder: Placeholder, position: Int) {
        holder.recycleRowBinding.recycleText.text = placeList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent (holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("place", placeList.get(position))
            intent.putExtra("Info","old")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size

    }
}