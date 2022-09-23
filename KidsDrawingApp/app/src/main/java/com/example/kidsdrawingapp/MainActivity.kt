package com.example.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private val galleryResult:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == RESULT_OK && result.data!=null){
               val imageBackgroundView:ImageView=findViewById(R.id.iv_background)
                imageBackgroundView.setImageURI(result.data?.data)
             }

        }

     private val requestPermission:ActivityResultLauncher<Array<String>> =
         registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
             permissions ->
             permissions.entries.forEach{
                 val permissionName=it.key
                 val isGranted= it.value

                 if (isGranted){
                     if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                         val pickintent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                           galleryResult.launch(pickintent)
                         Toast.makeText(this,"Permission For storageGranted",Toast.LENGTH_LONG).show()
                     }
                 }
                 else if (permissionName ==Manifest.permission.READ_EXTERNAL_STORAGE){
                     Toast.makeText(this,"permission for storage not granted",Toast.LENGTH_LONG).show()
                 }

             }

         }
     private var drawingView:DrawingView?=null
    private var mImageButtonCurrentPaint:ImageButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById(R.id.drawingView)
        drawingView?.setSizeBrush(5.toFloat())

        val galleryButton:ImageButton =findViewById(R.id.ib_gallery)
        val linearLayout= findViewById<LinearLayout>(R.id.ll_paint)
        val eraser=findViewById<ImageButton>(R.id.ib_eraser)
        eraser.setOnClickListener {
            drawingView?.setSizeBrush(10.toFloat())
            val eraserColor= eraser.tag.toString()
            drawingView?.setColor(eraserColor)
        }
        galleryButton.setOnClickListener {
            requestpermissionExtenalStorage()
        }

        mImageButtonCurrentPaint= linearLayout[1] as ImageButton


        val brush= findViewById<ImageButton>(R.id.ib_brush)
        brush.setOnClickListener {
            showBrushSizeDialog()
        }

        val ibUndo:ImageButton=findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener {
             drawingView?.onClickUndo()
        }

        val flView:FrameLayout=findViewById(R.id.fl_drawingview_container)
       val ibSave:ImageButton=findViewById(R.id.ib_save)
        ibSave.setOnClickListener {
            if(isReadStorageAllowed()){
                lifecycleScope.launch{
                    saveBitmapImage(getBitmapFromView(flView))
                }

            }
            else{
                requestpermissionExtenalStorage()
            }

        }
    }

    private fun requestpermissionExtenalStorage() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
             && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
             showRequestPermissionRationale("Kids Drawing App Require Gallery Permission",
                 "Gallery permission is denied by the user ")
         }
         else {
                 requestPermission.launch(arrayOf(
                     Manifest.permission.READ_EXTERNAL_STORAGE,
                     Manifest.permission.WRITE_EXTERNAL_STORAGE
                 ))
            }



    }

    private fun showRequestPermissionRationale(title:String,message:String) {
        val builder =AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Cancel"){ DialogInterface,which->
               DialogInterface.dismiss()
        }
        builder.setCancelable(false)
        builder.create().show()
    }

    fun paintClicked(view:View){
        if (view !== mImageButtonCurrentPaint){
             val imageButton = view as ImageButton
            val colorTag= imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }

    }
    private fun isReadStorageAllowed():Boolean{
        val result= ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
         return result== PackageManager.PERMISSION_GRANTED
    }

    private fun showBrushSizeDialog(){
        val brushSizeDialog =Dialog(this)
        brushSizeDialog.setContentView(R.layout.brush_layout)
        brushSizeDialog.setTitle("BrushSize:")
        val smallBrush=brushSizeDialog.findViewById<ImageButton>(R.id.ib_smallBrush)
        val mediumBrush=brushSizeDialog.findViewById<ImageButton>(R.id.ib_mediumBrush)
        val largeBrush=brushSizeDialog.findViewById<ImageButton>(R.id.ib_largeBrush)


        smallBrush.setOnClickListener {
        drawingView?.setSizeBrush(10.toFloat())
            brushSizeDialog.dismiss()
        }
        mediumBrush.setOnClickListener {
            drawingView?.setSizeBrush(20.toFloat())
            brushSizeDialog.dismiss()
        }
        largeBrush.setOnClickListener {
            drawingView?.setSizeBrush(30.toFloat())
            brushSizeDialog.dismiss()
        }
        brushSizeDialog.show()
    }
    private fun getBitmapFromView(view: View):Bitmap{

        val returnedBitmap:Bitmap= Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(returnedBitmap)
        val bgImage :ImageView=findViewById(R.id.iv_background)

        if (bgImage!= null){
            bgImage.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        return returnedBitmap
    }

    private suspend fun saveBitmapImage(mBitmap: Bitmap):String{
        var result =""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    val byte = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, byte)

                    val f = File(
                        externalCacheDir?.absoluteFile.toString() + File.separator +
                                "KidsDrawingApp" + System.currentTimeMillis() / 1000 + ".png"
                    )

                    val fo = FileOutputStream(f)
                    fo.write(byte.toByteArray())
                    fo.close()

                    result = f.absolutePath

                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Files successfully stored at : $result",
                                Toast.LENGTH_SHORT
                            ).show()
                            shareImage(result)
                        }
                        else {
                            Toast.makeText(
                                this@MainActivity,
                                "file is not Successfully stored",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                catch (e:Exception){
                    result =""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun shareImage(result:String){
        MediaScannerConnection.scanFile(this, arrayOf(result),null){
                path, uri ->
            val shareIntent =Intent()
            shareIntent.action=Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
            shareIntent.type="image/png"

            startActivity(Intent.createChooser(shareIntent,"Share"))

        }


    }
}