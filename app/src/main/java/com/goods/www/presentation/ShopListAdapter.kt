package com.goods.www.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goods.www.databinding.ItemLayoutMarketBinding
import com.goods.www.domain.model.ShopItem

class ShopListAdapter(private val onShopClicked: (ShopItem) -> Unit) :
    ListAdapter<ShopItem, ShopListAdapter.ShopItemViewHolder>(ShopItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        return ShopItemViewHolder(
            ItemLayoutMarketBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onShopClicked
        )
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ShopItemViewHolder(
        private val binding: ItemLayoutMarketBinding,
        private val onShopClicked: (ShopItem) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var shopItem: ShopItem? = null

        init {
            binding.root.setOnClickListener {
                shopItem?.let {
                    onShopClicked(it)
                }
            }
        }

        fun bind(shopItem: ShopItem) {
            this.shopItem = shopItem
            binding.ivShop.setImageResource(shopItem.img)
            binding.tvTitle.text = shopItem.name
            binding.tvContent.text = shopItem.content
        }
    }

    companion object ShopItemDiffCallback : DiffUtil.ItemCallback<ShopItem>() {
        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem.name == newItem.name && oldItem.img == newItem.img
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem == newItem
        }
    }
}