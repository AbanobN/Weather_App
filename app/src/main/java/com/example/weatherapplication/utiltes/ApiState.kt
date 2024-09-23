package com.example.weatherapplication.utiltes


sealed class ApiState {
    object Success : ApiState()
    data class Failure(val message: Throwable) : ApiState()
    object Loading : ApiState()
}
