package lol.magmaclient.commands.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.commands.Command;

public class SetPurseCommand extends Command {
    public SetPurseCommand()
    {
        super("purse", "setpurse","setcoins");
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 2)
        {
            Magma.sendMessageWithPrefix("Invalid command!");
            return;
        }

        double value = Double.parseDouble(args[1]);

        Magma.purseSpoofer.coins.set(value);

        Magma.sendMessageWithPrefix(String.format("Purse spoofed to %,.1f coins", Magma.purseSpoofer.coins.getValue()));

        Magma.configManager.saveConfig();
    }

    @Override
    public String getDescription() {
        return ".setpurse <value>";
    }
}