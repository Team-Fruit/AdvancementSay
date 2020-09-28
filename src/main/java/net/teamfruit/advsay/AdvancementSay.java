package net.teamfruit.advsay;

import io.chazza.advancementapi.AdvancementAPI;
import io.chazza.advancementapi.FrameType;
import io.chazza.advancementapi.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AdvancementSay extends JavaPlugin {

    public void send(String title, String description, String material, Player... player) {
        AdvancementAPI test = AdvancementAPI.builder(new NamespacedKey(this, "story/" + UUID.randomUUID().toString()))
                .frame(FrameType.CHALLENGE)
                .trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test"))
                .icon(material)
                .title(title)
                .description(description)
                .announce(false)
                .toast(true)
                .background("minecraft:textures/blocks/bedrock.png")
                .build();
        test.show(this, player);
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!"adv".equals(command.getName()))
            return false;

        if (args.length < 3)
            return false;

        String selector = args[0];
        String material = args[1];
        String title = ChatColor.translateAlternateColorCodes('&',
                Stream.of(Arrays.copyOfRange(args, 2, args.length))
                        .collect(Collectors.joining(" ")));

        if (selector.equals("@a")) {
            Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
            Player[] player = playerList.toArray(new Player[playerList.size()]);
            send(title, "505 Title not found", material, player);
        } else {
            Player player = Bukkit.getPlayer(selector);
            if (player == null || !player.isOnline()) {
                sender.sendMessage("The player " + selector + " is not online");
                return true;
            } else {
                send(title, "505 Title not found", material, player);
            }
        }

        return true;
    }

}
