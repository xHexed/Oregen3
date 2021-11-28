package me.banbeucmas.oregen3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.banbeucmas.oregen3.Oregen3.getPlugin;

public class CommandHandler implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length == 0) {
            new UsageCommand(sender, label).execute();
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                new ReloadCommand(sender).execute();
                break;
            case "help":
                new HelpCommand(sender, label).execute();
                break;
            case "info":
                new InformationCommand(sender, label, args).execute();
                break;
            case "edit":
                new EditCommand(sender, args).execute();
            case "developer":
                new DeveloperCommand(sender, args).execute();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("oregen3.reload")) list.add("reload");
            if (sender.hasPermission("oregen3.help")) list.add("help");
            if (sender.hasPermission("oregen3.information")) list.add("info");
            if (sender.hasPermission("oregen3.edit")) list.add("edit");
        }

        if (args.length > 1) {
            List<String> generators = new ArrayList<>(getPlugin().getConfig().getConfigurationSection("generators").getKeys(false));
            if (args[0].startsWith("edit") && sender.hasPermission("oregen3.edit"))
                generators.forEach(generator -> list.add(generator.toString()));
        }

        return args[args.length - 1].isEmpty() ? list : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
