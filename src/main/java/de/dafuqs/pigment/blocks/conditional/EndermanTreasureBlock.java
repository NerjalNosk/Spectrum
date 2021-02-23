package de.dafuqs.pigment.blocks.conditional;

import de.dafuqs.pigment.PigmentBlocks;
import de.dafuqs.pigment.PigmentCommon;
import de.dafuqs.pigment.interfaces.Cloakable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.util.List;

public class EndermanTreasureBlock extends Block implements Cloakable {

    private boolean wasLastCloaked;

    public EndermanTreasureBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isCloaked(PlayerEntity playerEntity, BlockState blockState) {
        return playerEntity.getArmor() < 1;
    }

    @Override
    public boolean wasLastCloaked() {
        return wasLastCloaked;
    }

    @Override
    public void setLastCloaked(boolean lastCloaked) {
        wasLastCloaked = lastCloaked;
    }

    @Deprecated
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        checkCloak(state);
        return super.isSideInvisible(state, stateFrom, direction);
    }

    public void setCloaked() {
        // Colored Logs => Oak logs
        PigmentCommon.getBlockCloaker().swapModel(this.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState()); // block
        //PigmentCommon.getBlockCloaker().swapModel(this.asItem(), Items.OAK_LOG); // item is always visible
    }

    public void setUncloaked() {
        PigmentCommon.getBlockCloaker().unswapAllBlockStates(this);
        //PigmentCommon.getBlockCloaker().unswapModel(this.asItem());
    }

    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return getCloakedDroppedStacks(state, builder);
    }

}
