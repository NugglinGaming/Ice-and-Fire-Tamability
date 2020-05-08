package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenDreadwoodTree extends WorldGenerator {

    private static final BlockState TRUNK = IafBlockRegistry.DREADWOOD_LOG.getDefaultState();

    public WorldGenDreadwoodTree() {
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i = rand.nextInt(3) + rand.nextInt(3) + 5;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockPos down = position.down();
                BlockState state = worldIn.getBlockState(down);
                boolean isSoil = state.isFullBlock();

                if (isSoil && position.getY() < worldIn.getHeight() - i - 1) {
                    state.getBlock().onPlantGrow(state, worldIn, down, position);
                    Direction Direction = Direction.Plane.HORIZONTAL.random(rand);
                    int k2 = i - rand.nextInt(4) - 1;
                    int l2 = 3 - rand.nextInt(3);
                    int i3 = position.getX();
                    int j1 = position.getZ();
                    int k1 = 0;

                    for (int l1 = 0; l1 < i; ++l1) {
                        int i2 = position.getY() + l1;

                        if (l1 >= k2 && l2 > 0) {
                            i3 += Direction.getXOffset();
                            j1 += Direction.getZOffset();
                            --l2;
                        }

                        BlockPos blockpos = new BlockPos(i3, i2, j1);
                        state = worldIn.getBlockState(blockpos);

                        if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos)) {
                            this.placeLogAt(worldIn, blockpos);
                            k1 = i2;
                        }
                    }

                    BlockPos blockpos2 = new BlockPos(i3, k1, j1);
                    blockpos2 = blockpos2.up();


                    i3 = position.getX();
                    j1 = position.getZ();
                    Direction Direction1 = Direction.Plane.HORIZONTAL.random(rand);

                    if (Direction1 != Direction) {
                        int l3 = k2 - rand.nextInt(2) - 1;
                        int k4 = 1 + rand.nextInt(3);
                        k1 = 0;

                        for (int l4 = l3; l4 < i && k4 > 0; --k4) {
                            if (l4 >= 1) {
                                int j2 = position.getY() + l4;
                                i3 += Direction1.getXOffset();
                                j1 += Direction1.getZOffset();
                                BlockPos blockpos1 = new BlockPos(i3, j2, j1);
                                state = worldIn.getBlockState(blockpos1);

                                if (state.getBlock().isAir(state, worldIn, blockpos1) || state.getBlock().isLeaves(state, worldIn, blockpos1)) {
                                    this.placeLogAt(worldIn, blockpos1);
                                    k1 = j2;
                                }
                            }

                            ++l4;
                        }

                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void placeLogAt(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, TRUNK);
        for(Direction facing : Direction.HORIZONTALS){
            if(worldIn.rand.nextFloat() < 0.1F){
                this.setBlockAndNotifyAdequately(worldIn, pos.offset(facing), TRUNK.with(BlockRotatedPillar.AXIS, facing.getAxis()));
                if(worldIn.rand.nextBoolean()){
                    this.setBlockAndNotifyAdequately(worldIn, pos.offset(facing, 2), TRUNK.with(BlockRotatedPillar.AXIS, facing.getAxis()));
                    this.setBlockAndNotifyAdequately(worldIn, pos.offset(facing, 3).up(), TRUNK.with(BlockRotatedPillar.AXIS, facing.getAxis()));
                }else{
                    this.setBlockAndNotifyAdequately(worldIn, pos.offset(facing).up(), TRUNK.with(BlockRotatedPillar.AXIS, facing.getAxis()));
                }
            }
        }

    }

    public boolean isReplaceable(World world, BlockPos pos) {
        net.minecraft.block.state.BlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos);
    }

}
