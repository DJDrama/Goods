package com.goods.www.repository

import android.util.Log
import com.goods.www.datasource.model.ItemDto
import com.goods.www.datasource.model.ShopDto
import com.goods.www.datasource.model.toDomainList
import com.goods.www.domain.model.Item
import com.goods.www.domain.model.ShopItem
import com.goods.www.utils.Brands
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreRepository {

    @ExperimentalCoroutinesApi
    fun getMarts(type: Brands?): Flow<List<ShopItem>> = callbackFlow {
        var eventsCollection: CollectionReference? = null
        try {
            eventsCollection = FirebaseFirestore.getInstance()
                .collection("shops")
        } catch (e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }

        val subscription = eventsCollection?.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                try {
                    val res = mutableListOf<ShopDto>()
                    for (document in snapshot.documents) {
                        val shopItem = document.toObject<ShopDto>()
                        shopItem?.let {
                            it.documentId = document.id
                            res.add(it)
                        }
                    }
                    val domainList = res.toDomainList()
                    offer(
                        type?.let { brand ->
                            domainList.filter {
                                it.type == brand
                            }
                        } ?: domainList
                    )
                } catch (e: Exception) {
                    Log.e("FirestoreRepo", "getMarts Exception: ${e.message ?: "Unknown Message"}")
                }
            } ?: return@addSnapshotListener
        }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from Firestore
        awaitClose {
            subscription?.remove()
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllItems(documentId: String): Flow<List<Item>> = callbackFlow {
        var eventsCollection: CollectionReference? = null
        try {
            eventsCollection = FirebaseFirestore.getInstance()
                .collection("shops")
                .document(documentId)
                .collection("items")
        } catch (e: Throwable) {
            close(e)
        }
        val subscription =
            eventsCollection?.addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    try {
                        val res = mutableListOf<ItemDto>()
                        for (document in it.documents) {
                            val item = document.toObject<ItemDto>()
                            item?.let { itemDto->
                                itemDto.documentId = document.id
                                res.add(itemDto)
                            }
                        }
                        offer(res.toDomainList())
                    } catch (e: Exception) {
                        Log.e("FirestoreRepo",
                            "getAllCategories Exception: ${e.message ?: "Unknown Message"}")
                    }
                } ?: return@addSnapshotListener
            }
        awaitClose {
            subscription?.remove()
        }
    }
}