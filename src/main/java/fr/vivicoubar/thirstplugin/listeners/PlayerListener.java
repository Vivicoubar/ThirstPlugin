package fr.vivicoubar.thirstplugin.listeners;

import fr.vivicoubar.thirstplugin.ThirstPlugin;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

  private ThirstPlugin main;



  public PlayerListener(ThirstPlugin thirstPlugin) {
    this.main = thirstPlugin;
  }


  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
    Player player = event.getPlayer();
    YamlConfiguration statsConfig = main.getStatsConfig();
    double thirst = main.getYamlConfig().getDouble("defaultThirst");
    if(!main.getThirsts().containsKey(player.getName())) {
      if (statsConfig.get("players." + player.getName() + ".thirst") != null) {
        thirst = (double) statsConfig.get("players." + player.getName() + ".thirst");
        main.getThirsts().put(player.getName(), thirst);
      } else {
        main.getThirsts().put(player.getName(), thirst);
      }
    }
    if(!main.getAllowThirsts().containsKey(player.getName())) {
      if (statsConfig.get("players." + player.getName() + ".allowThirst") != null) {
        main.getAllowThirsts().put(player.getName(),statsConfig.getBoolean("players." + player.getName() + ".allowThirst"));
      } else {
        main.getAllowThirsts().put(player.getName(), false);
      }
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) throws IOException {
    if(main.getAllowThirsts().get(event.getPlayer().getName())) {
      Location fromLoc = event.getFrom();
      Location toLoc = event.getTo();
      if(toLoc == null) {
        return;
      }
      if(fromLoc.getX() != toLoc.getX() || fromLoc.getY() != toLoc.getY() || fromLoc.getZ() != toLoc.getZ()) {
        changePlayerThirst(event.getPlayer());
      }
      if(fromLoc.getBlock().getType() == Material.WATER) {
        addPlayerThirst(event.getPlayer(), main.getYamlConfig().getDouble("bonus.water"));
      }
      Player player = event.getPlayer();
      if(main.getThirsts().get(player.getName()) < 3500) {
        player.setVelocity(player.getVelocity().multiply(new Vector(0.3,1,0.3)));
      }
      if (main.getThirsts().get(player.getName()) < 5500) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 255, 1));
      } else {
        player.removePotionEffect(PotionEffectType.CONFUSION);
      }
    }
  }
  @EventHandler
  public void onPlayerDrink(PlayerItemConsumeEvent event) {
    if (main.getAllowThirsts().get(event.getPlayer().getName())) {
      if (event.getItem().getType() == Material.POTION) {
        addPlayerThirst(event.getPlayer(), main.getYamlConfig().getDouble("bonus.potion"));
      } else if (event.getItem().getType() == Material.MELON_SLICE) {
        addPlayerThirst(event.getPlayer(), main.getYamlConfig().getDouble("bonus.melon"));
      } else if (event.getItem().getType() == Material.MILK_BUCKET) {
        addPlayerThirst(event.getPlayer(), main.getYamlConfig().getDouble("bonus.milk"));
      }
    }
  }

  private void addPlayerThirst(Player player, double thirstBonus) {
    double thirst = main.getThirsts().get(player.getName());
    main.getThirsts().put(player.getName(), Math.min(thirst + thirstBonus, main.getYamlConfig().getDouble("maximumThirst")));
  }

  @EventHandler
  public void onPluginDisableEvent(PluginDisableEvent event) throws IOException {
    if(event.getPlugin().equals(main)){
      saveAllThirst();
    }
  }

  private void saveAllThirst() throws IOException {
    YamlConfiguration statsConfig = main.getStatsConfig();
    main.getThirsts().forEach((playerName, value)-> {
      statsConfig.set("players." + playerName+ ".thirst", (value));
    });
    main.getAllowThirsts().forEach((playerName, value) -> {
      statsConfig.set("players." + playerName + ".allowThirst", value);
    });
    statsConfig.save(main.getStatsFile());
  }

  public void changePlayerThirst(Player player) throws IOException {
    double thirst = main.getThirsts().get(player.getName());
    double thirstMalus = 0;
    if(player.isSprinting()) {
      thirstMalus += main.getYamlConfig().getDouble("malus.sprint");
    } else{
      thirstMalus += main.getYamlConfig().getDouble("malus.walking");
    }
    if(player.getWorld().getEnvironment().equals(Environment.NETHER)) {
      thirstMalus *=main.getYamlConfig().getDouble("malus.netherFactor");
    }
    Location loc = player.getLocation();
    if(player.getWorld().getBiome(loc) == Biome.DESERT ||
        player.getWorld().getBiome(loc) == Biome.JUNGLE ||
        player.getWorld().getBiome(loc) == Biome.SAVANNA ||
        player.getWorld().getBiome(loc) == Biome.SAVANNA_PLATEAU ||
        player.getWorld().getBiome(loc) == Biome.WINDSWEPT_SAVANNA ||
        player.getWorld().getBiome(loc) == Biome.BADLANDS ||
        player.getWorld().getBiome(loc) == Biome.WOODED_BADLANDS ||
        player.getWorld().getBiome(loc) == Biome.ERODED_BADLANDS)
    {
      thirstMalus *=main.getYamlConfig().getDouble("malus.warmBiomeFactor");
    }

    if(player.getWorld().getBiome(loc) == Biome.ICE_SPIKES ||
        player.getWorld().getBiome(loc) == Biome.TAIGA ||
        player.getWorld().getBiome(loc) == Biome.SNOWY_BEACH ||
        player.getWorld().getBiome(loc) == Biome.SNOWY_PLAINS ||
        player.getWorld().getBiome(loc) == Biome.SNOWY_SLOPES ||
        player.getWorld().getBiome(loc) == Biome.SNOWY_TAIGA ||
        player.getWorld().getBiome(loc) == Biome.COLD_OCEAN ||
        player.getWorld().getBiome(loc) == Biome.DEEP_COLD_OCEAN)
    {
      thirstMalus *=main.getYamlConfig().getDouble("malus.coldBiomeFactor");
    }

    main.getThirsts().put(player.getName(), Math.max(0,(thirst-(thirstMalus*main.getYamlConfig().getDouble("globalThirstFactor")))));
  }

}
