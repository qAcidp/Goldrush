package net.qacidp.goldrush.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaydirtConfig {
    private static final Map<String, PaydirtProperties> PAYDIRT_TYPES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // Format: registerPaydirt(registryName, minGold, maxGold)
        registerPaydirt("pay_dirt_low", 0.0f, 0.05f);
        registerPaydirt("pay_dirt_common", 0.05f, 0.1f);
    }

    public static void registerPaydirt(String registryName, float minGold, float maxGold) {
        PAYDIRT_TYPES.put(registryName, new PaydirtProperties(minGold, maxGold));
    }

    public static float generateGoldAmount(String paydirtType) {
        PaydirtProperties props = PAYDIRT_TYPES.get(paydirtType);
        if (props == null) {
            return 0;
        }
        return props.minGold + (props.maxGold - props.minGold) * RANDOM.nextFloat();
    }

    public static float[] getGoldDistribution(String paydirtType, float totalGold) {
        Random random = new Random();
        float[] layers = new float[8];

        // Zufällige Gewichte erzeugen
        float[] weights = new float[8];
        float weightSum = 0f;

        for (int i = 0; i < 8; i++) {
            weights[i] = random.nextFloat(); // 0.0 - 1.0
            weightSum += weights[i];
        }

        // Gold proportional zu den Gewichten verteilen
        for (int i = 0; i < 8; i++) {
            layers[i] = (weights[i] / weightSum) * totalGold;
        }

        return layers;
    }


    public static PaydirtProperties getProperties(String paydirtType) {
        return PAYDIRT_TYPES.get(paydirtType);
    }

    public static class PaydirtProperties {
        public final float minGold;
        public final float maxGold;

        public PaydirtProperties(float minGold, float maxGold) {
            this.minGold = minGold;
            this.maxGold = maxGold;
        }
    }
}