package com.example.appfinalproject_10130492

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.camera.CameraSourcePreview
import com.example.appfinalproject_10130492.data.Assignment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [AMQrResolveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AMQrResolveFragment : Fragment() {

    private lateinit var cameraPreview: CameraSourcePreview
    private lateinit var fab: FloatingActionButton
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        cameraPreview = view.findViewById(R.id.camera_view)
        fab = view.findViewById(R.id.add_qr_fab)
        val barcodeDetector =
            BarcodeDetector.Builder(this.context).setBarcodeFormats(Barcode.QR_CODE).build()
        val cameraSource =
            CameraSource.Builder(this.context, barcodeDetector).setRequestedPreviewSize(500, 500)
                .setAutoFocusEnabled(true).build()
        if (this.context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
            != PackageManager.PERMISSION_GRANTED) {
            val array = arrayOf(Manifest.permission.CAMERA)
            this.activity?.let { ActivityCompat.requestPermissions(it, array, 1) };
        }
        val navController = findNavController()

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            }
            != PackageManager.PERMISSION_GRANTED
        )
            navController.navigateUp()
        try {

            cameraPreview.start(cameraSource)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            run {
                if (uri != null) {
                    Log.d("PhotoPicked", "Photo Picked: $uri")

                    val image =
                        BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
                    val frame = Frame.Builder().setBitmap(image).build()
                    barcodeDetector.receiveFrame(frame)
                }
            }
        }
        var recieved = ""
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val qrCodes = p0.detectedItems
                if (qrCodes.size() != 0) {
                    if (recieved == qrCodes.valueAt(0).displayValue) {
                        return
                    } else {
                        recieved = qrCodes.valueAt(0).displayValue
                    }
                    Log.i("QR", qrCodes.valueAt(0).displayValue)
                    AMDetailFragment.setEditModeToggle(true)
                    try {
                        AMDetailFragment.assignmentInput =
                            jsonParser(qrCodes.valueAt(0).displayValue)
                    } catch (e: org.json.JSONException) {
                        e.printStackTrace()
                        Snackbar.make(view, R.string.qr_error, Snackbar.LENGTH_SHORT).show()
                        Thread.sleep(1400)

                        return
                    }
                    AssignmentsModifyActivity.backFragmentTransition =
                        R.id.action_addQRFragment_to_AddFirstFragment
                    AssignmentsModifyActivity.onBackBehavior = "Fragment"
                    navController.navigate(R.id.AddSecondFragment)
                    cameraSource.stop()
                    return

                }
            }

        })
        if (isBitmapImported()) {
            Log.d("QR", "${isBitmapImported()}")
            val image = bitmap
            val frame = Frame.Builder().setBitmap(image).build()
            importMode = false
            barcodeDetector.receiveFrame(frame)
        }

        fab.setOnClickListener {


            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }


    }

    override fun onDestroy() {
        cameraPreview.stop()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_q_r, container, false)
    }

    /**
     * @throws org.json.JSONException
     */
    fun jsonParser(str: String): Assignment {
        val jsonObj = JSONObject(str)
        return Assignment(
            -1,
            jsonObj.getString("title"),
            (jsonObj.getLong("assignedDate") + 978307200) * 1000,
            (jsonObj.getLong("dueDate") + 978307200) * 1000,
            try {
                jsonObj.getString("courseName")
            } catch (e: Exception) {
                null
            },
            jsonObj.getString("note"),
            0
        )
    }

    companion object {
        lateinit var bitmap: Bitmap
        var importMode = false
        fun isBitmapImported(): Boolean {
            return this::bitmap.isInitialized && importMode
        }
    }
}