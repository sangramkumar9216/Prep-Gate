package com.example.myapplication.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Repositories are already annotated with @Singleton and @Inject
    // No need to provide them here as they will be automatically injected
}
