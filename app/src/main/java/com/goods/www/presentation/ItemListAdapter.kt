package com.goods.www.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goods.www.databinding.ItemLayoutItemBinding
import com.goods.www.domain.model.Item

class ItemListAdapter(private val itemClickListener: (Item) -> Unit) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            binding = ItemLayoutItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false),
            itemClickListener = itemClickListener
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(
        private val binding: ItemLayoutItemBinding,
        private val itemClickListener: (Item) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var item: Item? = null

        init {
            binding.root.setOnClickListener {
                item?.let {
                    itemClickListener(it)
                }
            }
        }

        fun bind(item: Item) {
            this.item = item
            Glide.with(binding.root.context).load(item.img).into(binding.ivItem)
            binding.tvItemName.text = item.name
            binding.tvPrice.text = "가격: ${item.price}원"
            binding.tvType.text = "분류: ${item.type}"
        }
    }

    companion object ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.documentId == newItem.documentId
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}