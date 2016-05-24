package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class LoginMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        System.out.println("LOGIN");
        if (session.getUser() != null) {
            TextMessage sendMessage = new StatusMessage();
            sendMessage.setText("already logged in");
            session.send(sendMessage);
        } else {

            try {
                LoginMessage loginMessage = (LoginMessage) msg;
                session.setUser(new User());
                session.getUser().setName(loginMessage.getLogin());
                session.getUser().setPassword(loginMessage.getPassword());
                User realUser = session.getServer().getUserStore()
                        .getUser(loginMessage.getLogin(),
                                loginMessage.getPassword());
                if (realUser == null) {
                    realUser = session.getServer().getUserStore()
                            .addUser(session.getUser());
                }
                session.getServer().getActiveUsers()
                        .put(realUser.getId(), session);
                session.setUser(realUser);
                System.out.println("LOGIN SUCCESS");
            } catch (ClassCastException e) {
                throw new CommandException("Wrong class", e);
            }



        }
    }
}
