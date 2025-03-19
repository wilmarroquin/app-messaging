package com.example.appumg

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.appumg.messaging.models.User
import com.example.appumg.messaging.firebase.UsersCollection
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var tieneCuenta: TextView
    private lateinit var btnRegistrar: Button
    private lateinit var txtInputEmail: EditText
    private lateinit var txtInputPassword: EditText
    private lateinit var txtInputUserName: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userCollections: UsersCollection
    private lateinit var progressDialog: AlertDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_register)

        userCollections = UsersCollection()
        txtInputEmail = findViewById(R.id.inputEmail_register)
        txtInputPassword = findViewById(R.id.inputPassword_register)
        txtInputUserName = findViewById(R.id.inputUserName)
        btnRegistrar = findViewById(R.id.btn_register)
        tieneCuenta = findViewById(R.id.txtIniciarSesion)

        btnRegistrar.setOnClickListener {
            verificarCredenciales()
        }
        tieneCuenta.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        progressDialog = AlertDialog.Builder(this)
            .setTitle("Registrando...")
            .setMessage("Por favor, espere.")
            .setCancelable(false)
            .create()
    }

    private fun showError(input: EditText, message: String) {
        input.error = message
        input.requestFocus()
    }

    private fun verificarCredenciales() {

        val email = txtInputEmail.text.toString().trim()
        val password = txtInputPassword.text.toString().trim()
        var userName = txtInputUserName.text.toString().trim()

        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@miumg\\.edu\\.gt$")

        when {
            email.isEmpty() -> showError(txtInputEmail, "Ingrese su correo electrónico")
            !email.matches(emailPattern) -> showError(txtInputEmail, "Correo no válido. Use un correo @miumg.edu.gt")
            password.isEmpty() || password.length < 7 -> showError(txtInputPassword, "La contraseña debe tener al menos 7 caracteres")
            userName.isEmpty() || userName.length > 20 -> showError(txtInputUserName, "El nombre de usuario debe ser menor a 20 caracteres")
            else -> {
                progressDialog.show()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressDialog.dismiss()
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            val userId = user?.uid

                            val userData = hashMapOf(
                                "usuarioID" to userId,
                                "pswd" to password, // ⚠️ No es seguro almacenar contraseñas en Firestore
                                "tokenID" to "" // Aquí puedes agregar el token de autenticación si lo necesitas
                            )

                            userId?.let {
                                db.collection("BD_CHAT").document("USER")
                                    .collection("Usuarios").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(applicationContext, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
}