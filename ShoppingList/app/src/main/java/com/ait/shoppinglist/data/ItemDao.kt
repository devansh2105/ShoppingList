package com.ait.shoppinglist.data

import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getAllItem(): List<Item>

    @Insert
    fun insertItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Query("DELETE FROM item")
    fun deleteAllItem()

    @Update
    fun updatedItem(item: Item)
}