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

import javax.annotation.Nullable;

public class UpdateCapacityService {
    public static void updateCapacities(LifecycleOwner owner, HashMap<String, Integer> map, List<String> cannotUpdate, List<String> csvBuildingNames, MutableLiveData<Boolean> updateMLD){

        MutableLiveData<List<Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<List<Building>> buildingsObserver = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildingList){
                Log.d("List Returned",buildingList.toString());
                for(int i =0;i<buildingList.size();i++){
                    if(buildingList.get(i).getOccupancy()>map.get(buildingList.get(i).getName())){// if the new capacity is less than occupancy cannot update
                        cannotUpdate.add(buildingList.get(i).getName());
                        map.remove(buildingList.get(i).getName());
                    }
                }

                Log.d("Can't Update",cannotUpdate.toString());
                Log.d("Can Update",map.toString());
                FbUpdate.updateCapacities(map,updateMLD);
            }
        };
        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getBuildings(csvBuildingNames,buildingsMLD);
    }
}
