package arhangel.dim.core.net;

import arhangel.dim.core.messages.LoginMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;

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
            case MSG_LOGIN:
                LoginMessage loginMessage = new LoginMessage(tokens[2], tokens[3]);
                loginMessage.setSenderId(parseLong(tokens[1]));
                loginMessage.setType(type);
                return loginMessage;
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
            case MSG_TEXT:
                TextMessage sendMessage = (TextMessage) msg;
                builder.append(String.valueOf(sendMessage.getSenderId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(String.valueOf(loginMessage.getSenderId())).append(DELIMITER);
                builder.append(loginMessage.getName()).append(DELIMITER);
                builder.append(loginMessage.getPassword()).append(DELIMITER);
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
