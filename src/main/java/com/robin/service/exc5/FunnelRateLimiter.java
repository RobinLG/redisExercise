package com.robin.service.exc5;

import java.util.HashMap;
import java.util.Map;

public class FunnelRateLimiter {

    static class Funnel {
        // The capacity of funnel
        int capacity;
        // The rate of flowing water in a funnel
        float leakingRate;
        // Funnel residual space
        int leftQuota;
        // Last leak time
        long leakingTs;

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }

        void makeSpace() {
            long nowTs = System.currentTimeMillis();
            // How long has it been since the last leek
            long deltaTs = nowTs - leakingTs;
            // Vacated space
            int deltaQuota = (int)(deltaTs * leakingRate);
            // The interval is too long, overflow
            if (deltaQuota < 0) {
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }
            // Vacated space is too small
            if (deltaQuota < 1) {
                return;
            }
            // Add residual space
            this.leftQuota += deltaQuota;
            // Record the time of water leakage
            this.leakingTs = nowTs;
            // The residual space shall not be higher than the capacity
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        boolean watering(int quota) {
            makeSpace();
            // Determine if there is enough space left
            if (this.leftQuota >= quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }

        // all of Funnel
        private Map<String, Funnel> funnels = new HashMap<>();

        public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
            String key = String.format("%s:%s", userId, actionKey);
            Funnel funnel = funnels.get(key);
            if (funnel == null) {
                funnel = new Funnel(capacity, leakingRate);
            }
            return funnel.watering(1);
        }
    }


}
