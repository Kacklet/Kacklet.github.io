package lol.magmaclient.managers;

import lol.magmaclient.Magma;
import lol.magmaclient.commands.Command;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager {
    public static final String COMMAND_CLASS_PATH = "lol.magmaclient.commands.impl";
    public static CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<>();
    public static void init()
    {
        Reflections reflections = new Reflections(COMMAND_CLASS_PATH);
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> clazz : commandClasses)
        {
            try {
                Command command = clazz.getDeclaredConstructor().newInstance();
                commands.add(command);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    public static void printHelp()
    {
        for (Command command : commands)
        {
            Magma.sendMessageWithPrefix(Magma.fancy + "b" + command.getName() + Magma.fancy + "7: " + command.getDescription());
        }
    }
    public static boolean handle(String msg) {
        if (!msg.startsWith(".")) return false;

        String[] array = msg.split(" ");
        String baseCommand = array[0].substring(1);

        for (Command command : commands)
        {
            for (String str : command.getNames())
            {
                if (("." + str).equals(array[0])) {
                    try {
                        command.execute(array);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            }
        }

        Magma.sendMessageWithPrefix(baseCommand);

        printHelp();

        return true;
    }
}

