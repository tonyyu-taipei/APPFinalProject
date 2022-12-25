package com.example.appfinalproject_10130492

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.

 */
class QRShareFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var qrImg: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var courseName: String
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        if(findId == -1){
            view?.let { Snackbar.make(it,"錯誤：無法讀取作業資料",Snackbar.LENGTH_LONG).show() }
        }

        val assignDB = AssignmentsDB(context)
        val assignment = assignDB.read(findId)
        val json = JSONObject()
        json.put("courseName",assignment?.courseName)
        courseName = assignment?.courseName.toString()
        json.put("assignedDate",(assignment?.assignedDate?.div(1000)?.minus(978307200)))
        json.put("dueDate",(assignment?.dueDate?.div(1000)?.minus(978307200)))
        json.put("note",assignment?.note)
        json.put("title",assignment?.title)


        qrImg = view.findViewById<ImageView>(R.id.qrcode_display)
        val encoder = BarcodeEncoder()
        try{

            Log.i("QR",json.toString())
            bitmap = encoder.encodeBitmap(String(json.toString().toByteArray(),Charsets.ISO_8859_1), BarcodeFormat.QR_CODE,300,300)
            qrImg.setImageBitmap(bitmap)
        }catch(e: WriterException){
            e.printStackTrace()
            Log.e("QR",e.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share_page,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onDestroy() {
        super.onDestroy()
        if(this::file.isInitialized)
            file.delete()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId != R.id.share_assign_options){
            return super.onOptionsItemSelected(item)
        }
        val file = writeToInternal(compressBitmap())
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )

        Log.i("Share",uri.toString())
        if (uri != null) {
            viewShareSheet(uri)

        }


        return super.onOptionsItemSelected(item)

    }

    private fun compressBitmap(): ByteArray {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        return bos.toByteArray()
    }

    private fun writeToInternal(data: ByteArray): File{
        file = File(context?.filesDir, "${courseName}-$findId.png")
        val fos = FileOutputStream(file)
        fos.write(data)
        fos.flush()
        fos.close()
        return file
    }

    private fun viewShareSheet(uri: Uri){
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
        }

        startActivity(Intent.createChooser(shareIntent,null))

    }

    companion object{
        var findId: Int = -1
    }
}