package com.example.maisbarato.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maisbarato.data.model.Oferta
import com.example.maisbarato.data.repository.OfferDataSource
import com.example.maisbarato.data.repository.auth.AuthenticationRepository
import com.example.maisbarato.data.repository.local.RepositoryResult
import com.example.maisbarato.util.DateUtil
import com.example.maisbarato.util.StateViewResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaOfertasViewModel @Inject constructor(
    private val ofertaRepository: OfferDataSource,
    authRepository: AuthenticationRepository
) : ViewModel() {

    private var _stateView: MutableStateFlow<StateViewResult<Any>?> = MutableStateFlow(null)
    val stateView get() = _stateView.asStateFlow()

    private var _oferta = MutableStateFlow<List<Oferta>>(listOf())
    val oferta get() = _oferta.asStateFlow()

    private var userUid = authRepository.currentUser?.uid ?: ""

    fun lerTodasOfertas(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        _stateView.value = StateViewResult.Loading
        viewModelScope.launch(dispatcher) {
            val listOffer = ofertaRepository.getAllOffers()

            when (listOffer) {
                is RepositoryResult.Success -> {
                    listOffer.result.collect { list ->
                        _oferta.emit(list)
                        _stateView.value = StateViewResult.Success()
                    }
                }
                is RepositoryResult.Error -> {
                    _stateView.value = StateViewResult.Error("Falha ao carregar lista de ofertas")
                }
            }
        }
    }

    fun addOfferToHistory(offer: Oferta, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(dispatcher) {
            offer.dataAcesso = DateUtil.getCurrentTime()
            ofertaRepository.addOfferToHistory(userUid, offer)
        }
    }

}