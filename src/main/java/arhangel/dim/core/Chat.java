package arhangel.dim.core;

import arhangel.dim.core.store.SUserStore;

import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private List<Long> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public List<User> getUsers(SUserStore userStore) {
//        List<Long> userIds = userStore.getUsersByChat(id);
//        users.clear();
//        for (Long userId : userIds) {
//            users.add(userStore.getUserById(userId));
//        }
//        return users;
//    }


    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

}
