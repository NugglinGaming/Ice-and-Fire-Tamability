package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;

public class MyrmexAIMoveToMate extends EntityAIBase {
    private final EntityMyrmexRoyal myrmex;
    private final double movementSpeed;
    private Path path;

    public MyrmexAIMoveToMate(EntityMyrmexRoyal entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.mate != null && this.myrmex.canSeeSky();
    }

    public void updateTask() {
        if (this.myrmex.mate != null && (this.myrmex.getDistance(this.myrmex.mate) > 30 || this.myrmex.getNavigator().noPath())) {
            this.myrmex.getMoveHelper().setMoveTo(this.myrmex.mate.getPosX(), this.myrmex.getPosY(), this.myrmex.mate.getPosZ(), movementSpeed);
        }
    }

    public boolean shouldContinueExecuting() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.mate != null && this.myrmex.mate.isEntityAlive() && (this.myrmex.getDistance(this.myrmex.mate) < 15 || !this.myrmex.getNavigator().noPath());
    }

}