package arhangel.dim.core.net;

import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.RegisterMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Простейший протокол передачи данных
 */
public class StringProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

    public static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String str = new String(bytes);
        log.info("decoded: {}", str);
        String[] tokens = str.split(DELIMITER);
        Type type = Type.valueOf(tokens[0]);
        switch (type) {
            case MSG_REGISTER:
                RegisterMessage registerMessage = new RegisterMessage();
                registerMessage.setType(type);
                registerMessage.setSenderId(parseLong(tokens[1]));
                registerMessage.setLogin(tokens[2]);
                registerMessage.setSecret(tokens[3]);
                return registerMessage;
            case MSG_LOGIN:
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setType(type);
                loginMessage.setSenderId(parseLong(tokens[1]));
                loginMessage.setLogin(tokens[2]);
                loginMessage.setSecret(tokens[3]);
                return loginMessage;
            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setSenderId(parseLong(tokens[1]));
                textMsg.setChatId(parseLong(tokens[2]));
                textMsg.setText(tokens[3]);
                textMsg.setType(type);
                return textMsg;
            default:
                throw new ProtocolException("Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();
        builder.append(type).append(DELIMITER);
        switch (type) {
            case MSG_REGISTER:
                RegisterMessage registerMessage = (RegisterMessage) msg;
                builder.append(String.valueOf(registerMessage.getSenderId())).append(DELIMITER);
                builder.append(registerMessage.getLogin()).append(DELIMITER);
                builder.append(registerMessage.getSecret()).append(DELIMITER);
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(String.valueOf(loginMessage.getSenderId())).append(DELIMITER);
                builder.append(loginMessage.getLogin()).append(DELIMITER);
                builder.append(loginMessage.getSecret()).append(DELIMITER);
                break;
            case MSG_TEXT:
                TextMessage sendMessage = (TextMessage) msg;
                builder.append(String.valueOf(sendMessage.getSenderId())).append(DELIMITER);
                builder.append(String.valueOf(sendMessage.getChatId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            default:
                throw new ProtocolException("Invalid type: " + type);


        }
        log.info("encoded: {}", builder.toString());
        return builder.toString().getBytes();
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            // who care
        }
        return null;
    }
}
