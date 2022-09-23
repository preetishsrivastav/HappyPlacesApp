package com.example.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a7minuteworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private  var binding :ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.flButton?.setOnClickListener {
            val intent=Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.bmiBtn?.setOnClickListener {
            val intent=Intent(this,BmiCalculatorActivity::class.java)
            startActivity(intent)
        }
        binding?.historyBtn?.setOnClickListener {
            val intent=Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }


    }
}