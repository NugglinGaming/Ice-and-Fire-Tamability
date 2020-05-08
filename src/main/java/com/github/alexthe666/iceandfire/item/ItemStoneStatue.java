package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import com.github.alexthe666.iceandfire.message.MessageStoneStatue;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemStoneStatue extends Item {

    public ItemStoneStatue() {
        this.maxStackSize = 1;
        this.setTranslationKey("iceandfire.stone_statue");
        this.setRegistryName(IceAndFire.MODID, "stone_statue");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.getTagCompound() != null) {
            boolean isPlayer = stack.getTagCompound().getBoolean("IAFStoneStatuePlayerEntity");
            int id = stack.getTagCompound().getInteger("IAFStoneStatueEntityID");
            if (EntityList.getKey(EntityList.getClassFromID(id)) != null) {
                String untranslated = isPlayer ? "entity.player.name" : "entity." + net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(EntityList.getClassFromID(id)).getName() + ".name";
                tooltip.add(new TranslationTextComponent(untranslated));
            }
        }
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTagCompound(new CompoundNBT());
        itemStack.getTagCompound().setBoolean("IAFStoneStatuePlayerEntity", true);
        itemStack.getTagCompound().setInteger("IAFStoneStatueEntityID", 90);
    }

    @Override
    public EnumActionResult onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (side != Direction.UP) {
            return EnumActionResult.FAIL;
        } else {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getTagCompound() != null) {

                if (stack.getTagCompound().getBoolean("IAFStoneStatuePlayerEntity")) {
                    EntityStoneStatue statue = new EntityStoneStatue(worldIn);
                    statue.setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, player.rotationYaw, 0);
                    statue.smallArms = true;
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntity(statue);
                    }
                    statue.readEntityFromNBT(stack.getTagCompound());
                    statue.setCrackAmount(0);
                    float yaw = MathHelper.wrapDegrees(player.rotationYaw + 180F);
                    statue.prevRotationYaw = yaw;
                    statue.rotationYaw = yaw;
                    statue.rotationYawHead = yaw;
                    statue.renderYawOffset = yaw;
                    statue.prevRenderYawOffset = yaw;
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    return EnumActionResult.SUCCESS;
                } else {
                    EntityEntry entry = net.minecraftforge.registries.GameData.getEntityRegistry().get((stack.getTagCompound().getInteger("IAFStoneStatueEntityID")));
                    Class classFromEntity = entry.getEntityClass();
                    Entity entity = null;
                    if (classFromEntity == null) {
                        return EnumActionResult.SUCCESS;
                    }
                    if (Entity.class.isAssignableFrom(classFromEntity)) {
                        try {
                            entity = (Entity) classFromEntity.getDeclaredConstructor(World.class).newInstance(worldIn);
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                            return EnumActionResult.SUCCESS;
                        }
                    }
                    if (entity != null && entity instanceof LivingEntity) {
                        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, player.rotationYaw, 0);
                        if (!worldIn.isRemote) {
                            worldIn.spawnEntity(entity);
                        }
                        StoneEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
                        properties.isStone = true;
                        IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageStoneStatue(entity.getEntityId(), true));
                        ((LivingEntity) entity).readEntityFromNBT(stack.getTagCompound());
                        ((LivingEntity) entity).setNoAI(true);
                        float yaw = MathHelper.wrapDegrees(player.rotationYaw + 180F);
                        entity.prevRotationYaw = yaw;
                        entity.rotationYaw = yaw;
                        ((LivingEntity) entity).rotationYawHead = yaw;
                        ((LivingEntity) entity).renderYawOffset = yaw;
                        ((LivingEntity) entity).prevRenderYawOffset = yaw;
                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                        }
                    }

                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
