package com.example.pethealthmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pethealthmanager.data.repository.AccountRepository
import com.example.pethealthmanager.data.repository.AuthResult
import kotlinx.coroutines.flow.StateFlow

enum class AuthScreen { LOGIN, REGISTER, SUCCESS }

class AuthViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    companion object {
        const val MAX_ATTEMPTS = 3
        const val USERNAME_MIN_LENGTH = 3
        const val PASSWORD_MIN_LENGTH = 6
    }

    val currentUser: StateFlow<String?> = accountRepository.currentUser

    var authActive by mutableStateOf(false)
        private set

    var screen by mutableStateOf(AuthScreen.LOGIN)
        private set

    var loginName by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    var registerName by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirm by mutableStateOf("")
        private set

    var message by mutableStateOf<String?>(null)
        private set
    var messageIsError by mutableStateOf(false)
        private set

    var attemptsLeft by mutableIntStateOf(MAX_ATTEMPTS)
        private set

    var loggedInName by mutableStateOf(accountRepository.currentUser.value.orEmpty())
        private set

    val isLoginLocked: Boolean get() = attemptsLeft <= 0

    fun startLogin() {
        authActive = true
        screen = AuthScreen.LOGIN
        registerPassword = ""
        registerConfirm = ""
        clearMessage()
    }

    fun startRegister() {
        authActive = true
        screen = AuthScreen.REGISTER
        registerName = ""
        registerPassword = ""
        registerConfirm = ""
        clearMessage()
    }

    fun exitAuth() {
        authActive = false
        loginPassword = ""
        registerPassword = ""
        registerConfirm = ""
        clearMessage()
    }

    fun finishAuth() {
        authActive = false
        screen = AuthScreen.LOGIN
        loginPassword = ""
        clearMessage()
    }

    fun updateLoginName(value: String) {
        loginName = value
        clearMessage()
    }

    fun updateLoginPassword(value: String) {
        loginPassword = value
        clearMessage()
    }

    fun updateRegisterName(value: String) {
        registerName = value
        clearMessage()
    }

    fun updateRegisterPassword(value: String) {
        registerPassword = value
        clearMessage()
    }

    fun updateRegisterConfirm(value: String) {
        registerConfirm = value
        clearMessage()
    }

    fun login() {
        if (isLoginLocked) return

        val name = loginName.trim()
        val password = loginPassword

        if (name.isEmpty() || password.isEmpty()) {
            showMessage("Please enter your username and password.", isError = true)
            return
        }

        when (accountRepository.authenticate(name, password)) {
            AuthResult.Success -> {
                loggedInName = name
                loginPassword = ""
                attemptsLeft = MAX_ATTEMPTS
                clearMessage()
                screen = AuthScreen.SUCCESS
            }

            AuthResult.UnknownUser -> consumeAttempt("Please register first...")
            AuthResult.WrongPassword -> consumeAttempt("Wrong password.")
            AuthResult.UserExists -> Unit
        }
    }

    private fun consumeAttempt(prefix: String) {
        attemptsLeft = (attemptsLeft - 1).coerceAtLeast(0)
        if (attemptsLeft == 0) {
            showMessage("Too many failed attempts. LOGIN is now disabled.", isError = true)
        } else {
            val unit = if (attemptsLeft == 1) "attempt" else "attempts"
            showMessage("$prefix $attemptsLeft $unit left.", isError = true)
        }
    }

    fun register() {
        val name = registerName.trim()

        when {
            name.length < USERNAME_MIN_LENGTH ->
                showMessage("Username needs at least $USERNAME_MIN_LENGTH characters.", isError = true)

            registerPassword.length < PASSWORD_MIN_LENGTH ->
                showMessage("Password needs at least $PASSWORD_MIN_LENGTH characters.", isError = true)

            registerPassword != registerConfirm ->
                showMessage("Passwords do not match.", isError = true)

            else -> when (accountRepository.register(name, registerPassword)) {
                AuthResult.UserExists ->
                    showMessage("This username is already registered.", isError = true)

                AuthResult.Success -> {
                    loginName = name
                    loginPassword = ""
                    registerName = ""
                    registerPassword = ""
                    registerConfirm = ""
                    attemptsLeft = MAX_ATTEMPTS
                    screen = AuthScreen.LOGIN
                    showMessage("Registration successful. Please log in.", isError = false)
                }

                else -> showMessage("Registration failed. Please try again.", isError = true)
            }
        }
    }

    fun goToRegister() {
        registerName = ""
        registerPassword = ""
        registerConfirm = ""
        clearMessage()
        screen = AuthScreen.REGISTER
    }

    fun goToLogin() {
        clearMessage()
        screen = AuthScreen.LOGIN
    }

    fun logout() {
        accountRepository.logout()
        loginPassword = ""
        loggedInName = ""
        attemptsLeft = MAX_ATTEMPTS
        authActive = false
        screen = AuthScreen.LOGIN
        clearMessage()
    }

    private fun showMessage(text: String, isError: Boolean) {
        message = text
        messageIsError = isError
    }

    private fun clearMessage() {
        if (message != null) message = null
    }
}
