package com.example.appfinalproject_10130492.display

import android.content.Context

class DisplayDensityHelper (val context: Context){
    fun convertPxToDp(px: Float): Float{
        return px/ getDensity()
    }
    fun convertDpToPx(dp: Float):Float{
        return dp * getDensity()
    }
    private fun getDensity(): Float{
        return context.resources.displayMetrics.density
    }
}