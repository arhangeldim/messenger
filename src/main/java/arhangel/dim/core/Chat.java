package arhangel.dim.core;

import java.util.ArrayList;
import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private List<Long> users;

    public List<Long> getUsers() {
        return users;
    }

    public Chat(long id) {
        this.id = id;
        users = new ArrayList<>();
    }
}
