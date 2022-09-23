package com.example.a7minuteworkout


data class ExerciseModel(
   private var id:Int,
   private var name:String,
  private var image:Int,
   private var isSelected:Boolean,
   private var isCompleted:Boolean
) {

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getImage(): Int {
        return image
    }

    fun setImage(image: Int) {
        this.image = image
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun getIsSelected():Boolean{
        return isSelected
    }
    fun setIsSelected(isSelected: Boolean){
        this.isSelected=isSelected
    }
    fun getIsCompleted():Boolean{
        return isCompleted
    }
    fun setIsCompleted(isCompleted: Boolean){
        this.isCompleted=isCompleted
    }
}