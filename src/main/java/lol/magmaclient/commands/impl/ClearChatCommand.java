package lol.magmaclient.commands.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.commands.Command;

public class ClearChatCommand extends Command {
    public ClearChatCommand()
    {
        super("clear");
    }
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 2) {
            Magma.sendMessageWithPrefix("Invalid command!");
            return;
        }

        for(int i = 0; i < Integer.parseInt(args[1]); i++) {
            Magma.sendMessage("");
        }
    }

    @Override
    public String getDescription() {
        return ".clear <lines>";
    }
}