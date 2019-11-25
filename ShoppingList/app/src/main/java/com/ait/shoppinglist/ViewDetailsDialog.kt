package com.ait.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ait.shoppinglist.data.Item
import kotlinx.android.synthetic.main.activity_view_details.view.*

class ViewDetailsDialog : DialogFragment() {
    private lateinit var tvCategory: TextView
    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPurchased: TextView

    var isEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(R.string.new_item)

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.activity_view_details, null
        )



        tvName = rootView.tvNameView
        tvCategory = rootView.tvCategory
        tvPrice = rootView.tvPriceView
        tvDescription = rootView.tvDescription
        tvPurchased = rootView.tvPurchased
        builder.setView(rootView)

        isEditMode = ((arguments != null) && arguments!!.containsKey(MainActivity.KEY_ITEM))

        builder.setTitle(R.string.view_details)
        var item: Item = (arguments?.getSerializable(MainActivity.KEY_ITEM) as Item)

        tvName.setText(getString(R.string.name_header) + item.name )
        tvPrice.setText(getString(R.string.price_header) + item.price )
        tvDescription.setText(getString(R.string.description_header) + item.description )

        var category  = ""
        when(item.category){
            0 -> category = getString(R.string.clothes)
            1 -> category = getString(R.string.electronics)
            2 -> category = getString(R.string.groceries)
            3 -> category = getString(R.string.hygiene)
            4 -> category = getString(R.string.stationery)
            5 -> category = getString(R.string.miscellaneous)
        }
        tvCategory.setText(getString(R.string.category_header) + category)

        if(item.purchased) {
            tvPurchased.setText(getString(R.string.purchased_yes))
        }
        else {
            tvPurchased.setText(getString(R.string.purchased_no))
        }


        builder.setPositiveButton(getString(R.string.back)) {
                dialog, witch -> // empty
        }

        return builder.create()
    }
}