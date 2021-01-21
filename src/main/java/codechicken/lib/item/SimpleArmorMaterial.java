package codechicken.lib.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

/**
 * Created by covers1624 on 14/12/20.
 */
public class SimpleArmorMaterial implements IArmorMaterial {

    private final int[] durability;
    private final int[] damageReduction;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final LazyValue<Ingredient> repairMaterial;
    private final String textureName;
    private final float toughness;
    private final float knockbackResistance;

    public SimpleArmorMaterial(int[] durability, int[] damageReduction, int enchantability, SoundEvent soundEvent, Supplier<Ingredient> repairMaterial, String textureName, float toughness, float knockbackResistance) {
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.repairMaterial = new LazyValue<>(repairMaterial);
        this.textureName = textureName;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int getDurability(EquipmentSlotType slot) {
        return durability[slot.getIndex()];
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slot) {
        return damageReduction[slot.getIndex()];
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return soundEvent;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return repairMaterial.getValue();
    }

    @Override
    public String getName() {
        return textureName;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public static class Builder {

        private final int[] durability = new int[4];
        private final int[] damageReduction = new int[4];
        private int enchantability;
        private SoundEvent soundEvent;
        private Supplier<Ingredient> repairMaterial;
        private String textureName;
        private float toughness;
        private float knockbackResistance;

        private Builder() {
        }

        public Builder durability(EquipmentSlotType slot, int value) {
            durability[slot.getIndex()] = value;
            return this;
        }

        public Builder durabilityFactor(int factor) {
            for (int i = 0; i < 4; i++) {
                durability[i] = ArmorMaterial.MAX_DAMAGE_ARRAY[i] * factor;
            }
            return this;
        }

        public Builder durability(int[] durability) {
            System.arraycopy(durability, 0, this.durability, 0, 4);
            return this;
        }

        public Builder damageReduction(EquipmentSlotType slot, int value) {
            damageReduction[slot.getIndex()] = value;
            return this;
        }

        public Builder damageReduction(int[] damageReduction) {
            System.arraycopy(damageReduction, 0, this.damageReduction, 0, 4);
            return this;
        }

        public Builder enchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }

        public Builder soundEvent(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public Builder repairMaterial(Supplier<Ingredient> repairMaterial) {
            this.repairMaterial = repairMaterial;
            return this;
        }

        public Builder textureName(String textureName) {
            this.textureName = textureName;
            return this;
        }

        public Builder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public Builder knockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }

        public SimpleArmorMaterial build() {
            return new SimpleArmorMaterial(durability, damageReduction, enchantability, soundEvent, repairMaterial, textureName, toughness, knockbackResistance);
        }
    }
}
