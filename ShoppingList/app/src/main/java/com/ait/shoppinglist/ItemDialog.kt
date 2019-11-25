package com.ait.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ait.shoppinglist.data.Item
import kotlinx.android.synthetic.main.activity_add_item.view.*

class ItemDialog : DialogFragment() {

    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.runtime_exception))
        }
    }

    private lateinit var spinnerCategory: Spinner
    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var cbPurchased: CheckBox

    var isEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_item))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.activity_add_item, null
        )

        spinnerCategory = rootView.spinnerCategory
        etName = rootView.etName
        etPrice = rootView.etPrice
        etDescription = rootView.etDescription
        cbPurchased = rootView.cbPurchased
        builder.setView(rootView)

        isEditMode = ((arguments != null) && arguments!!.containsKey(MainActivity.KEY_ITEM))

        val categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.category_array, android.R.layout.simple_spinner_item)

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        if (isEditMode) {
            builder.setTitle(getString(R.string.edit_item))
            var item: Item = (arguments?.getSerializable(MainActivity.KEY_ITEM) as Item)

            etName.setText(item.name)
            etPrice.setText(item.price.toString().substring(1))
            etDescription.setText(item.description)
            spinnerCategory.setSelection(item.category)
            cbPurchased.setChecked(item.purchased)
        }

        builder.setPositiveButton(getString(R.string.save)) {
                dialog, witch -> // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty() && etPrice.text.isNotEmpty()) {
                if(isEditMode) {
                    handleItemEdit()
                }
                else {
                    handleItemCreate()
                }
                dialog.dismiss()
            } else if (etName.text.isEmpty() && etPrice.text.isEmpty()) {
                etName.error = getString(R.string.field_cannot_be_empty)
                etPrice.error = getString(R.string.field_cannot_be_empty)
            }
            else if (etName.text.isEmpty()){
                etName.error = getString(R.string.field_cannot_be_empty)
            }
            else if (etPrice.text.isEmpty()) {
                etPrice.error = getString(R.string.field_cannot_be_empty)
            }
        }
    }

    private fun handleItemCreate() {

        itemHandler.itemCreated(
            Item(
                null,
                spinnerCategory.selectedItemPosition,
                etName.text.toString(),
                getString(R.string.dollar) + etPrice.text.toString(),
                etDescription.text.toString(),
                cbPurchased.isChecked
            )
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            MainActivity.KEY_ITEM
        ) as Item

        itemToEdit.name = etName.text.toString()
        itemToEdit.price = getString(R.string.dollar) + etPrice.text.toString()
        itemToEdit.description = etDescription.text.toString()
        itemToEdit.purchased = cbPurchased.isChecked
        itemToEdit.category = spinnerCategory.selectedItemPosition
        itemHandler.itemUpdated(itemToEdit)
    }
}