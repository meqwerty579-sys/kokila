package com.example.domain.usecase

import com.example.data.repository.ModelRepository
import javax.inject.Inject

class ValidateModelUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    operator fun invoke(modelPath: String): Boolean {
        return modelRepository.validateInstalledModel(modelPath)
    }
}
