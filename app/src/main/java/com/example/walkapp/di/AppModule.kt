package com.example.walkapp.di

import android.content.Context
import com.example.walkapp.helpers.LocationManager
import com.example.walkapp.repositories.AuthRepository
import com.example.walkapp.repositories.UserRepository
import com.example.walkapp.viewmodels.AuthViewModel
import com.example.walkapp.viewmodels.AvatarMakerViewModel
import com.example.walkapp.viewmodels.WalkViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.viewmodels.UserFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        single { AuthRepository() }
        viewModel { AuthViewModel(get()) }

        single { UserRepository() }
        viewModel { UserFormViewModel(get()) }

        viewModel { AvatarMakerViewModel(get()) }

        viewModel { WalkViewModel(get()) }

        single {
                val context = get<Context>()
                LocationManager.initialize(context)
                LocationManager
        }

        viewModel { LocationViewModel(get()) }

}