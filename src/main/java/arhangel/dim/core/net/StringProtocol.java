package arhangel.dim.core.net;

import arhangel.dim.core.messages.*;
import arhangel.dim.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setSenderId(parseLong(tokens[1]));
                textMsg.setChatId(parseLong(tokens[2]));
                textMsg.setText(tokens[3]);
                textMsg.setType(type);
                return textMsg;

            case MSG_LOGIN:
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setSenderId(parseLong(tokens[1]));
                loginMessage.setLogin(tokens[2]);
                loginMessage.setPassword(tokens[3]);
                loginMessage.setType(type);
                return loginMessage;

            case MSG_CHAT_LIST:
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setType(type);
                chatListMessage.setSenderId(parseLong(tokens[1]));
                return chatListMessage;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setSenderId(parseLong(tokens[1]));
                chatCreateMessage.setUserIds(ParseUtils.stringArrToLongList(tokens[2].split(",")));
                return chatCreateMessage;

            case MSG_STATUS:
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setSenderId(parseLong(tokens[1]));
                statusMessage.setText(tokens[2]);
                statusMessage.setType(type);
                return statusMessage;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                chatListResultMessage.setSenderId(parseLong(tokens[1]));
                if (tokens.length >= 3) {
                    chatListResultMessage.setChatIds(Arrays.asList(tokens[2].split(",")).stream()
                            .map(this::parseLong)
                            .collect(Collectors.toList()));
                }
                return chatListResultMessage;

            case MSG_INFO:
                InfoMessage infoMessage = new InfoMessage();
                infoMessage.setSenderId(parseLong(tokens[1]));
                infoMessage.setTarget(parseLong(tokens[2]));
                infoMessage.setType(type);
                return infoMessage;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = new InfoResultMessage();
                infoResultMessage.setType(type);
                infoResultMessage.setSenderId(parseLong(tokens[1]));
                infoResultMessage.setName(tokens[2]);
                if (tokens.length > 3 && tokens[3].length() > 0) {
                    infoResultMessage.setChats(Arrays.asList(tokens[3].split(",")).stream()
                            .map(this::parseLong)
                            .collect(Collectors.toList()));
                } else {
                    infoResultMessage.setChats(new ArrayList<>());
                }
                return infoResultMessage;

            default:
                throw new ProtocolException("Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();
        builder.append(type).append(DELIMITER);
        builder.append(msg.getSenderId()).append(DELIMITER);
        switch (type) {
            case MSG_TEXT:
                TextMessage sendMessage = (TextMessage) msg;
                builder.append(String.valueOf(sendMessage.getChatId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getLogin()).append(DELIMITER);
                builder.append(loginMessage.getPassword()).append(DELIMITER);
                break;

            case MSG_CHAT_LIST:
                break;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;
                builder.append(String.join(
                        ",",
                        ParseUtils.longListToStringArr(chatCreateMessage.getUserIds()))).append(DELIMITER);
                break;

            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                builder.append(statusMessage.getText()).append(DELIMITER);
                break;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = (ChatListResultMessage) msg;
                builder.append(String.join(",",
                        chatListResultMessage.getChatIds().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList()))).append(DELIMITER);
                break;

            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                builder.append(infoMessage.getTarget()).append(DELIMITER);
                break;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                builder.append(infoResultMessage.getName()).append(DELIMITER);
                if (infoResultMessage.getChats() != null && infoResultMessage.getChats().size() > 0) {
                    builder.append(String.join(",",
                            infoResultMessage.getChats().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toList())));

                }
                builder.append(DELIMITER);
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
