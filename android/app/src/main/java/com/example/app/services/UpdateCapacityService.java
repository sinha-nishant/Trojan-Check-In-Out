package com.example.app.services;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class UpdateCapacityService {
    public static void updateCapacities(LifecycleOwner owner, HashMap<String, Integer> map, List<String> cannotUpdate, List<String> csvBuildingNames, MutableLiveData<Boolean> updateMLD){
        MutableLiveData<HashMap<String, Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<HashMap<String, Building>> buildingsObserver = new Observer<HashMap<String, Building>>(){
            @Override
            public void onChanged(@Nullable final HashMap<String, Building> buildingHashMap){
                for(int i =0;i<csvBuildingNames.size();i++){
                    if(!buildingHashMap.containsKey(csvBuildingNames.get(i)) || buildingHashMap.get(csvBuildingNames.get(i)).getOccupancy()>map.get(csvBuildingNames.get(i))){
                        cannotUpdate.add(csvBuildingNames.get(i));
                        map.remove(csvBuildingNames.get(i));
                    }
                }
                if(map.size()>0){
                    FbUpdate.updateCapacities(map,updateMLD);
                }else{
                    updateMLD.setValue(false);
                }
            }
        };

        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getAllBuildingsMap(buildingsMLD);
    }
    public static void updateCapacity(String buildingName, Integer newCapacity, MutableLiveData<Boolean> updateMLD){
        FbUpdate.updateCapacity(buildingName,updateMLD,newCapacity);
    }
    public static void addBuildings(LifecycleOwner owner, HashMap<String, Integer> map, List<String> cannotUpdate, List<String> csvBuildingNames, MutableLiveData<Boolean> addMLD){
        // get all buildings map from firebase
        MutableLiveData<HashMap<String, Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<HashMap<String, Building>> buildingsObserver = new Observer<HashMap<String, Building>>(){
            @Override
            public void onChanged(@Nullable final HashMap<String, Building> buildingHashMap){
                //iterate through csvBuildingNames and if exists in database then add it to cannotUpdate and remove from map
                for(int i =0;i<csvBuildingNames.size();i++){
                    if(buildingHashMap.containsKey(csvBuildingNames.get(i))){// if building exists then cannot add it
                        cannotUpdate.add(csvBuildingNames.get(i));
                        map.remove(csvBuildingNames.get(i));
                    }
                }
                FbUpdate.addBuildings(map,addMLD);
            }
        };
        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getAllBuildingsMap(buildingsMLD);

        //once done iterating call batch add
    }
    public static void removeBuildings(LifecycleOwner owner, List<String> cannotUpdate, List<String> csvBuildingNames, MutableLiveData<Boolean> removeMLD) {
        //iterate through csvBuildingnames and add ones that exist to array you will pass onto firebase
        List<String> removeableBuildings = new ArrayList<String>();
        MutableLiveData<HashMap<String, Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<HashMap<String, Building>> buildingsObserver = new Observer<HashMap<String, Building>>(){
            @Override
            public void onChanged(@Nullable final HashMap<String, Building> buildingHashMap){
                //iterate through csvBuildingNames and if building does exist add it to removeableBuildings
                for(int i =0;i<csvBuildingNames.size();i++){
                    if(buildingHashMap.containsKey(csvBuildingNames.get(i)) && buildingHashMap.get(csvBuildingNames.get(i)).getOccupancy()==0 ){// building exists and has no students so can be removed
                        removeableBuildings.add(csvBuildingNames.get(i));
                    }else{//doesn't exist or has students so can't remove
                        cannotUpdate.add(csvBuildingNames.get(i));
                    }
                }
                if(removeableBuildings.size()>0){// some or all building names provided by user were valid
                    /*uncomment once firebase remove batch buildings done*/
                    //FbUpdate.removeBuildings(removeableBuildings,removeMLD);
                }else{// all names provided by user didn't exist in firebase
                    removeMLD.setValue(false);
                }
            }
        };
        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getAllBuildingsMap(buildingsMLD);
    }
}
