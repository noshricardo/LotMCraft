package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import java.util.ArrayList;
import org.jetbrains.annotations.UnknownNullability;


import java.util.Arrays;
import java.util.List;

public class BeyonderComponent implements ValueIOSerializable {


    private int sequence = 10;
    private String pathway = "none";
    private String[] pathwayHistory = new String[10];
    private int[] characteristicStack = new int[11];
    private float spirituality = 0;
    private float digestionProgress = 0;
    private boolean isGriefingEnabled = true;
    private int cowardWormAmount = 0;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getPathway() {
        return pathway;
    }

    public void setPathway(String pathway) {
        this.pathway = pathway;
    }

    public String[] getPathwayHistory() {
        for(int i = 0; i < sequence; i++) {
            pathwayHistory[i] = null;
        }
        for(int i = sequence; i < 10; i++) {
            if (pathwayHistory[i] == null || pathwayHistory[i].isEmpty()) {
                pathwayHistory[i] = pathway;
            }
        }
        return pathwayHistory;
    }

    public void setPathwayHistory(String[] pathwayHistory) {
        this.pathwayHistory = pathwayHistory;
    }

    public int[] getCharacteristicStack() {
        return characteristicStack;
    }

    public void setCharacteristicStack(int characteristicStack, int sequence) {
        if(sequence <= 9 && sequence > 0)
            this.characteristicStack[sequence] = characteristicStack;
    }

    public void clearCharacteristicStack() {
        this.characteristicStack = new int[11];
    }

    public float getSpirituality() {
        return spirituality;
    }

    public void setSpirituality(float spirituality) {
        this.spirituality = spirituality;
    }

    public float getDigestionProgress() {
        return digestionProgress;
    }

    public void setDigestionProgress(float digestionProgress) {
        this.digestionProgress = digestionProgress;
    }

    public boolean isGriefingEnabled() {
        return isGriefingEnabled;
    }

    public void setGriefingEnabled(boolean griefingEnabled) {
        isGriefingEnabled = griefingEnabled;
    }

    public int getCowardWormAmount() {
        return cowardWormAmount;
    }

    public void setCowardWormAmount(int cowardWormAmount) {
        this.cowardWormAmount = cowardWormAmount;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("sequence", sequence);
        output.putString("pathway", pathway);

        output.putCollection("pathwayHistory", Arrays.asList(pathwayHistory), (pathwayEntry, out) -> {
            out.putString(null, pathwayEntry == null ? "" : pathwayEntry);
        });


        output.putCollection("characteristicStack", () -> {
            List<Integer> list = new ArrayList<>();
            for (int i : characteristicStack) list.add(i);
            return list.iterator();
        }, (val, out) -> out.putInt(null, val));

        output.putFloat("spirituality", spirituality);
        output.putFloat("digestionProgress", digestionProgress);
        output.putBoolean("isGriefingEnabled", isGriefingEnabled);
        output.putInt("cowardWormAmount", cowardWormAmount);
    }


    @Override
    public void deserialize(ValueInput input) {
        this.sequence = input.getIntOr("sequence", 10);
        this.pathway = input.getStringOr("pathway", "none");

        List<String> history = input.readCollection("pathwayHistory", ArrayList::new, in -> {
            String s = in.getStringOr(null, "");
            return s.isEmpty() ? null : s;
        });
        this.pathwayHistory = history.toArray(new String[0]);

        List<Integer> stack = input.readCollection("characteristicStack", ArrayList::new, in -> in.getIntOr(null, 0));
        this.characteristicStack = new int[stack.size()];
        for (int i = 0; i < stack.size(); i++) {
            this.characteristicStack[i] = stack.get(i);
        }

        this.spirituality = input.getFloatOr("spirituality", 0f);
        this.digestionProgress = input.getFloatOr("digestionProgress", 0f);
        this.isGriefingEnabled = input.getBooleanOr("isGriefingEnabled", true);
        this.cowardWormAmount = input.getIntOr("cowardWormAmount", 0);
    }


}
