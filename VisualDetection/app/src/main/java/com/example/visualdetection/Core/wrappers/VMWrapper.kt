package com.example.visualdetection.Core.wrappers

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.visualdetection.Core.presentation.viewmodel.CoreVM
import com.example.visualdetection.ML.presentation.viewmodel.MLVM
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject


class VMWrapper @Inject constructor(
    private val coreViewModel: CoreVM,
    private val mlViewModel: MLVM,
) {

    val coreVM: CoreVM
        get() = coreViewModel

    val mlVM: MLVM
        get() = mlViewModel
}