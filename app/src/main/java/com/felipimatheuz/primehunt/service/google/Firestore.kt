package com.felipimatheuz.primehunt.service.google

import com.felipimatheuz.primehunt.business.resources.OtherPrimeData
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.felipimatheuz.primehunt.business.util.otherPrimeList
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class Firestore @Inject constructor(
    private val primeSetData: PrimeSetData,
    private val otherPrimeData: OtherPrimeData
) {

    fun importCheckList(userId: String, state: MutableStateFlow<SyncState>) {
        val document = Firebase.firestore.collection("checklist").document(userId)
        document.get().addOnCompleteListener { doc ->
            if (doc.exception != null) {
                state.update {
                    SyncState.Error(doc.exception!!.toString())
                }
            }

            //update api set
            val mapSet = doc.result.get("SET") as Map<*, *>
            primeSetList.forEach { primeSet ->
                primeSet.primeItems.forEach { primeItem ->
                    val fieldBp = getFieldName(primeSet, primeItem)
                    val bpObtained = mapSet[fieldBp] as Boolean?
                    primeSetData.setStatusItem(fieldBp, bpObtained ?: false)
                    primeItem.blueprint = bpObtained ?: false
                    val distComp = primeItem.components.distinctBy { it.part }
                    distComp.forEach { dist ->
                        val itemComp = primeItem.components.filter { it.part == dist.part }
                        itemComp.forEachIndexed { index, primeComp ->
                            val fieldName = getFieldName(primeSet, primeItem, primeComp, index)
                            val compObtained =
                                mapSet[fieldName] as Boolean?
                            primeSetData.setStatusItem(fieldName, compObtained ?: false)
                            primeComp.obtained = compObtained ?: false
                        }
                    }
                }
            }
            //update api other
            val mapOther = doc.result.get("OTHER") as Map<*, *>
            otherPrimeList.forEach { primeItem ->
                val fieldBp = getFieldName(primeItem = primeItem)
                val bpObtained = mapOther[fieldBp] as Boolean?
                if (bpObtained != null) {
                    otherPrimeData.setStatusItem(fieldBp, bpObtained)
                    primeItem.blueprint = bpObtained
                }
                val distComp = primeItem.components.distinctBy { it.part }
                distComp.forEach { dist ->
                    val itemComp = primeItem.components.filter { it.part == dist.part }
                    itemComp.forEachIndexed { index, primeComp ->
                        val fieldName = getFieldName(
                            primeItem = primeItem,
                            primeComp = primeComp,
                            index = index
                        )
                        val compObtained = mapOther[fieldName] as Boolean?
                        if (compObtained != null) {
                            otherPrimeData.setStatusItem(fieldName, compObtained)
                            primeComp.obtained = compObtained
                        }
                    }
                }
            }
            state.update {
                SyncState.SuccessImport
            }
        }
    }

    fun exportCheckList(userId: String, state: MutableStateFlow<SyncState>) {
        val setData = primeSetData.getLocalData()
        val otherData = otherPrimeData.getLocalData()

        Firebase.firestore.collection("checklist").document(userId)
            .set(mapOf("SET" to setData.all, "OTHER" to otherData.all))
            .addOnCompleteListener { task ->
                state.update {
                    if (task.exception != null) {
                        SyncState.Error(task.exception.toString())
                    } else {
                        SyncState.SuccessExport
                    }
                }
            }
    }
}