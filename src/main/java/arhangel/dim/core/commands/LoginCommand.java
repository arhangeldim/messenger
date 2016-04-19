package arhangel.dim.core.commands;

import arhangel.dim.core.authorization.Authorize;
import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;

public class LoginCommand implements Command {
    /**
     * args 0 - название команды, 1 - логин, 2 - пароль
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Authorize service = session.getAuthorize();
        if (args.length == 3) {
            service.authorizeUser(args[1], args[2], session);
        } else {
            Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
            writer.write(protocol.encode(new AnswerMessage("", AnswerMessage.Value.NUM_ARGS)));
        }
    }

    public String toString() {
        return "/login";
    }
}
