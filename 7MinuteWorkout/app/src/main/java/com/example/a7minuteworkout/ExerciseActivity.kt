package com.example.a7minuteworkout

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import com.example.a7minuteworkout.databinding.CustomDialogForBackPressedBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding?=null
    private var restTimer :CountDownTimer?=null
    private var restProgress =0

    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgressBar=0

    private var tts:TextToSpeech?=null
    private var adapter:MainAdapter?=null
    private var ExerciseList:ArrayList<ExerciseModel>?=null
   private var currentExercisePosition=-1
    private var upcomingExerciseName=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        tts= TextToSpeech(this,this)

     setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar !=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForExit()
        }

        ExerciseList=Constant.defaultExerciseList()

        setRestView()

        setRecyclerView()
    }

    private fun setRecyclerView() {
        adapter = MainAdapter(ExerciseList!!)

        binding?.rvText?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvText?.adapter = adapter
    }


    override fun onBackPressed() {
        customDialogForExit()
    }

    private fun customDialogForExit() {
      val exitDialog=Dialog(this)
        val dialogBinding:CustomDialogForBackPressedBinding=CustomDialogForBackPressedBinding.inflate(layoutInflater)
        setContentView(dialogBinding.root)

        dialogBinding.btnYes.setOnClickListener {
           this@ExerciseActivity.finish()
           exitDialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {
            exitDialog.dismiss()
        }
        exitDialog.show()
    }

    private fun setRestView(){
        binding?.flProgressBarExercise?.visibility=View.INVISIBLE
        binding?.textExerciseTimer?.visibility=View.INVISIBLE
        binding?.ivExercise?.visibility=View.INVISIBLE
        binding?.textView?.visibility=View.VISIBLE
        binding?.llUpcomingExercise?.visibility=View.VISIBLE
        binding?.flProgressBar?.visibility=View.VISIBLE
        if (restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }
        binding?.upcomingExerciseName?.text=ExerciseList!![upcomingExerciseName].getName()
        speakOut("Upcoming Exercise"+binding?.upcomingExerciseName?.text.toString())
        setRestTimer()

    }
    private fun setExerciseView(){
        binding?.textView?.visibility= View.INVISIBLE
        binding?.llUpcomingExercise?.visibility=View.INVISIBLE
        binding?.flProgressBar?.visibility=View.INVISIBLE
        binding?.flProgressBarExercise?.visibility=View.VISIBLE
        binding?.textExerciseTimer?.visibility=View.VISIBLE
        binding?.ivExercise?.visibility=View.VISIBLE

        if (exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgressBar=0
        }
        binding?.ivExercise?.setImageResource(ExerciseList!![currentExercisePosition].getImage())
        binding?.textExerciseTimer?.text=ExerciseList!![currentExercisePosition].getName()
        setExerciseTimer()

    }


    private fun setExerciseTimer(){

       exerciseTimer=object:CountDownTimer(30000,1000){
           override fun onTick(p0: Long) {
                exerciseProgressBar++
               binding?.textViewCenterExercise?.text=(30-exerciseProgressBar).toString()
               binding?.progressBarExercise?.progress=30-exerciseProgressBar
               speakOut(binding?.textViewCenterExercise?.text.toString())

           }

           override fun onFinish() {
               upcomingExerciseName++
               ExerciseList!![currentExercisePosition].setIsSelected(false)
               ExerciseList!![currentExercisePosition].setIsCompleted(true)

               adapter!!.notifyDataSetChanged()


               if (currentExercisePosition < ExerciseList!!.size-1){
                   setRestView()
               }
               else{
                   Toast.makeText(this@ExerciseActivity,"All Exercises Finished",Toast.LENGTH_SHORT).show()

               }
           }

       }.start()

    }

   private fun setRestTimer(){
        restTimer=object :CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress=10-restProgress
                binding?.textViewCenter?.text=(10-restProgress).toString()

            }
            override fun onFinish() {
               currentExercisePosition++
               ExerciseList!![currentExercisePosition].setIsSelected(true)
                adapter!!.notifyDataSetChanged()

              setExerciseView()
            }


        }.start()
   }

    override fun onDestroy() {
        super.onDestroy()
        if (tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        binding=null
    }

    private fun speakOut(text:String){
        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS){
            val result= tts!!.setLanguage(Locale.US)
            if (result== TextToSpeech.LANG_NOT_SUPPORTED || result== TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS","The Language Specified Is not Supported")
            }
        }
        else{
            Log.e("TTS","Initialisation Failed")
        }
    }
}