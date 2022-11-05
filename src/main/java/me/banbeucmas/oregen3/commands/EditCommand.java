package me.banbeucmas.oregen3.commands;

import me.banbeucmas.oregen3.gui.EditorGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends AbstractCommand {
    EditCommand(final CommandSender sender, final String[] args) {
        super("oregen3.check", sender, null, args);
    }

    @Override
    protected ExecutionResult run() {
        final CommandSender sender = getSender();

        if (!sender.hasPermission(getPermission())) {
            return ExecutionResult.NO_PERMISSION;
        }

        final Player player = getPlayer();
        final String[] args = getArgs();
        final int length = args.length;

        if (length == 1) {
            if (!(sender instanceof Player)) {
                return ExecutionResult.NON_PLAYER;
            }

            EditorGUI.open(player);
        } else if (length > 1) {
            //TODO: Edit generators using commands
            return ExecutionResult.SUCCESS;
        }

        return ExecutionResult.SUCCESS;
    }
}