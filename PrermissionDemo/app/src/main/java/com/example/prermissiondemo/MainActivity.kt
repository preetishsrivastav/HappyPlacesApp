package com.example.prermissiondemo

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private val cameraResultLauncher:ActivityResultLauncher<String> =
    registerForActivityResult(
     ActivityResultContracts.RequestPermission()){
        isGranted ->
        if (isGranted){
            Toast.makeText(this,"Permission Granted For Camera",Toast.LENGTH_LONG).show()
        }else
        {
            Toast.makeText(this,"Permission Denied For Camera",Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPermission:Button = findViewById(R.id.button)
        btnPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                showRationaleDialog("Camera permission is required","Permission Demo Require camera permission")
                cameraResultLauncher.launch(Manifest.permission.CAMERA)
            }else
            {
                cameraResultLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
    private fun showRationaleDialog(title:String,message:String){
      val builder:AlertDialog.Builder=AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cancel"){dialog,_->
            dialog.dismiss()
        }
        builder.create().show()

    }
}