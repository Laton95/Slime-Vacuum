package com.laton95.slimevacuum;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;

public class VacuumItem extends Item {
	
	public VacuumItem() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS).defaultMaxDamage(100));
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		if(!entity.world.isRemote() && entity instanceof SlimeEntity) {
			int size = ((SlimeEntity) entity).getSlimeSize();
			
			if(size > 1) {
				CompoundNBT nbt = new CompoundNBT();
				entity.writeAdditional(nbt);
				nbt.putInt("Size", size - 2);
				entity.readAdditional(nbt);
			} else {
				entity.remove();
			}
			
			stack.attemptDamageItem(1, player.world.rand, (ServerPlayerEntity) player);
			entity.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * 0.2F + 1.0F);
			
			ItemStack slimeBalls = new ItemStack(Items.SLIME_BALL, size);
			ItemEntity slimeBallsEntity = new ItemEntity(player.world, player.posX, player.posY, player.posZ, slimeBalls);
			player.world.addEntity(slimeBallsEntity);
			
			return true;
		}
		
		return false;
	}
}
