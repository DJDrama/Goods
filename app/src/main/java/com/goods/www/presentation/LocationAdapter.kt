package com.goods.www.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goods.www.databinding.ItemLayoutLocationBinding

class LocationAdapter :
    ListAdapter<Pair<String, Int>, LocationAdapter.LocationViewHolder>(LocationDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            ItemLayoutLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LocationViewHolder(private val binding: ItemLayoutLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<String, Int>) {
            binding.tvType.text = pair.first
            val pos = pair.second-1
            if (pos > -1)
                showImageView(pos)
        }

        private fun showImageView(pos: Int) {
            binding.apply {
                val arr = arrayOf(
                    iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9, iv10
                )
                arr.forEachIndexed { _, imageView ->
                    imageView.visibility = View.INVISIBLE
                }
                arr[pos].visibility = View.VISIBLE
            }
        }
    }

    companion object LocationDiffCallback : DiffUtil.ItemCallback<Pair<String, Int>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>,
        ): Boolean {
            return oldItem.first == newItem.first && oldItem.second == newItem.second
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>,
        ): Boolean {
            return oldItem == newItem
        }
    }
}