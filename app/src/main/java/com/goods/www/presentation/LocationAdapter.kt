package com.goods.www.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goods.www.databinding.ItemLayoutLocationBinding

class LocationAdapter: ListAdapter<Int, LocationAdapter.LocationViewHolder>(LocationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            ItemLayoutLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class LocationViewHolder(private val binding: ItemLayoutLocationBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
        }
    }

    companion object LocationDiffCallback: DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

}