package com.example.walkapp.di

import android.content.Context
import com.example.walkapp.helpers.LocationManager
import com.example.walkapp.repositories.AuthRepository
import com.example.walkapp.repositories.BadgeRepository
import com.example.walkapp.repositories.GroupRepository
import com.example.walkapp.repositories.LeaderboardRepository
import com.example.walkapp.repositories.PerformanceRepository
import com.example.walkapp.repositories.UserRepository
import com.example.walkapp.repositories.WalkRepository
import com.example.walkapp.viewmodels.AuthViewModel
import com.example.walkapp.viewmodels.AvatarMakerViewModel
import com.example.walkapp.viewmodels.BadgeViewModel
import com.example.walkapp.viewmodels.EnterGroupViewModel
import com.example.walkapp.viewmodels.GroupViewModel
import com.example.walkapp.viewmodels.HistoricViewModel
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.viewmodels.LeaderboardViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.viewmodels.PerformanceViewModel
import com.example.walkapp.viewmodels.UserFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        single { AuthRepository() }
        viewModel { AuthViewModel(get()) }

        single { UserRepository() }
        viewModel { (nickname: String, walkingGoal: String, avatarIndex: Int) ->
                UserFormViewModel(get(), nickname, walkingGoal, avatarIndex)
        }

        viewModel { AvatarMakerViewModel(get()) }

        viewModel { HomeViewModel(get()) }

        single {
                val context = get<Context>()
                LocationManager.initialize(context)
                LocationManager
        }

        viewModel { LocationViewModel(get()) }

        viewModel { HistoricViewModel(get()) }

        single { PerformanceRepository() }
        viewModel { PerformanceViewModel(get()) }

        single { BadgeRepository() }
        viewModel { BadgeViewModel(get()) }

        single { WalkRepository(get(), get(), get()) }

        single { LeaderboardRepository() }
        viewModel { LeaderboardViewModel(get()) }

        single { GroupRepository() }
        viewModel { GroupViewModel(get(), get()) }

        viewModel { EnterGroupViewModel(get()) }
}