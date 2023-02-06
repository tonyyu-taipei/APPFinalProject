package com.example.appfinalproject_10130492

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
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
import java.util.*


/**
 * A simple [Fragment] subclass.
 * For Sharing the Assignment QRCode to others.

 */
class QRShareFragment : Fragment() {
    private lateinit var qrImg: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var courseName: String
    private lateinit var file: File
    private lateinit var assignText: TextView
    private lateinit var courseText: TextView
    private lateinit var dateText: TextView
    private lateinit var cardView: CardView
    private lateinit var shareText: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_share, container, false)
    }

    override fun onResume() {
        super.onResume()
        val assignDB = AssignmentsDB(context)
        val assignment = assignDB.read(findId)

        assignText.text = assignment?.title
        courseText.text = assignment?.courseName
        val dueDate = assignment?.let { Date(it.dueDate) }
        val uDate = java.text.SimpleDateFormat("yyyy/MM/dd aa HH:mm", Locale.TAIWAN)
        var dateStr = uDate.format(dueDate)


        dateText.text = dateStr


        val json = JSONObject()
        json.put("courseName", assignment?.courseName)
        courseName = assignment?.courseName.toString()
        json.put("assignedDate", (assignment?.assignedDate?.div(1000)?.minus(978307200)))
        json.put("dueDate", (assignment?.dueDate?.div(1000)?.minus(978307200)))
        json.put("note", assignment?.note)
        json.put("title", assignment?.title)


        qrImg = view?.findViewById(R.id.qrcode_display)!!
        val encoder = BarcodeEncoder()
        try {

            Log.i("QR", json.toString())
            bitmap = encoder.encodeBitmap(
                String(json.toString().toByteArray(), Charsets.ISO_8859_1),
                BarcodeFormat.QR_CODE,
                300,
                300
            )
            var newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val logoBitmap =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_foreground, null)
                    ?.toBitmap(100, 100)
            val canvas = Canvas(newBitmap)
            canvas.drawBitmap(bitmap, Matrix(), null)
            if (logoBitmap != null) {
                canvas.drawBitmap(logoBitmap, 100f, 100f, null)
            }
            bitmap = newBitmap
            qrImg.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Log.e("QR", e.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignText = view.findViewById(R.id.share_assign_name)
        courseText = view.findViewById(R.id.share_course_name)
        dateText = view.findViewById(R.id.share_date)
        cardView = view.findViewById(R.id.share_card)
        shareText = view.findViewById(R.id.share_text_for_qr)

        setHasOptionsMenu(true)
        if (findId == -1) {
            view.let { Snackbar.make(it, "錯誤：無法讀取作業資料", Snackbar.LENGTH_LONG).show() }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share_page, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (this::file.isInitialized)
            file.delete()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.share_assign_options) {
            return super.onOptionsItemSelected(item)
        }
        val file = writeToInternal(compressBitmap())
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )

        Log.i("Share", uri.toString())
        if (uri != null) {
            viewShareSheet(uri)

        }


        return super.onOptionsItemSelected(item)

    }

    private fun compressBitmap(): ByteArray {
        val bos = ByteArrayOutputStream()
        shareText.visibility = VISIBLE
        shareText.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val backgroundColorOriginal = cardView.backgroundTintList
        cardView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val textBitmap =
            Bitmap.createBitmap(shareText.width, shareText.height + 10, Bitmap.Config.ARGB_8888)
        val cardBitmap = Bitmap.createBitmap(
            cardView.measuredWidth,
            cardView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val resBitmap = Bitmap.createBitmap(
            if (cardBitmap.width > bitmap.width) {
                cardView.measuredWidth + 40
            } else {
                bitmap.width + 40
            }, cardBitmap.height + bitmap.height + textBitmap.height + 60, Bitmap.Config.ARGB_8888
        )

        val outputCanvas = Canvas(resBitmap)
        val textCanvas = Canvas(textBitmap)
        shareText.draw(textCanvas)
        val cardCanvas = Canvas(cardBitmap)

        cardView.draw(cardCanvas)
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        outputCanvas.drawColor(
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    Color.BLACK
                }
                else -> {
                    Color.WHITE
                }
            }
        )
        outputCanvas.drawBitmap(textBitmap, 20f, 10f, null)
        outputCanvas.drawBitmap(
            bitmap,
            cardBitmap.width / 2f - bitmap.width / 2f + 20,
            textBitmap.height.toFloat(),
            null
        )
        outputCanvas.drawBitmap(
            cardBitmap,
            20f,
            textBitmap.height.toFloat() + bitmap.height.toFloat() + 40,
            null
        )

        resBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        shareText.visibility = INVISIBLE
        cardView.backgroundTintList = backgroundColorOriginal
        return bos.toByteArray()
    }

    private fun writeToInternal(data: ByteArray): File {

        file = File(context?.filesDir, "CheckAssign-$findId.png")
        val fos = FileOutputStream(file)
        fos.write(data)
        fos.flush()
        fos.close()
        return file
    }

    private fun viewShareSheet(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
        }

        startActivity(Intent.createChooser(shareIntent, null))

    }

    companion object {
        var findId: Int = -1
    }
}