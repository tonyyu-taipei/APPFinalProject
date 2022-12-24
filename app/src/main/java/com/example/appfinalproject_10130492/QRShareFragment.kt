package com.example.appfinalproject_10130492

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.

 */
class QRShareFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var qrImg: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(findId == -1){
            view?.let { Snackbar.make(it,"錯誤：無法讀取作業資料",Snackbar.LENGTH_LONG).show() }
        }

        val assignDB = AssignmentsDB(context)
        val assignment = assignDB.read(findId)
        val json = JSONObject()
        json.put("courseName",assignment?.courseName)
        json.put("assignedDate",(assignment?.assignedDate?.div(1000)?.minus(978307200)))
        json.put("dueDate",(assignment?.dueDate?.div(1000)?.minus(978307200)))
        json.put("note",assignment?.note)
        json.put("title",assignment?.title)


        qrImg = view.findViewById<ImageView>(R.id.qrcode_display)
        val encoder = BarcodeEncoder()
        try{

            Log.i("QR",json.toString())
            val bitmap = encoder.encodeBitmap(String(json.toString().toByteArray(),Charsets.ISO_8859_1), BarcodeFormat.QR_CODE,150,150)
            qrImg.setImageBitmap(bitmap)
        }catch(e: WriterException){
            e.printStackTrace()
            Log.e("QR",e.toString())
        }
    }

    companion object{
        var findId: Int = -1
    }
}