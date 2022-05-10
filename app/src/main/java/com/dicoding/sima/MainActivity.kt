package com.dicoding.sima

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import org.tensorflow.lite.DataType
import com.dicoding.sima.ml.Model
import com.dicoding.sima.ml.Modelcnn
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    private var result: TextView? = null
    private var imageSize = 128

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val camera: Button = findViewById(R.id.button)
        val gallery: Button = findViewById(R.id.button2)
        result = findViewById(R.id.result)
        imageView = findViewById(R.id.imageView)

        camera.setOnClickListener(View.OnClickListener { //camera not yet
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 3)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
                }
        })
        gallery.setOnClickListener(View.OnClickListener {
            val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        })
    }

    private fun classifyImage(image: Bitmap?) {
        try {
            val model = Modelcnn.newInstance(applicationContext)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image!!.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf("a", "ha", "ba", "pa", "na", "wa", "ga", "ja", "da", "ra", "ma", "ta", "sa", "ya", "nga", "la", "i", "u", "nya", "be", "bi", "bo", "bu", "bang", "te", "ti", "to", "tu", "tang")
            result!!.text = classes[maxPos]


            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                var image = data!!.extras!!["data"] as Bitmap?
                val dimension = Math.min(image!!.width, image.height)
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                imageView!!.setImageBitmap(image)
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                classifyImage(image)
            } else {
                val dat = data!!.data
                var image: Bitmap? = null
                try {
                    image = MediaStore.Images.Media.getBitmap(this.contentResolver, dat)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imageView!!.setImageBitmap(image)
                image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, false)
                classifyImage(image)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}