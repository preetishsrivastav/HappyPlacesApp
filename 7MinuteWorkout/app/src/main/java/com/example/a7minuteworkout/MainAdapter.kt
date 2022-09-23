package com.example.a7minuteworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minuteworkout.databinding.RecylerViewLayoutBinding

class MainAdapter(val items:ArrayList<ExerciseModel>):RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    class MainViewHolder(itemBinding:RecylerViewLayoutBinding):RecyclerView.ViewHolder(itemBinding.root){
        var tvItem=itemBinding.tvItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
         return MainViewHolder(RecylerViewLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
       val model:ExerciseModel= items[position]
       holder.tvItem.text=model.getId().toString()

        when{
            model.getIsCompleted()->{
                holder.tvItem.background=ContextCompat.getDrawable(holder.itemView.context,R.drawable.rv_item_bakground_exercise_finish)
                holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))

            }
            model.getIsSelected()->{
                holder.tvItem.background=ContextCompat.getDrawable(holder.itemView.context,R.drawable.rv_item_bakground_exercise_selected)
                holder.tvItem.setTextColor(Color.parseColor("212121"))

            }
            else->{
                holder.tvItem.background=ContextCompat.getDrawable(holder.itemView.context,R.drawable.rv_item_background_gray)
                holder.tvItem.setTextColor(Color.parseColor("212121"))

            }

        }
    }

    override fun getItemCount(): Int {
        return items.size

    }


}