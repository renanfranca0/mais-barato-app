package com.example.maisbarato.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.maisbarato.data.model.Oferta
import com.example.maisbarato.data.repository.auth.AuthenticationRepository
import com.example.maisbarato.data.repository.firebase.FirebaseRepository
import com.example.maisbarato.data.repository.local.RepositoryResult
import com.example.maisbarato.util.StateViewResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrudOfertaViewModel @Inject constructor(
    application: Application,
    authRepository: AuthenticationRepository
) : AndroidViewModel(application) {

    val firebaseRepository = FirebaseRepository()

    private val _ofertaStateView = MutableStateFlow<StateViewResult<String>>(StateViewResult.Initial)
    val ofertaStateView = _ofertaStateView.asStateFlow()

    val userUid = authRepository.currentUser?.uid ?: ""

    fun adicionaOferta(oferta: Oferta, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        _ofertaStateView.value = StateViewResult.Loading
        viewModelScope.launch(dispatcher) {
            val response = firebaseRepository.salvaOferta(oferta)

            when (response) {
                is RepositoryResult.Success -> {
                    _ofertaStateView.value = StateViewResult.Success(response.result)
                }
                is RepositoryResult.Error -> {
                    _ofertaStateView.value = StateViewResult.Error(response.error)
                }
            }
        }
    }
}