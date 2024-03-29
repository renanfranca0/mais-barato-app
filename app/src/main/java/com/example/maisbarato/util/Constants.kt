package com.example.maisbarato.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.maisbarato.R

const val COLLECTION_OFERTA = "oferta"
const val COLLECTION_USUARIO = "usuario"

const val SUBCOLLECTION_FAVORITOS = "favoritos"
const val SUBCOLLECTION_HISTORY = "history"

const val FIELD_EMAIL = "email"
const val FIELD_NOME = "nome"
const val FIELD_TELEFONE = "telefone"
const val FIELD_URL_IMAGEM_PERFIL = "urlImagemPerfil"

const val PATH_IMAGENS = "imagens"
const val PATH_USUARIOS = "usuarios"

val PREFERENCE_EMAIL = stringPreferencesKey("email_data_key")
val PREFERENCE_SENHA = stringPreferencesKey("senha_data_key")
val PREFERENCE_SWITCH_STATUS = booleanPreferencesKey("switch_status_key")
val PREFERENCE_USER_UID = stringPreferencesKey("user_uid")
val PREFERENCE_URL_IMAGEM_USUARIO = stringPreferencesKey("url_imagem_usuario")



val telasComIconeMenuHamburguer = setOf(
    R.id.listaOfertasFragment
)

val telasSemToolbar = setOf(
    R.id.loginFragment,
    R.id.cadastroFragment
)

val telasSemMenuDrawer = setOf(
    R.id.loginFragment,
    R.id.cadastroFragment,
    R.id.detalhesOfertaFragment,
    R.id.userProfileFragment,
    R.id.historyFragment
)