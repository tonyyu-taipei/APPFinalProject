package com.example.appfinalproject_10130492

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.json.JSONObject


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [AddQRFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddQRFragment : Fragment() {

    private lateinit var surfaceView: SurfaceView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        surfaceView = view.findViewById(R.id.surfaceView)
        val barcodeDetector = BarcodeDetector.Builder(this.context).setBarcodeFormats(Barcode.QR_CODE).build()
        val cameraSource = CameraSource.Builder(this.context,barcodeDetector).setAutoFocusEnabled(true).build()
        if(this.context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
            != PackageManager.PERMISSION_GRANTED){
            val array = arrayOf(Manifest.permission.CAMERA)
            this.activity?.let { ActivityCompat.requestPermissions(it,array,1) };
        }
        val navController = findNavController()
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

                if (context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.CAMERA
                        )
                    }
                    != PackageManager.PERMISSION_GRANTED
                ) return
                try {
                    cameraSource.start(holder)
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }
            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val qrCodes = p0.detectedItems
                if(qrCodes.size() !=0){
                    Log.i("QR",qrCodes.valueAt(0).displayValue)
                    NewAssignFragment.setEditModeToggle(true)
                    NewAssignFragment.assignmentInput = jsonParser(qrCodes.valueAt(0).displayValue)
                    AddActivity.backFragmentTransition = R.id.action_addQRFragment_to_AddFirstFragment
                    AddActivity.onBackBehavior = "Fragment"
                    navController?.navigate(R.id.action_addQRFragment_to_AddSecondFragment)
                    cameraSource.stop()

                }
            }

        })




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_q_r, container, false)
    }

    fun jsonParser(str: String): Assignment{
        val jsonObj = JSONObject(str)
        return Assignment(-1,jsonObj.getString("title"),jsonObj.getLong("assignedDate")+978307200*1000,jsonObj.getLong("dueDate")+978307200*1000,jsonObj.getString("courseName"),jsonObj.getString("note"),0)
    }

}