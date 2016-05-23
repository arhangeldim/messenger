package arhangel.dim.client;

import arhangel.dim.core.User;

public class ClientUser extends User {
    private boolean isLoginned;

    public ClientUser() {
        super();
        isLoginned = false;
    }

    public boolean isLoginned() {
        return isLoginned;
    }

    void login(Long id, String name) {
        isLoginned = true;
        this.setId(id);
        this.setName(name);
    }
}
