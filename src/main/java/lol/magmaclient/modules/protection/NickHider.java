package lol.magmaclient.modules.protection;

import lol.magmaclient.Magma;
import lol.magmaclient.settings.StringSetting;
import lol.magmaclient.modules.Module;

public class NickHider extends Module {
    public StringSetting nick;
    public NickHider()
    {
        super("Nick Hider", Category.PROTECTIONS);
        this.nick = new StringSetting("Name", Magma.mc.getSession().getUsername());
        addSettings(nick);
    }

    @Override
    public void assign()
    {
        Magma.nickHider = this;
    }

    @Override
    public String getSuffix()
    {
        return nick.getValue();
    }
}
