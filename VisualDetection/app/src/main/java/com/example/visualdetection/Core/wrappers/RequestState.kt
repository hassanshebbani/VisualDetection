package com.example.visualdetection.Core.wrappers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull(): T? {
        return try {
            (this as Success).data
        } catch (e: Exception) {
            null
        }
    }

    fun getErrorMessage() = (this as Error).message
    fun getErrorMsgOrNull(): String? {
        return try {
            (this as Error).message
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun DisplayResult(
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable () -> Unit,
        onError: @Composable () -> Unit,
    ) {
        AnimatedContent(targetState = this, transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        }, label = "Request State") { state ->
            when (state) {
                Idle -> {
                    onIdle?.invoke()
                }

                Loading -> {
                    onLoading.invoke()
                }

                is Success -> {
                    onSuccess.invoke()
                }

                is Error -> {
                    onError.invoke()
                }
            }
        }
    }


}