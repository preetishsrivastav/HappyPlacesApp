package com.example.a7minuteworkout

object Constant {
    fun defaultExerciseList():ArrayList<ExerciseModel>{
        var exerciseList=ArrayList<ExerciseModel>()

        val jumpingJacks =ExerciseModel(1,"Jumping Jacks",R.drawable.ic_jumping_jacks,false,false)
         exerciseList.add(jumpingJacks)

        val lunge =ExerciseModel(2,"Lunge",R.drawable.ic_lunge,false,false)
        exerciseList.add(lunge)

        val highKneesRunningInPlace =ExerciseModel(3,"High Knees Running Place",R.drawable.ic_high_knees_running_in_place,false,false)
        exerciseList.add(highKneesRunningInPlace)

        val abdominalCrunch =ExerciseModel(4,"Abdominal Crunch",R.drawable.ic_abdominal_crunch,false,false)
        exerciseList.add(abdominalCrunch)

        val plank =ExerciseModel(5,"Plank",R.drawable.ic_plank,false,false)
        exerciseList.add(plank)

        val pushUp =ExerciseModel(6,"Push Up",R.drawable.ic_push_up,false,false)
        exerciseList.add(pushUp)

        val pushUpAndRotation =ExerciseModel(7,"Push Up And Rotation",R.drawable.ic_push_up_and_rotation,false,false)
        exerciseList.add(pushUpAndRotation)

        val squat =ExerciseModel(8,"Squat",R.drawable.ic_squat,false,false)
        exerciseList.add(squat)

        val stepUpOnToChair =ExerciseModel(9,"Step Up On To Chair",R.drawable.ic_step_up_onto_chair,false,false)
        exerciseList.add(stepUpOnToChair)

        val tricepsDipOnChair =ExerciseModel(10,"Triceps Dip On Chair",R.drawable.ic_triceps_dip_on_chair,false,false)
        exerciseList.add(tricepsDipOnChair)

        val wallSit =ExerciseModel(11,"Wall Sit",R.drawable.ic_wall_sit,false,false)
        exerciseList.add(wallSit)

        val sidePlank =ExerciseModel(12,"Side Plank",R.drawable.ic_side_plank,false,false)
        exerciseList.add(sidePlank)



        return exerciseList

    }

}