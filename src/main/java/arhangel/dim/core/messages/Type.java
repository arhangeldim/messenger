package arhangel.dim.core.messages;

/**
 * Маркер типа сообщения
 */
public enum Type {
//client to server
    MSG_LOGIN, // answer status
    MSG_TEXT, // answer status
    MSG_INFO, // answer MSG_INFO_RESULT
    MSG_CHAT_LIST, // answer MSG_CHAT_LIST_RESULT,
    MSG_CHAT_CREATE, // answer status
    MSG_CHAT_HIST, // answer MSG_CHAT_HIST_RESULT,

// server to client
    MSG_STATUS,
    MSG_CHAT_LIST_RESULT,
    MSG_CHAT_HIST_RESULT,
    MSG_INFO_RESULT
}
