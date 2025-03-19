package com.example.appumg

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var lblCrearCuenta: TextView
    private lateinit var txtInputEmail: EditText
    private lateinit var txtInputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var lblOlvidasteContra: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtInputEmail = findViewById(R.id.inputEmail)
        txtInputPassword = findViewById( R.id.inputPassword)
        btnLogin = findViewById(R.id.btnlogin)
        lblCrearCuenta = findViewById(R.id.txtNotieneCuenta)
        lblOlvidasteContra = findViewById(R.id.forgotPassword)
        progressBar = findViewById(R.id.progressBar)

        lblCrearCuenta.setOnClickListener {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            verificarCredenciales()
        }
        lblOlvidasteContra.setOnClickListener {
            startActivity(Intent(this, LoginPasswActivity::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }
    private fun verificarCredenciales() {
        val email = txtInputEmail.text.toString().trim()
        val password = txtInputPassword.text.toString().trim()

        when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                showError(txtInputEmail, "Por favor ingrese un correo electrónico válido")

            password.isEmpty() || password.length < 8 ->
                showError(txtInputPassword, "La contraseña debe tener al menos 8 caracteres")

            else -> {
                progressBar.visibility = View.VISIBLE  // Mostrar barra de carga

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE  // Ocultar barra de carga

                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            user?.let {
                                verificarUsuarioEnFirestore(it.uid, email)
                            }
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error: ${task.exception?.localizedMessage ?: "No se pudo iniciar sesión"}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun verificarUsuarioEnFirestore(userId: String, email: String) {
        Log.d("Firestore", "Buscando usuario con ID: $userId")
        db.collection("BD_CHAT").document("USER")
            .collection("Usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Usuario encontrado en Firestore
                    val intent = Intent(this, ChatsActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("userId", userId)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    // Usuario no registrado en Firestore
                    Toast.makeText(applicationContext, "Usuario no registrado en Firestore.", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al conectar con Firestore.", Toast.LENGTH_LONG).show()
            }
    }

    private fun showError(input: EditText, message: String) {
        input.error = message
        input.requestFocus()
    }
}
