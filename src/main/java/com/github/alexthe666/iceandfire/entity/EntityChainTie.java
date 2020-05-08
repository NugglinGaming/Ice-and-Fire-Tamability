package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntityChainTie extends HangingEntity {

    public EntityChainTie(World worldIn) {
        super(worldIn);
        this.facingDirection = Direction.NORTH;
    }

    public EntityChainTie(World worldIn, BlockPos hangingPositionIn) {
        super(worldIn, hangingPositionIn);
        this.setPosition((double) hangingPositionIn.getX() + 0.5D, (double) hangingPositionIn.getY(), (double) hangingPositionIn.getZ() + 0.5D);
        this.setSize(0.8F, 0.9F);
        this.forceSpawn = true;
    }

    public static EntityChainTie createKnot(World worldIn, BlockPos fence) {
        EntityChainTie entityleashknot = new EntityChainTie(worldIn, fence);
        worldIn.spawnEntity(entityleashknot);
        entityleashknot.playPlaceSound();
        return entityleashknot;
    }

    @Nullable
    public static EntityChainTie getKnotForPosition(World worldIn, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (EntityChainTie entityleashknot : worldIn.getEntitiesWithinAABB(EntityChainTie.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D))) {
            if (entityleashknot != null && entityleashknot.getHangingPosition() != null && entityleashknot.getHangingPosition().equals(pos)) {
                return entityleashknot;
            }
        }

        return null;
    }

    public void setPosition(double x, double y, double z) {
        this.getPosX() = x;
        this.getPosY() = y;
        this.getPosZ() = z;
        if (this.isAddedToWorld() && !this.world.isRemote)
            this.world.updateEntityWithOptionalForce(this, false); // Forge - Process chunk registration after moving.
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - (double) f, y, z - (double) f, x + (double) f, y + (double) f1, z + (double) f));
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            return super.attackEntityFrom(source, amount);
        }
        return false;
    }

    public int getWidthPixels() {
        return 0;
    }

    public int getHeightPixels() {
        return 0;
    }

    public void writeEntityToNBT(CompoundNBT compound) {
        BlockPos blockpos = this.getHangingPosition();
        compound.setInteger("TileX", blockpos.getX());
        compound.setInteger("TileY", blockpos.getY());
        compound.setInteger("TileZ", blockpos.getZ());
    }

    public void readEntityFromNBT(CompoundNBT compound) {
        this.hangingPosition = new BlockPos(compound.getInteger("TileX"), compound.getInteger("TileY"), compound.getInteger("TileZ"));
    }

    public float getEyeHeight() {
        return 0F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 1024.0D;
    }

    public void onBroken(@Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    public void setDead() {
        this.isDead = true;
        double d0 = 30D;
        List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.getPosX() - d0, this.getPosY() - d0, this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + d0, this.getPosZ() + d0));
        for (LivingEntity LivingEntity : list) {
            ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
            if (chainProperties != null && chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity, this)) {
                chainProperties.removeChain(LivingEntity, this);
                EntityItem entityitem = new EntityItem(this.world, this.getPosX(), this.getPosY() + (double) 1, this.getPosZ(), new ItemStack(IafItemRegistry.CHAIN));
                entityitem.setDefaultPickupDelay();
                this.world.spawnEntity(entityitem);
            }
        }
    }

    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (this.world.isRemote) {
            return true;
        } else {
            boolean flag = false;
            double d0 = 30D;
            List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.getPosX() - d0, this.getPosY() - d0, this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + d0, this.getPosZ() + d0));

            for (LivingEntity LivingEntity : list) {
                ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
                if (chainProperties != null && chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity, player)) {
                    chainProperties.addChain(LivingEntity, this);
                    chainProperties.removeChain(LivingEntity, player);
                    flag = true;
                }
            }

            if (!flag) {
                this.setDead();

                if (player.capabilities.isCreativeMode) {
                    for (LivingEntity LivingEntity1 : list) {
                        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity1, ChainEntityProperties.class);
                        if (chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity1, this)) {
                            chainProperties.removeChain(LivingEntity1, this);
                            EntityItem entityitem = new EntityItem(this.world, this.getPosX(), this.getPosY() + (double) 1, this.getPosZ(), new ItemStack(IafItemRegistry.CHAIN));
                            entityitem.setDefaultPickupDelay();
                            this.world.spawnEntity(entityitem);
                        }
                    }
                }
            }

            return true;
        }
    }

    public boolean onValidSurface() {
        return this.world.getBlockState(this.hangingPosition).getBlock() instanceof BlockWall;
    }

    public void playPlaceSound() {
        this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }
}
