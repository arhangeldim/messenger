package arhangel.dim.core.commands;

import arhangel.dim.core.authorization.Authorize;
import arhangel.dim.core.authorization.User;
import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;

public class UserInfoCommand implements Command {
    /**
     * args 0 - название команды, 1 - имя пользователя. Если аргумент 1 отсутствует, то выводятся
     *                                                  данные о текущем пользователе
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        if (args.length == 1) {
            int currentUserId = session.getCurrentUserId();
            if (currentUserId == -1) {
                success = AnswerMessage.Value.NUM_ARGS;
            } else {
                Authorize service = session.getAuthorize();
                User user = service.getUserInfo(currentUserId);
                if (user == null) {
                    message = "Can't find you. Don't worry.";
                    success = AnswerMessage.Value.ERROR;
                } else {
                    message = String.format("Username: %s," +
                                            " nickname: %s, Id: %d", user.getName(), user.getNick(), user.getId());
                    success = AnswerMessage.Value.SUCCESS;
                }
            }
        } else if (args.length == 2) {
            Authorize service = session.getAuthorize();
            User user = service.getUserInfo(Integer.parseInt(args[1]));
            if (user == null) {
                message = "Can't find user: " + args[1];
                success = AnswerMessage.Value.ERROR;
            } else {
                message = String.format("Username: %s," +
                        " nickname: %s, Id: %d", user.getName(), user.getNick(), user.getId());
                success = AnswerMessage.Value.SUCCESS;
            }
        } else {
            success = AnswerMessage.Value.NUM_ARGS;
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    public String toString() {
        return "/info";
    }
}
