package com.ait.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.ait.shoppinglist.adapter.ItemAdapter
import com.ait.shoppinglist.data.AppDatabase
import com.ait.shoppinglist.data.Item
import com.ait.shoppinglist.touch.ItemReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class MainActivity : AppCompatActivity(), ItemDialog.ItemHandler{

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
        const val TAG_VIEW_DETAILS = "TAG_VIEW_DETAILS"
    }

    lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

    }


    fun saveWasStarted() {
        var sharedPref= PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    fun wasStartedBefore():Boolean {
        var sharedPref= PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    private fun initRecyclerView() {
        Thread {
            var itemList =
                AppDatabase.getInstance(this@MainActivity).itemDao().getAllItem()

            runOnUiThread {
                itemAdapter = ItemAdapter(this, itemList)
                recyclerItem.adapter = itemAdapter

                var itemDecoration = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerItem.addItemDecoration(itemDecoration)


                val callback = ItemReyclerTouchCallback(itemAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerItem)
            }
        }.start()
    }

    fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager, TAG_ITEM_DIALOG)
    }

    var editIndex: Int  = -1

    fun showEditItemDialog(itemToEdit: Item, idx: Int) {
        editIndex = idx

        val editDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemToEdit)

        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, TAG_ITEM_EDIT)

    }

    var viewIndex = -1

    fun showViewDetailsDialog(itemToView: Item, idx: Int) {
        viewIndex = idx

        val viewDetailsDialog = ViewDetailsDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemToView)

        viewDetailsDialog.arguments = bundle

        viewDetailsDialog.show(supportFragmentManager, TAG_VIEW_DETAILS)

    }

    fun saveItem(item: Item) {
        Thread {
            var newId = AppDatabase.getInstance(this).itemDao().insertItem(
                item
            )

            item.itemId = newId

            runOnUiThread {
                itemAdapter.addItem(item)
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        Handler().post {
            showSignInPrompt(this) }
        return super.onCreateOptionsMenu(menu)
    }

    private fun showSignInPrompt(mainActivity: MainActivity) {
        if(!wasStartedBefore()) {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(R.id.action_add_item)
            .setPrimaryText(getString(R.string.add_new_item))
            .setSecondaryText(getString(R.string.click_to_create_new_item))
            .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> {
                itemAdapter.deleteAllItems()
            }
            R.id.action_add_item -> {
                showAddItemDialog()
            }
        }
        return true
    }

    override fun itemCreated(item: Item) {
        saveItem(item)
    }

    override fun itemUpdated(item: Item) {
        Thread{
            AppDatabase.getInstance(this@MainActivity).itemDao().updatedItem(item)
            runOnUiThread {
                itemAdapter.updateItemOnPosition(item, editIndex)
            }
        }.start()
    }
}
