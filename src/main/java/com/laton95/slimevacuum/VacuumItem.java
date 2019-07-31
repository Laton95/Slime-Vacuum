package com.laton95.slimevacuum;

import net.minecraft.entity.EntityType;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

public class VacuumItem extends Item {
	
	public VacuumItem() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS).defaultMaxDamage(100));
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		if(!entity.world.isRemote() && entity instanceof SlimeEntity) {
			//Shink slime
			int size = ((SlimeEntity) entity).getSlimeSize();
			
			if(size > 1) {
				CompoundNBT nbt = new CompoundNBT();
				entity.writeAdditional(nbt);
				nbt.putInt("Size", size - 2);
				entity.readAdditional(nbt);
			} else {
				entity.remove();
			}
			
			//Get slimeball item
			Item slimeItem = Items.AIR;
			
			if(entity.getType() == EntityType.SLIME) {
				slimeItem = Items.SLIME_BALL;
			}
			else if(entity.getType() == EntityType.MAGMA_CUBE) {
				slimeItem = Items.MAGMA_CREAM;
			}
			else {
				//In the case of a slime added by a mod, try and get *something* from the loot table
				try {
					LootTable loottable = entity.world.getServer().getLootTableManager().getLootTableFromLocation(entity.getType().getLootTable());
					LootContext.Builder builder = new LootContext.Builder((ServerWorld)entity.world).withRandom(entity.world.rand).withParameter(LootParameters.THIS_ENTITY, entity).withParameter(LootParameters.POSITION, new BlockPos(entity)).withParameter(LootParameters.DAMAGE_SOURCE, new DamageSource("")).withLuck(1000);
					
					int tries = 0;
					while(slimeItem == Items.AIR) {
						if(tries > 10) {
							slimeItem = Items.SLIME_BALL;
							break;
						}
						
						slimeItem = loottable.generate(builder.build(LootParameterSets.ENTITY)).get(0).getItem();
						
						tries++;
					}
				}
				catch(Exception e) {
					slimeItem = Items.SLIME_BALL;
					SlimeVacuum.LOGGER.error("Error encountered sucking modded slime, please report to https://github.com/Laton95/Slime-Vacuum/issues");
				}
			}
			
			//Do misc effects
			stack.attemptDamageItem(1, player.world.rand, (ServerPlayerEntity) player);
			entity.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * 0.2F + 1.0F);
			
			//Give slimeball
			ItemStack slimeBalls = new ItemStack(slimeItem, size);
			ItemEntity slimeBallsEntity = new ItemEntity(player.world, player.posX, player.posY, player.posZ, slimeBalls);
			player.world.addEntity(slimeBallsEntity);
			
			return true;
		}
		
		return false;
	}
}
