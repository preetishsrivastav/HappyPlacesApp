    package com.example.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplaces.databinding.ActivityAddHappyPlacesBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

    class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener{
        private var binding:ActivityAddHappyPlacesBinding?=null
        private var cal = Calendar.getInstance()
        private lateinit var dateListner:DatePickerDialog.OnDateSetListener
        override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
            binding= ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

            setSupportActionBar(binding?.toolbarHpp)

            if (supportActionBar!=null){
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            binding?.toolbarHpp?.setNavigationOnClickListener {
                onBackPressed()
        }

         dateListner=DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
             cal.set(Calendar.YEAR,year)
             cal.set(Calendar.MONTH,month)
             cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
             UpdateDateInView()
         }

      binding?.etDate?.setOnClickListener(this)
      binding?.btnAddImage?.setOnClickListener(this)

    }

        override fun onClick(view: View?) {
           when(view?.id) {
               R.id.et_date ->{
                   DatePickerDialog(this@AddHappyPlacesActivity,dateListner,
                       cal.get(Calendar.YEAR)
                       ,cal.get(Calendar.MONTH)
                       , cal.get(Calendar.DAY_OF_MONTH)).show()
               }
               R.id.btn_add_image->{
                   val actionDialog=AlertDialog.Builder(this)
                   actionDialog.setTitle("Select Action")
                   actionDialog.setIcon(R.drawable.ic_baseline_call_to_action_24)
                   val actionDialogItems= arrayOf("Select Image From Gallery","Take Photo From Camera")
                   actionDialog.setItems(actionDialogItems){ _,
                       which ->
                       when(which){
                           0-> selectImageFromGallery()
                           1-> takePhotoFromCamera()
                       }
                   }
                      actionDialog.show()
                   }

               }


            }

        private fun takePhotoFromCamera() {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        // Here after all the permission are granted launch the gallery to select and image.
                        if (report!!.areAllPermissionsGranted()) {

                            val cameraIntent = Intent(
                              MediaStore.ACTION_IMAGE_CAPTURE
                            )

                            startActivityForResult(cameraIntent, Camera)

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()


        }

        private fun selectImageFromGallery() {

            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        // Here after all the permission are granted launch the gallery to select and image.
                        if (report!!.areAllPermissionsGranted()) {

                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )

                            startActivityForResult(galleryIntent, Gallery)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()



        }


      public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode==Activity.RESULT_OK){
                if (requestCode== Gallery) {
                    if (data != null) {
                        val contentUri = data.data
                        try {
                            val getBitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                            val saveImageInternally=saveImageInternally(getBitmap)
                            Log.e("Saved Image","Path::$saveImageInternally")
                            binding?.ivHappyPlaces?.setImageBitmap(getBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this, "Image Cannot be uploaded Try again after some time",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                    }
                } else if(requestCode== Camera){
                    val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap

                    val saveImageInternally=saveImageInternally(thumbnail)
                    Log.e("Saved Image","Path::$saveImageInternally")
                    binding?.ivHappyPlaces?.setImageBitmap(thumbnail)

                }


            }


        }


        private fun showRationalDialogForPermissions() {
            val rationalAlertDialog=AlertDialog.Builder(this)
            rationalAlertDialog.setMessage("It Looks like you have denied the permission required to access Gallery" +
                    "You can Go to settings to allow it")

            rationalAlertDialog.setPositiveButton("Go To Settings"){
                _,_ ->
                try {
                    val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            rationalAlertDialog.setNegativeButton("Cancel"){
                dialog,_ ->
                dialog.dismiss()
            }
           rationalAlertDialog.show()
        }

       private fun saveImageInternally(bitmap:Bitmap):Uri{
           val wrapper=ContextWrapper(applicationContext)
           var file =wrapper.getDir(Store_Image_Internally, MODE_PRIVATE)
           file=File(file,"${UUID.randomUUID()}.jpg")
           try {
               val fileOutputStream:OutputStream=FileOutputStream(file)
               bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
               fileOutputStream.flush()
               fileOutputStream.close()
           }catch (e:IOException){
               e.printStackTrace()
           }
           return Uri.parse(file.absolutePath)
       }


        private fun UpdateDateInView(){
            val myFormat="dd.MM.yyyy"
            val sdf=SimpleDateFormat(myFormat,Locale.getDefault())
            binding?.etDate?.setText(sdf.format(cal.time).toString())
        }

        companion object{
            private const val Gallery =1
            private const val Camera=2
            private const val Store_Image_Internally="Happy Places"
        }
    }