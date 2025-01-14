package lol.magmaclient.managers;

import lol.magmaclient.Magma;

public class LicenseManager {
    private boolean hasConnected = false;
    private boolean isPremium = false;
    public LicenseManager() {
        if(!Boolean.parseBoolean(Magma.licensed) || Magma.mc.getSession().getPlayerID() != null && this.checkLicense(Magma.mc.getSession().getPlayerID())) {
            isPremium = true;
        }
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean hasConnected() {
        return hasConnected;
    }

    public void setConnected(boolean value) {
        this.hasConnected = value;
    }

    public boolean disconnect() {
        if(Boolean.parseBoolean(Magma.licensed)) {
            this.isPremium = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkLicense(String uuid) {
        return true;
    }

}
