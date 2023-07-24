package fr.vivicoubar.thirstplugin.command;

import fr.vivicoubar.thirstplugin.ThirstPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThirstCommand implements CommandExecutor {

  private final ThirstPlugin main;

  public ThirstCommand(ThirstPlugin thirstPlugin) {
    this.main = thirstPlugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if(commandSender instanceof Player) {
      Player player = (Player) commandSender;
      main.getAllowThirsts().put(player.getName(), !main.getAllowThirsts().get(player.getName()));
      player.sendMessage("§b[ThirstPlugin] Thirst changes now set to : §d" + main.getAllowThirsts().get(player.getName()).toString());
      return true;
    }
    return false;
  }
}
