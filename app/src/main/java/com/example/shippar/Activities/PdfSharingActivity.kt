package com.example.shippar.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shippar.R
import com.example.shippar.databinding.ActivityPdfSharingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException

class PdfSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfSharingBinding
    private var url: String =
        "https://www.junkybooks.com/administrator/thebooks/66085f3b7dba2-showcasing-innovative-greece.pdf"
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfSharingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyStoragePermissions(this)

        binding.startRecordingBtn.setOnClickListener {
            downloadAndSharePdf(url)
        }
    }

    private fun downloadAndSharePdf(pdfUrl: String) {
        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(pdfUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "sample.pdf"
                    )

                    if (file.exists()) {
                        file.delete()
                    }

                    response.body?.byteStream()?.use { inputStream ->
                        file.sink().buffer().use { sink ->
                            sink.writeAll(inputStream.source())
                        }
                    }
                    sharePdfViaWhatsApp(file)
                } else {
                    showToast("Failed to download PDF")
                }
            } catch (e: IOException) {
                showToast("Error: ${e.message}")
            } finally {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun sharePdfViaWhatsApp(pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            pdfFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.setPackage("com.whatsapp")
        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
    }

    private fun showToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }

    private fun verifyStoragePermissions(activity: Activity) {
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(
                        this,
                        "Permission denied to access external storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }


}