package me.banbeucmas.oregen3.commands;

import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.gui.editor.options.CreateRandomOraxen;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.banbeucmas.oregen3.Oregen3.getPlugin;

public class EditCommand extends AbstractCommand {
    EditCommand(final CommandSender sender, final String[] args) {
        super("oregen3.edit", sender, null, args);
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
            PlayerUI.openUI(p, new EditorGUI(p));
        } else if (length > 1) {
            if (args[1].startsWith("developer")) {
                PlayerUI.openUI(p, new CreateRandomOraxen(p, 0));
                return ExecutionResult.SUCCESS;
            }
            MenuGenerator ui = new MenuGenerator(p,
                    new ListGenerator(p, new EditorGUI(p), 0),
                    DataManager.getChoosers().containsValue(DataManager.getChoosers().get(args[1]))
                            ? DataManager.getChoosers().get(args[1])
                            : DataManager.getChoosers().get(getPlugin().getConfig().getString("defaultGenerator", "")
                    ));
            PlayerUI.openUI(p, ui);
        }

        return ExecutionResult.SUCCESS;
    }
}