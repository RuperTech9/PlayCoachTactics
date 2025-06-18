package com.example.playcoachtactics.di

import android.content.Context
import com.example.playcoachtactics.data.repositories.FormationRepository
import com.example.playcoachtactics.data.repositories.TeamRepository
import com.example.playcoachtactics.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFormationRepository(
        firestore: FirebaseFirestore
    ): FormationRepository {
        return FormationRepository(firestore)
    }

    @Provides
    fun provideTeamRepository(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): TeamRepository {
        return TeamRepository(firestore, context)
    }

    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): UserRepository {
        return UserRepository(firestore, auth)
    }
}
