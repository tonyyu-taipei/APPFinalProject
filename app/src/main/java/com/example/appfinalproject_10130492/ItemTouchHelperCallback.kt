package com.example.appfinalproject_10130492

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.display.DisplayDensityHelper

class ItemTouchHelperCallback(val iTadapter: ItemTouchHelperAdapter,val context: Context) : ItemTouchHelper.Callback(){

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0,ItemTouchHelper.START )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        //No need to move the view up and down
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
             if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                 val dpiHelper = DisplayDensityHelper(context)
                 val itemView = viewHolder.itemView
                val p = Paint()
                val drawable: Drawable? = AppCompatResources.getDrawable(context,R.drawable.delete_forever_48px_with_color)
                val delIcon:Bitmap = drawable!!.toBitmap(dpiHelper.convertDpToPx(40f).toInt(),dpiHelper.convertDpToPx(40f).toInt(),null)

                 if(dX < 0 ){
                    p.color = Color.parseColor("#de0707")

                    c.drawRoundRect(itemView.right.toFloat()+dX+dpiHelper.convertDpToPx(5f), itemView.top.toFloat()+dpiHelper.convertDpToPx(10f),itemView.right.toFloat(),
                    itemView.bottom.toFloat()-dpiHelper.convertDpToPx(10f),dpiHelper.convertDpToPx(8f),dpiHelper.convertDpToPx(8f),p)
                    c.drawBitmap(delIcon,itemView.right.toFloat()+(dX/2),itemView.top.toFloat()+(((itemView.bottom.toFloat() - itemView.top-delIcon.height)/2)),p)
            }

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        iTadapter.onItemDismiss(viewHolder.adapterPosition)

    }


    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
       return 20f
    }
}