package net.qacidp.goldrush.component;

import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.List;

public class GoldDistributionComponent {
    private final float[] goldDistribution;

    public GoldDistributionComponent(float[] goldDistribution) {
        this.goldDistribution = goldDistribution.clone(); // Immutabilität
    }

    public float[] getGoldDistribution() {
        return goldDistribution.clone(); // Kein direkter Zugriff
    }

    public float getTotalGold() {
        float total = 0f;
        for (float gold : goldDistribution) {
            total += gold;
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoldDistributionComponent other)) return false;
        return Arrays.equals(this.goldDistribution, other.goldDistribution);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(goldDistribution);
    }

    public static final Codec<GoldDistributionComponent> CODEC =
            Codec.FLOAT.listOf().xmap(
                    list -> {
                        float[] arr = new float[list.size()];
                        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
                        return new GoldDistributionComponent(arr);
                    },
                    comp -> {
                        Float[] boxed = new Float[comp.goldDistribution.length];
                        for (int i = 0; i < comp.goldDistribution.length; i++) boxed[i] = comp.goldDistribution[i];
                        return Arrays.asList(boxed);
                    }
            );
}