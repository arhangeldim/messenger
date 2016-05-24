package arhangel.dim.core;

import java.util.List;
import java.util.Set;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private Set<Long> userIds;

    public Chat(){}

    public Chat(Long id, Set<Long> userIds) {
        this.id = id;
        this.userIds = userIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }
}
