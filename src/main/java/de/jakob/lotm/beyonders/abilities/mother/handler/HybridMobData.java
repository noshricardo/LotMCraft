package de.jakob.lotm.beyonders.abilities.mother.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;

public class HybridMobData {
    private final Identifier modelEntityType;
    private final EntityDimensions dimensions;

    public HybridMobData(Identifier modelEntityType, EntityDimensions dimensions) {
        this.modelEntityType = modelEntityType;
        this.dimensions = dimensions;
    }

    public Identifier getModelEntityType() {
        return modelEntityType;
    }

    public EntityDimensions getDimensions() {
        return dimensions;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("ModelEntityType", modelEntityType.toString());
        tag.putFloat("Width", dimensions.width());
        tag.putFloat("Height", dimensions.height());
        tag.putBoolean("Fixed", dimensions.fixed());
        return tag;
    }

    public static HybridMobData load(CompoundTag tag) {
        Identifier modelType = Identifier.parse(tag.getString("ModelEntityType"));
        float width = tag.getFloat("Width");
        float height = tag.getFloat("Height");
        boolean fixed = tag.getBoolean("Fixed");
        
        EntityDimensions dimensions = fixed ? 
            EntityDimensions.fixed(width, height) : 
            EntityDimensions.scalable(width, height);
            
        return new HybridMobData(modelType, dimensions);
    }
}