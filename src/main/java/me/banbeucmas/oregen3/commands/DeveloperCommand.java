package me.banbeucmas.oregen3.commands;

import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.options.CreateRandomOraxen;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeveloperCommand extends AbstractCommand {
    DeveloperCommand(final CommandSender sender, final String[] args) {
        super("oregen3.developer", sender, null, args);
    }

    @Override
    protected ExecutionResult run() {
        final CommandSender sender = getSender();

        if (!sender.hasPermission(getPermission())) {
            return ExecutionResult.NO_PERMISSION;
        }

        final Player p = getPlayer();
        final String[] args = getArgs();
        final int length = args.length;

        if (length == 1) {
            if (!(sender instanceof Player)) {
                return ExecutionResult.NON_PLAYER;
            }
            PlayerUI.openUI(p, new CreateRandomOraxen(p, 0));
        } else if (length > 1) {
            return ExecutionResult.SUCCESS;
        }

        return ExecutionResult.SUCCESS;
    }
}
