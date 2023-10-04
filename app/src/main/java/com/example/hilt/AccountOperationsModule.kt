package com.example.hilt

import com.example.chatapp.firebase.AccountOperations
import com.google.android.datatransport.runtime.dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AccountOperationsModule {

    @Provides
    fun provideAccountOperations(): AccountOperations {
        return AccountOperations()
    }
}