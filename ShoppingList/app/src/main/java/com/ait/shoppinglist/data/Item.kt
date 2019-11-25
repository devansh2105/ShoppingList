package com.ait.shoppinglist.data

import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")
data class Item (
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "category")var category: Int,
    @ColumnInfo(name = "name")var name: String,
    @ColumnInfo(name = "price")var price: String,
    @ColumnInfo(name = "description")var description: String,
    @ColumnInfo(name = "purchased")var purchased: Boolean
)  : Serializable