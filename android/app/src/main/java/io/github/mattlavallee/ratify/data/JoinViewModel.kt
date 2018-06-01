package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group

class JoinViewModel: ViewModel {
    private val joinedGroup: MutableLiveData<Pair<String, Group?>> = MutableLiveData()

    public constructor(){}

    fun getGroup(): LiveData<Pair<String, Group?>> {
        return joinedGroup
    }

    fun joinGroup(groupCode: String) {
        var params: MutableMap<String, String> = mutableMapOf()
        params.put("groupCode", groupCode)

        FirebaseFunctions.getInstance().getHttpsCallable("joinGroup").call(params)
                .continueWith({ task ->
                    if (task.isSuccessful) {
                        val joinGroupResponse: HashMap<String, Object> = task.result.data as HashMap<String, Object>
                        if (joinGroupResponse["error"] != null) {
                            joinedGroup.value = Pair(joinGroupResponse["error"].toString(), null)
                        } else {
                            joinedGroup.value = Pair("", null)
                        }
                    } else {
                        joinedGroup.value = Pair("Error getting groups! " + task.exception?.stackTrace.toString(), null)
                    }
                })
    }
}