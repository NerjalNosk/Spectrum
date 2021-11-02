package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.networking.SpectrumS2CPackets;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.sound.SpectrumSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public class LightStaffItem extends Item {

	public static int USE_DURATION = 12;
	public static int MAX_REACH_STEPS = 8;
	public static int MIN_LIGHT_LEVEL = 10;

	public LightStaffItem(Settings settings) {
		super(settings);
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.LIGHT_STAFF_CHARGING, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		// trigger the items' usage action every x ticks
		if(world instanceof ServerWorld && user.getItemUseTime() > USE_DURATION && user.getItemUseTime() % USE_DURATION == 0) {
			usage(world, user);
		}
	}

	public void usage(World world, LivingEntity user) {
		int useTimes = (user.getItemUseTime() / USE_DURATION);
		int maxCheckDistance = Math.min(MAX_REACH_STEPS, useTimes);

		BlockPos sourcePos = user.getBlockPos();
		Vec3d cameraVec = user.getRotationVec(MinecraftClient.getInstance().getTickDelta());

		for(int iteration = 0; iteration < maxCheckDistance; iteration++) {
			BlockPos targetPos = sourcePos.add(cameraVec.x * (double) iteration * 4, cameraVec.y * (double) iteration * 4, cameraVec.z * (double) iteration * 4);
			if(world.getLightLevel(LightType.BLOCK, targetPos) < MIN_LIGHT_LEVEL) {
				if(iteration > 0) {
					targetPos = targetPos.add(iteration - world.getRandom().nextInt(2 * iteration), iteration - world.getRandom().nextInt(2 * iteration), iteration - world.getRandom().nextInt(2 * iteration));
				}
				BlockState targetBlockState = world.getBlockState(targetPos);

				if (targetBlockState.isAir()) {
					world.setBlockState(targetPos, SpectrumBlocks.WAND_LIGHT_BLOCK.getDefaultState(), 3);
					playSoundAndParticles(world, targetPos, user, useTimes, iteration);
					break;
				} else if(targetBlockState.isOf(Blocks.WATER)) {
					world.setBlockState(targetPos, SpectrumBlocks.WAND_LIGHT_BLOCK.getDefaultState().with(WATERLOGGED, true), 3);
					playSoundAndParticles(world, targetPos, user, useTimes, iteration);
					break;
				}
			}
		}
	}

	private void playSoundAndParticles(World world, BlockPos targetPos, LivingEntity user, int useTimes, int iteration) {
		float pitch;
		if (useTimes % 2 == 0) { // high ding <=> deep ding
			pitch = Math.min(1.35F, 0.7F + 0.1F * useTimes);
		} else {
			pitch = Math.min(1.5F, 0.7F + 0.1F * useTimes);
		}
		SpectrumS2CPackets.sendLightCreatedParticle(world, targetPos);
		if(user instanceof PlayerEntity playerUser) {
			world.playSound(playerUser, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, SpectrumSoundEvents.LIGHT_STAFF_PLACE, SoundCategory.PLAYERS, 1.0F, pitch);
			world.playSound(null, playerUser.getX() + 0.5, playerUser.getY() + 0.5, playerUser.getZ() + 0.5, SpectrumSoundEvents.LIGHT_STAFF_PLACE, SoundCategory.PLAYERS, (float) Math.max(0.25, 1.0F-(float)iteration*0.1F), pitch);
		} else {
			world.playSound(null, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, SpectrumSoundEvents.LIGHT_STAFF_PLACE, SoundCategory.PLAYERS, 1.0F, pitch);
		}
	}

}
