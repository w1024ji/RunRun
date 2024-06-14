package com.example.runrun

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Dex는 Dalvic Executable이다. (64k) 실행파일
class MyApplication : MultiDexApplication() {
    companion object{
        lateinit var auth : FirebaseAuth
        var email : String? = null

        fun checkAuth() : Boolean {
            var currentUser = auth.currentUser
            if(currentUser != null){
                email = currentUser.email
                return currentUser.isEmailVerified // 이메일이 verified되면 true
            }
            return false // 유효한 유저가 아니면 false
        }
    }
    override fun onCreate(){
        super.onCreate()
        auth = Firebase.auth
    }
}