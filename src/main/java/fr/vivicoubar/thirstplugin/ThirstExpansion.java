package fr.vivicoubar.thirstplugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThirstExpansion extends PlaceholderExpansion {

  private ThirstPlugin main;
  public ThirstExpansion(ThirstPlugin thirstPlugin) {
    this.main = thirstPlugin;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "thirst";
  }

  @Override
  public @NotNull String getAuthor() {
    return "arcialys";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onPlaceholderRequest(Player player, String params) {
    if(params.equalsIgnoreCase("value")) {
      if(player == null) {
        return "";
      } else {
        String message = ChatColor.BLUE.toString() + ChatColor.BOLD;
        int val = 1;
        if(main.getThirsts().get(player.getName()) > 500) {
          for(int i = 1; i <= Math.min((main.getThirsts().get(player.getName())+500) / 1000,main.getYamlConfig().getInt("maxThirst")) ; i++) {
            val++;
            message += "🥛";
          }
        }
        message += ChatColor.GRAY;

        for(int i=val; i <= main.getYamlConfig().getDouble("maxThirst"); i++) {
          message += "🥛";
        }
        return message;
      }
    }
    return null;
  }
}
