package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.data.cloud.Firestore
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import javax.inject.Inject

class ClearCloudDataUseCase @Inject constructor(
    private val firestore: Firestore
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        return firestore.deleteCloudData(userId)
    }
}
