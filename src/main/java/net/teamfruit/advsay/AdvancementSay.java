package net.teamfruit.advsay;

import io.chazza.advancementapi.AdvancementAPI;
import io.chazza.advancementapi.FrameType;
import io.chazza.advancementapi.Trigger;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
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
    public AdvancementAPI.AdvancementAPIBuilder create(String title, String description, String material) {
        return AdvancementAPI.builder(new NamespacedKey(this, "story/" + UUID.randomUUID().toString()))
                .frame(FrameType.CHALLENGE)
                .trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test"))
                .icon(material)
                .title(title)
                .description(description)
                .announce(false)
                .toast(true)
                .background("minecraft:textures/blocks/bedrock.png");
    }

    public void send(String title, String description, String material, Player... player) {
        create(title, description, material).build().show(this, player);
    }

    public void sendOneAnnounce(String title, String description, String material, Player[] playerAll, Player[] playerSelected) {
        AdvancementAPI test = create(title, description, material).build();
        test.show(this, playerAll);

        TextComponent titleText = test.getTitle();
        TextComponent chatText = new TextComponent(new ComponentBuilder("[")
                .color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE)
                .append(
                        new ComponentBuilder(titleText)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new ComponentBuilder(titleText)
                                                        .color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE)
                                                        .append("\n")
                                                        .append(test.getDescription())
                                                        .create()
                                        )
                                )
                                .create()
                )
                .append("]")
                .create()
        );

        for (Player selected : playerSelected)
            Bukkit.broadcast(new TranslatableComponent("chat.type.advancement." + test.getFrame(),
                    selected.getDisplayName(), chatText));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("adv".equals(command.getName())) {
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
                    send(title, title, material, player);
                }
            }

            return true;
        } else if ("advall".equals(command.getName())) {
            String selector = args[0];
            String material = args[1];
            String title = ChatColor.translateAlternateColorCodes('&',
                    Stream.of(Arrays.copyOfRange(args, 2, args.length))
                            .collect(Collectors.joining(" ")));

            Collection<? extends Player> playerAll = Bukkit.getOnlinePlayers();
            Collection<? extends Player> playerSelected;
            if (selector.equals("@a")) {
                playerSelected = playerAll;
            } else {
                Player player = Bukkit.getPlayer(selector);
                if (player == null || !player.isOnline()) {
                    sender.sendMessage("The player " + selector + " is not online");
                    return true;
                }
                playerSelected = Arrays.asList(player);
            }

            Player[] playerArraySelected = playerSelected.toArray(new Player[playerSelected.size()]);
            Player[] playerArrayAll = playerAll.toArray(new Player[playerAll.size()]);
            sendOneAnnounce(title, title, material, playerArrayAll, playerArraySelected);

            return true;
        }

        return false;
    }

}
