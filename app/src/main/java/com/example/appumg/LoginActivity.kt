package com.example.appumg

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var lblCrearCuenta: TextView
    private lateinit var txtInputEmail: EditText
    private lateinit var txtInputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var lblOlvidasteContra: TextView
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtInputEmail = findViewById(R.id.inputEmail)
        txtInputPassword = findViewById( R.id.inputPassword)
        btnLogin = findViewById(R.id.btnlogin)
        lblCrearCuenta = findViewById(R.id.txtNotieneCuenta)
        lblOlvidasteContra = findViewById(R.id.forgotPassword)

        lblCrearCuenta.setOnClickListener {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            //verificarCredenciales()
        }
        lblOlvidasteContra.setOnClickListener {
            startActivity(Intent(this, LoginPasswActivity::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
    }

    /* private fun verificarCredenciales() {
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
                            val intent = Intent(this, ChatsActivity::class.java)
                            intent.putExtra("email", email)
                            user?.let {
                                intent.putExtra("userId", it.uid)
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
    } */

    private fun showError(input: EditText, message: String) {
        input.error = message
        input.requestFocus()
    }
}
