package de.jakob.lotm.beyonders.abilities.visionary.prophecy.actions.context.implementations;

import de.jakob.lotm.beyonders.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.beyonders.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.beyonders.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ActionNumberContext extends ActionContextBase {
    public double doubleValue;
    public int intValue;

    public boolean isDouble;
    public boolean isInt;

    public static String NBT_DOUBLE = "double";
    public static String NBT_INT = "int";
    public static String NBT_DOUBLE_BOOL = "double_bool";
    public static String NBT_INT_BOOL = "int_bool";

    public ActionNumberContext(UUID entityId) {
        super(entityId);

        doubleValue = 0;
        isDouble = false;

        intValue = 0;
        isInt = false;
    }

    @Override
    public ActionContextEnum getType() {
        return ActionContextEnum.NUMBER;
    }

    @Override
    public CompoundTag toNBT(HolderLookup.Provider provider) {
        var tag = super.toNBT(provider);

        tag.putInt(NBT_INT, intValue);
        tag.putDouble(NBT_DOUBLE, doubleValue);

        tag.putBoolean(NBT_DOUBLE_BOOL, isDouble);
        tag.putBoolean(NBT_INT_BOOL, isInt);

        return tag;
    }

    @Override
    public ActionContextBase fillFromStream(TokenStream stream) {
        stream.next();

        if(stream.isEmpty()) return this;

        try{
            intValue = Integer.parseInt(stream.peek());
            isInt = true;
        }catch (NumberFormatException e){
            try {
                doubleValue = Double.parseDouble(stream.peek());
                isDouble = true;
            }
            catch (NumberFormatException ignored){}
        }

        return this;
    }

    public static ActionNumberContext load(CompoundTag tag, UUID id, HolderLookup.Provider provider){
        var context = new ActionNumberContext(id);

        context.doubleValue = tag.getDouble(NBT_DOUBLE);
        context.isDouble = tag.getBooleanOr(NBT_DOUBLE_BOOL, false);

        context.intValue = tag.getIntOr(NBT_INT, 0);
        context.isInt = tag.getBooleanOr(NBT_INT_BOOL, false);

        return context;
    }
}
