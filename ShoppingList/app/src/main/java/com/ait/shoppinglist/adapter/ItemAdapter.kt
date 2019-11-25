package com.ait.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ait.shoppinglist.MainActivity
import com.ait.shoppinglist.R
import com.ait.shoppinglist.data.AppDatabase
import com.ait.shoppinglist.data.Item
import com.ait.shoppinglist.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item.view.*
import java.util.*


class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>, ItemTouchHelperCallback {

    var itemList = mutableListOf<Item>()

    val context: Context

    constructor(context: Context, listTodos: List<Item>){
        this.context = context

        itemList.addAll(listTodos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRow = LayoutInflater.from(context).inflate(
            R.layout.item, parent, false
        )
        return ViewHolder(itemRow)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = itemList.get(holder.adapterPosition)

        holder.tvName.text = item.name
        holder.tvPrice.text = item.price
        holder.cbPurchased.setChecked(item.purchased)


        when(item.category) {
            0 -> holder.ivCategory.setImageResource(R.drawable.clothes)
            1 -> holder.ivCategory.setImageResource(R.drawable.electronics)
            2 -> holder.ivCategory.setImageResource(R.drawable.groceries)
            3 -> holder.ivCategory.setImageResource(R.drawable.hygiene)
            4 -> holder.ivCategory.setImageResource(R.drawable.stationery)
            5 -> holder.ivCategory.setImageResource(R.drawable.miscellaneous)
        }


        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.cbPurchased.setOnClickListener {
            item.purchased = holder.cbPurchased.isChecked
            updateItem(item)
        }

        holder.btnEdit.setOnClickListener{
            (context as MainActivity).showEditItemDialog(item, holder.adapterPosition)
        }

        holder.btnViewDet.setOnClickListener{
            (context as MainActivity).showViewDetailsDialog(item, holder.adapterPosition)
        }
    }

    fun addItem(item: Item) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex)
    }

    fun updateItem(item: Item) {
        Thread{
            AppDatabase.getInstance(context).itemDao().updatedItem(item)
        }.start()
    }

    fun updateItemOnPosition(item: Item,  index: Int) {
        itemList.set(index, item)
        notifyItemChanged(index)
    }

    fun deleteItem(index: Int){
        Thread{
            AppDatabase.getInstance(context).itemDao().deleteItem(itemList[index])
            (context as MainActivity).runOnUiThread {
                itemList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }


    fun deleteAllItems() {
        Thread{
            AppDatabase.getInstance(context).itemDao().deleteAllItem()
            (context as MainActivity).runOnUiThread {
                itemList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCategory = itemView.ivCategory
        val tvName = itemView.tvName
        val tvPrice = itemView.tvPrice
        val btnEdit = itemView.btnEdit
        val cbPurchased = itemView.cbPurchased
        val btnDelete = itemView.btnDelete
        val btnViewDet = itemView.btnViewDet
    }

}