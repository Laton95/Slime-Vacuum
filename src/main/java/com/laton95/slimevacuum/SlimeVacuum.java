package com.laton95.slimevacuum;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laton95.slimevacuum.SlimeVacuum.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlimeVacuum {
	
	public static final String MOD_ID = "slimevacuum";
	
	public static Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	@ObjectHolder(SlimeVacuum.MOD_ID + ":vacuum")
	public static Item VACUUM;
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new VacuumItem().setRegistryName("vacuum"));
	}
}
