package com.example.icebreaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputBinding
import androidx.appcompat.app.AppCompatDelegate
import com.example.icebreaker.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG="IceBreakerAndroidDebug"
    private val db=Firebase.firestore
    private var questionBank:MutableList<Question> = arrayListOf()
    //Check if code is auto-saved via fork
    //Add TODO LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getQuestionsFromFirebase()
        binding.btnGetQuestion.setOnClickListener{
            //If the button gets pressed, this code will run
            Log.d("IceBreakerAndroidDebug","Button Get Question Was Pressed")
            Log.d(TAG,"Question grabbed: ${questionBank?.random()}")
            binding.txtQuestion.text= questionBank?.random().toString()
        }
        binding.btnSubmit.setOnClickListener{
            Log.d("IceBreakerAndroidDebug","Button Submit Was Pressed")
            val firstName=binding.txtFirstName
            Log.d("IceBreakerAndroidDebug","First name is ${firstName.text}")
            val lastName=binding.txtLastName
            Log.d("IceBreakerAndroidDebug","Last name is ${lastName.text}")
            val preferredName=binding.preferredName
            Log.d(TAG,"Preferred name is ${preferredName.text}")
            val answer=binding.txtAnswer
            writeStudentToFirebase()
        }
        //getQuestionsFromFirebase()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    private fun getQuestionsFromFirebase()
    {
        Log.d("IceBreakerAndroidDebug","Fetching Questions from Firebase")
        db.collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                questionBank= mutableListOf()
                for(document in documents){
                    Log.d(TAG, "${document.id}=> ${document.data}")

                    val question=document.toObject(Question::class.java)
                    Log.d(TAG, question.text)
                    questionBank.add(question)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents", exception)
            }
    }
    private fun writeStudentToFirebase(){
        val firstName=binding.txtFirstName
        val lastName=binding.txtLastName
        val preferredName=binding.preferredName
        val answer=binding.txtAnswer

        val student= hashMapOf(
            "firstName" to firstName.text.toString(),
            "lastName" to lastName.text.toString(),
            "preferredName" to preferredName.text.toString(),
            "answer" to answer.text.toString(),
            "question" to binding.txtQuestion.text
        )

        db.collection("students")
            .add(student)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        firstName.setText(" ")
        lastName.setText("")
        preferredName.setText("")
        answer.setText("")
    }
    }
