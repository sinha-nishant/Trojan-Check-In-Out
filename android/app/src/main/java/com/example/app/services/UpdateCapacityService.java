package com.example.app.services;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class UpdateCapacityService {
    public static void updateCapacities(LifecycleOwner owner, HashMap<String, Integer> map, List<String> cannotUpdate, List<String> csvBuildingNames, MutableLiveData<Boolean> updateMLD){
        MutableLiveData<HashMap<String, Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<HashMap<String, Building>> buildingsObserver = new Observer<HashMap<String, Building>>(){
            @Override
            public void onChanged(@Nullable final HashMap<String, Building> buildingHashMap){
                for(int i =0;i<csvBuildingNames.size();i++){
                    if(buildingHashMap.get(csvBuildingNames.get(i))==null){
                        Log.d("Null on",csvBuildingNames.get(i));
                    }
                    else if(buildingHashMap.get(csvBuildingNames.get(i)).getOccupancy()>map.get(csvBuildingNames.get(i))){
                        cannotUpdate.add(csvBuildingNames.get(i));
                        map.remove(csvBuildingNames.get(i));
                    }
                }
                Log.d("Can't Update",cannotUpdate.toString());
                Log.d("Can Update",map.toString());
                FbUpdate.updateCapacities(map,updateMLD);
            }
        };
        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getAllBuildingsMap(buildingsMLD);
    }
    public static void updateCapacity(String buildingName, Integer newCapacity, MutableLiveData<Boolean> updateMLD){
        FbUpdate.updateCapacity(buildingName,updateMLD,newCapacity);
    }
}
