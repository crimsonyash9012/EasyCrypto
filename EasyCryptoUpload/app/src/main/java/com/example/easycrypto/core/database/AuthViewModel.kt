package com.example.easycrypto.core.database


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AppwriteRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signUpUser(email: String, username: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.signUpUser(
                email = email,
                password = password,
                username = username,
                onSuccess = {
                    _authState.value = AuthState.Success
                },
                onError = {
                    _authState.value = AuthState.Error(it)
                }
            )
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.loginUser(
                    email,
                    password,
                    onSuccess = {
                        _authState.value = AuthState.Success
                    },
                    onError = {
                        _authState.value = AuthState.Error(it)
                    })
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }


    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
