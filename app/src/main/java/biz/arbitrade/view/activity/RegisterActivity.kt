package biz.arbitrade.view.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.RegisterController
import biz.arbitrade.model.RegisterForm
import biz.arbitrade.model.User
import biz.arbitrade.view.dialog.Loading
import java.util.*
import kotlin.concurrent.schedule

class RegisterActivity : AppCompatActivity() {
    private lateinit var controller: RegisterController
    private lateinit var user: User
    private lateinit var loading: Loading
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var passwordConfirm: EditText
    private lateinit var walletDax: EditText
    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        user = User(this)
        loading = Loading(this)

        controller = RegisterController()
        name = findViewById(R.id.editTextName)
        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        passwordConfirm = findViewById(R.id.editTextConfirmPassword)
        walletDax = findViewById(R.id.editTextWalletDax)
        registerBtn = findViewById(R.id.btnRegister)

        registerBtn.setOnClickListener {
            loading.openDialog()
            val newUser = RegisterForm(
                name.text.toString(),
                username.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordConfirm.text.toString(),
                walletDax.text.toString(),
            )
            Timer().schedule(100) {
                val response = controller.doRegister(user, newUser)
                if (response.getInt("code") >= 400) {
                    Toast.makeText(
                        applicationContext,
                        response.getString("message"),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "User ${username.text} successfully created!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                loading.closeDialog()
            }
        }
    }
}