package com.xing.bluetoothweighapp.gunutils;

import android.content.Context;
import android.os.Build;

import com.xing.bluetoothweighapp.Application;
import com.xing.bluetoothweighapp.gunutils.strategy.IGun;
import com.xing.bluetoothweighapp.gunutils.strategy.ZLGun;
import com.xing.bluetoothweighapp.gunutils.strategy.ZLHighLevelGun;

import java.util.ArrayList;
import java.util.List;

public class GunManager {
    private List<IGun> guns = new ArrayList<>();

    private static volatile GunManager gunManager;

    public GunManager(Context context) {
        guns.add(new ZLGun(context));
        guns.add(new ZLHighLevelGun(context));
    }

    public static GunManager getInstance() {
        synchronized (GunManager.class) {
            if (gunManager == null) {
                synchronized (GunManager.class) {
                    if (gunManager == null) {
                        gunManager = new GunManager(Application.Companion.getApplication());
                    }
                }
            }
        }
        return gunManager;
    }


    public IGun getFilterGun() {
        for (IGun gun:guns){
            if(gun.getType().ordinal()==CompileType.PHONE_NAME.ordinal()){
                List<String> filterList = gun.getFilterList();
                if(filterList!=null && !filterList.isEmpty()){
                    for (String s:filterList){
                        if(Build.MODEL.equalsIgnoreCase(s)){
                            return gun;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isFilterVersion(){
        return getFilterGun()!=null;
    }


}
