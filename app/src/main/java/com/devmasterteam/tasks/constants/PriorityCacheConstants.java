package com.devmasterteam.tasks.constants;

import com.devmasterteam.tasks.entities.PriorityEntity;

import java.util.HashMap;
import java.util.List;

public class PriorityCacheConstants {

    private static HashMap<Integer, String> mPriorityCache = new HashMap<>();

    // Não permite instância
    private PriorityCacheConstants() {
    }

    public static void setCache(List<PriorityEntity> list) {
        for (PriorityEntity item : list) {
            mPriorityCache.put(item.Id, item.Description);
        }
    }

    public static String getPriorityDescription(int id) {
        return mPriorityCache.get(id);
    }

}
