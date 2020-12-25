package com.example.myapplication

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var firstNameEt: EditText
    private lateinit var lastNameEt: EditText
    private lateinit var confirmPassEt: EditText

    private lateinit var emailTv: TextView
    private lateinit var firstnameTv: TextView
    private lateinit var lastnameTv: TextView

    private lateinit var symptomEt: EditText
    private lateinit var infoEt: EditText
    private lateinit var doctorEt: EditText


    private lateinit var signupBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var nextBtn: Button

    private lateinit var listViewLv: ListView

    private lateinit var dataOut: TextView

    private lateinit var loadData: Button

    private lateinit var resetPasswordTv: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEt = findViewById(R.id.emailLoginText)
        passwordEt = findViewById(R.id.passwordLoginText)



        signupBtn = findViewById(R.id.buttonRegister)
        loginBtn = findViewById(R.id.buttonLogin)

        auth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener {
            var email: String = emailEt.text.toString()
            var password: String = passwordEt.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@MainActivity, "Please fill all the fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                            setContentView(R.layout.activity_main4)

                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }

        signupBtn.setOnClickListener {
            setContentView(R.layout.activity_main2)

            auth = FirebaseAuth.getInstance()

            emailEt = findViewById(R.id.emailRegisterText)
            passwordEt = findViewById(R.id.passwordRegisterText)
            firstNameEt = findViewById(R.id.firstnameRegisterText)
            lastNameEt = findViewById(R.id.lastnameRegisterText)
            confirmPassEt = findViewById(R.id.confirmPasswordRegisterText)

            signupBtn = findViewById(R.id.buttonRegister2)

            signupBtn.setOnClickListener {
                var email: String = emailEt.text.toString()
                var password: String = passwordEt.text.toString()
                var firstName: String = firstNameEt.text.toString()
                var lastName: String = lastNameEt.text.toString()
                var confirmPass: String = confirmPassEt.text.toString()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val db = FirebaseFirestore.getInstance()
                                val appoint: MutableMap<String, Any> =
                                    HashMap()
                                appoint["email"] = email
                                appoint["password"] = password
                                appoint["firstname"] = firstName
                                appoint["lastname"] = lastName
                                db.collection("users")
                                    .add(appoint)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Data added", Toast.LENGTH_LONG).show()
                                    }
                                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG)
                                    .show()
                                setContentView(R.layout.activity_main)
                            } else {
                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
                }
            }

        }


    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        setContentView(R.layout.activity_main)
    }

    fun loadData(view: View) {
        val db = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_main7)
        listViewLv = findViewById(R.id.listView)
        val listVList = ArrayList<String>();


        db.collection("appointments")
            .whereEqualTo("user", auth.currentUser?.email)
            .get()
            .addOnSuccessListener { task ->
                for (document in task) {
                 listVList.add("You have appointment with doctor " +  document.data.get("doctor").toString()
                         + ". Your symptoms are: " + document.data.get("symptoms").toString())
                    println("+=================================================+")
                    print(listVList)
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1, listVList
                    )
                    listViewLv.setAdapter(adapter)
                }

            }
    }

    fun addData(view: View) {
        nextBtn = findViewById(R.id.nextButtonAppoint)
        symptomEt = findViewById(R.id.syptomsTextBox)
        infoEt = findViewById(R.id.additionalInfoTextBox)
        doctorEt = findViewById(R.id.doctorTypeTextBox)
        var info: String = infoEt.text.toString()
        var doctor: String = doctorEt.text.toString()
        var symptom: String = symptomEt.text.toString()

        nextBtn.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val appoint: MutableMap<String, Any> =
                HashMap()
            appoint["doctor"] = doctor
            appoint["symptoms"] = symptom
            appoint["info"] = info
            appoint["user"] = auth.currentUser?.email.toString()

            db.collection("appointments")
                .add(appoint)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data added", Toast.LENGTH_LONG).show()
                    setContentView(R.layout.activity_main10)
                    val db = FirebaseFirestore.getInstance()

                    db.collection("users")
                        .whereEqualTo("email", auth.currentUser?.email)
                        .get()
                        .addOnSuccessListener { task ->
                            emailTv = findViewById(R.id.emailTvOut)
                            firstnameTv = findViewById(R.id.firstnameTvOut)
                            lastnameTv = findViewById(R.id.lastnameTvOut)

                            for (document in task) {
                                emailTv.text = document.data.get("email").toString()
                                firstnameTv.text = document.data.get("firstname").toString()
                                lastnameTv.text = document.data.get("lastname").toString()
                            }
                        }
                }
        }

    }

}



