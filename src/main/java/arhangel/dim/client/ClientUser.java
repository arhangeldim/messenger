package arhangel.dim.client;

import arhangel.dim.core.User;

public class ClientUser extends User {
    private boolean isLoginnedFlag;

    ClientUser() {
        super();
        isLoginnedFlag = false;
    }

    public boolean isLoginnedFlag() {
        return isLoginnedFlag;
    }

    void login(Long id, String name) {
        isLoginnedFlag = true;
        this.setId(id);
        this.setName(name);
    }
}
