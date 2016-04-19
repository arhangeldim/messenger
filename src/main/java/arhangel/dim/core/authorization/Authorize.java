package arhangel.dim.core.authorization;


import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;
import arhangel.dim.core.store.UserStore;

import java.io.DataOutputStream;
import java.util.List;

/**
 * Класс для авторизации и регистрации пользователей
 */
public class Authorize {

    private UserStore userStore;

    private Authorize() {}

    public Authorize(UserStore userStore) {
        this.userStore = userStore;
    }

    public synchronized void registerUser(String name, String password, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        if (name != null && password != null) {
            List<User> userByName = userStore.getUserByName(name);
            if (userByName.size() > 0) {
                message = "Sorry, but user with this name has already registered";
                success = AnswerMessage.Value.ERROR;
            } else {
                int id = userStore.addUser(new User(name, password, name));
                session.setCurrentUserName(name);
                session.setCurrentUserId(id);
                message = String.format("User was successfully signed up\n" +
                                                "Login: %s, Password: %s, Id: %d", name, password, id);
                success = AnswerMessage.Value.SUCCESS;
            }
        } else {
            message = "Incorrect name/password";
            success = AnswerMessage.Value.ERROR;
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    public synchronized void authorizeUser(String name, String password, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        List<User> userByName = userStore.getUserByName(name);
        if (userByName.size() == 1) {
            User user = userByName.get(0);
            if (user.getPassword().equals(Integer.toString(password.hashCode()))) {
                session.setCurrentUserName(name);
                session.setCurrentUserId(user.getId());
                message = String.format("Hello, %s! Your id = %d", name, user.getId());
                success = AnswerMessage.Value.SUCCESS;
            } else {
                message = "Password is incorrect";
                success = AnswerMessage.Value.ERROR;
            }
        } else {
            message = "Sorry, but we didn't find user with this name: " + name;
            success = AnswerMessage.Value.ERROR;
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    /**
     * Возвращает класс User по идентификатору пользователя
     */
    public User getUserInfo(int id) throws Exception  {
        return userStore.getUser(id);
    }
}
