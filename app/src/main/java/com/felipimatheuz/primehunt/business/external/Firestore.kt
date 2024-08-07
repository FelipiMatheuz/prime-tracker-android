package com.felipimatheuz.primehunt.business.external

import android.content.Context
import com.felipimatheuz.primehunt.business.resources.OtherPrimeData
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.business.util.apiOther
import com.felipimatheuz.primehunt.business.util.apiSet
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class Firestore {

    fun importCheckList(context: Context, userId: String, state: MutableStateFlow<SyncState>) {
        val document = Firebase.firestore.collection("checklist").document(userId)
        document.get().addOnCompleteListener { doc ->
            if (doc.exception != null) {
                state.update {
                    SyncState.Error(doc.exception!!.toString())
                }
            }
            val setData = PrimeSetData(context)
            val otherData = OtherPrimeData(context)
            //update api set
            val mapSet = doc.result.get("SET") as Map<*, *>
            apiSet.getSetData().forEach { primeSet ->
                primeSet.primeItems.forEach { primeItem ->
                    val fieldBp = getFieldName(primeSet, primeItem)
                    val bpObtained = mapSet[fieldBp] as Boolean?
                    setData.setStatusItem(fieldBp, bpObtained ?: false)
                    primeItem.blueprint = bpObtained ?: false
                    val distComp = primeItem.components.distinctBy { it.part }
                    distComp.forEach { dist ->
                        val itemComp = primeItem.components.filter { it.part == dist.part }
                        itemComp.forEachIndexed { index, primeComp ->
                            val fieldName = getFieldName(primeSet, primeItem, primeComp, index)
                            val compObtained =
                                mapSet[fieldName] as Boolean?
                            setData.setStatusItem(fieldName, compObtained ?: false)
                            primeComp.obtained = compObtained ?: false
                        }
                    }
                }
            }
            //update api other
            val mapOther = doc.result.get("OTHER") as Map<*, *>
            apiOther.getOtherData().forEach { primeItem ->
                val fieldBp = getFieldName(primeItem = primeItem)
                val bpObtained = mapOther[fieldBp] as Boolean?
                if (bpObtained != null) {
                    otherData.setStatusItem(fieldBp, bpObtained)
                    primeItem.blueprint = bpObtained
                }
                val distComp = primeItem.components.distinctBy { it.part }
                distComp.forEach { dist ->
                    val itemComp = primeItem.components.filter { it.part == dist.part }
                    itemComp.forEachIndexed { index, primeComp ->
                        val fieldName = getFieldName(primeItem = primeItem, primeComp = primeComp, index = index)
                        val compObtained = mapOther[fieldName] as Boolean?
                        if(compObtained!=null){
                            otherData.setStatusItem(fieldName, compObtained)
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

    fun exportCheckList(context: Context, userId: String, state: MutableStateFlow<SyncState>) {
        val setData = PrimeSetData(context).getLocalData()
        val otherData = OtherPrimeData(context).getLocalData()
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