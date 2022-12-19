package com.example.appfinalproject_10130492

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(val iTadapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback(){

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0,ItemTouchHelper.START or ItemTouchHelper.END)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        //No need to move the view up and down
        return false;
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        iTadapter.onItemDismiss(viewHolder.adapterPosition)
    }
}