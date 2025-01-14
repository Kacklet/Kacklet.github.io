package lol.magmaclient.commands.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.commands.Command;
import lol.magmaclient.managers.CommandManager;

public class KoreCommand extends Command {
    public KoreCommand()
    {
        super("kore");
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length > 2)
        {
            Magma.sendMessageWithPrefix(".kore <help/check/disconnect>");
            return;
        }

        if(args.length > 1 && args[1].equals("help")) {
            CommandManager.printHelp();

        } else if(args.length > 1 && args[1].equals("check")) {
            if(Magma.updateManager.checkUpdate()) {
                Magma.sendMessageWithPrefix("(&cUpdater&f) You are not running the latest version");
            } else {
                Magma.sendMessageWithPrefix("(&cUpdater&f) You are running the latest version");
            }

        } else if(args.length > 1 && args[1].equals("disconnect")) {
            if(Magma.licenseManager.disconnect()) {
                Magma.sendMessageWithPrefix("(&cLicense&f) You successfully disconnected from Kore");
            } else {
                Magma.sendMessageWithPrefix("(&cLicense&f) You are in the unlicensed version.");
            }

        } else {
            Magma.sendMessageWithPrefix("(&cDiscord&f) -> https://discord.com/invite/H4x6eFp9KR");
        }
    }

    @Override
    public String getDescription() {
        return ".kore <help/update/disconnect>";
    }
}