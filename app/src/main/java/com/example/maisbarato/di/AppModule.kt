package com.example.maisbarato.di

import android.content.Context
import androidx.room.Room
import com.example.maisbarato.database.MaisBaratoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOfertaDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MaisBaratoDatabase::class.java, "maisbarato_database").build()

    @Singleton
    @Provides
    fun provideOfertaDAO(db: MaisBaratoDatabase) = db.ofertaDAO()
}