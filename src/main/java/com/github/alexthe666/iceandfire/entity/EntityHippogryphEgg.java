package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHippogryphEgg extends EggEntity {

    private ItemStack itemstack;

    public EntityHippogryphEgg(World world) {
        super(world);
    }

    public EntityHippogryphEgg(World worldIn, double x, double y, double z, ItemStack itemstack) {
        super(worldIn, x, y, z);
        this.itemstack = itemstack;
    }


    public EntityHippogryphEgg(World world, PlayerEntity player, ItemStack itemstack) {
        super(world, player);
        this.itemstack = itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.getPosX(), this.getPosY(), this.getPosZ(), ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(IafItemRegistry.HIPPOGRYPH_EGG));
            }
        }
    }

    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.world.isRemote) {
            EntityHippogryph hippogryph = new EntityHippogryph(this.world);
            hippogryph.setGrowingAge(-24000);
            hippogryph.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
            if (itemstack != null) {
                hippogryph.setVariant(itemstack.getMetadata());
            }
            this.world.spawnEntity(hippogryph);
        }

        this.world.setEntityState(this, (byte) 3);
        this.setDead();
    }
}