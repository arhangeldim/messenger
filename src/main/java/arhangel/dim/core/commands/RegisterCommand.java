package arhangel.dim.core.commands;

import arhangel.dim.core.authorization.Authorize;
import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;

public class RegisterCommand implements Command {
    /**
     * args 0 - название команды, 1 - логин, 2 - пароль
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        AnswerMessage.Value success;
        if (args.length == 3) {
            Authorize service = session.getAuthorize();
            service.registerUser(args[1], args[2], session);
        } else {
            DataOutputStream writer = session.getWriter();
            success = AnswerMessage.Value.NUM_ARGS;
            writer.write(protocol.encode(new AnswerMessage("", success)));
        }
    }

    public String toString() {
        return "/register";
    }
}
