package arhangel.dim.core.messages;

import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class LoginCommand implements Command {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() != null) {
            TextMessage answerMessage = new TextMessage();
            answerMessage.setType(Type.MSG_STATUS);
            answerMessage.setText("already logged in");
            try {
                session.send(answerMessage);
            } catch (ProtocolException | IOException e) {
                e.printStackTrace();
            }
        }
        else {

        }
    }
}
