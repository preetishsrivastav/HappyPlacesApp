package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context,attrs:AttributeSet):View(context, attrs) {
     private var mDrawPath :CustomPath?=null
     private var mDrawPaint:Paint?=null
     private var mCanvasBitmap:Bitmap?=null
     private var mCanvasPaint:Paint?=null
     private var canvas:Canvas?=null
    private var brushSize:Float=0.toFloat()
    private var color:Int=Color.BLACK
    private var mPath=ArrayList<CustomPath>()
    private var mUndoPath=ArrayList<CustomPath>()

    internal inner class CustomPath(var color:Int,var brushThickness:Float):Path(){
    }

    init {
        setUpDrawing()
    }
   private fun setUpDrawing(){
       mDrawPath=CustomPath(color,brushSize)
       mDrawPaint= Paint()
       mDrawPaint!!.color=color
       mDrawPaint!!.style=Paint.Style.STROKE
       mDrawPaint!!.strokeCap=Paint.Cap.ROUND
       mDrawPaint!!.strokeJoin=Paint.Join.ROUND
       mCanvasPaint= Paint(Paint.DITHER_FLAG)
       brushSize=20.toFloat()

   }
    fun onClickUndo(){
        if (mPath.size>0){
        mUndoPath.add(mPath.removeAt(mPath.size-1))
        invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mDrawPaint)
        for(path in mPath) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)

        }
    }
    fun setSizeBrush(newSize:Float){
        brushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth=brushSize
    }
    fun setColor(newColor:String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX =event?.x
        val touchY =event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mDrawPath!!.color=color
                mDrawPath!!.brushThickness=brushSize

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                        mPath.add(mDrawPath!!)
                    }
                }

            }
            MotionEvent.ACTION_UP->{
                mDrawPath=CustomPath(color,brushSize)

            }
            else->
                return false
        }
        invalidate()

        return true




    }







}