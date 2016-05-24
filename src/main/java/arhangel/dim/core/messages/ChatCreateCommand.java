package arhangel.dim.core.messages;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatCreateCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(ChatCreateCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;

            if (chatCreateMessage.getUsers().size() < 1) {
                log.info("User {} requested to create a chat with less than 2 users",
                        session.getUser().getLogin());
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText("You can not create a chat with less than 2 users");
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to chat create command", e);
                    throw new CommandException("Couldn't reply to chat create command");
                }
                return;
            }
            //At least two users
            if (chatCreateMessage.getUsers().contains(session.getUser().getLogin())) {
                log.info("User {} requested to create a chat with himself",
                        session.getUser().getLogin());
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText("Don't include your own login when trying to create chat");
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to chat create command", e);
                    throw new CommandException("Couldn't reply to chat create command");
                }
                return;
            }
            //At least two users, on of which is the one who requested to create a chat
            if (chatCreateMessage.getUsers().size() == 1) {
                Set<String> requestedChatUsers = chatCreateMessage.getUsers();
                String otherUserLogin = (String) requestedChatUsers.toArray()[0];

                MessageStore messageStore = session.getServer().getMessageStore();
                UserStore userStore = session.getServer().getUserStore();

                User otherUser = userStore.getUserByLogin(otherUserLogin);

                if (otherUser == null) {
                    log.info("User {} requested dialogue with non-existent user", session.getUser().getLogin());
                    StatusMessage response = new StatusMessage();
                    response.setType(Type.MSG_STATUS);
                    response.setSenderId(null);
                    response.setText(String.format("User %s not found", otherUserLogin));
                    try {
                        session.send(response);
                    } catch (Exception e) {
                        log.error("Couldn't reply to chat create command", e);
                        throw new CommandException("Couldn't reply to chat create command");
                    }
                    return;
                }

                Set<Long> chatsWithRequester = messageStore.getChatsByUserId(session.getUser().getId());
                for (Long chatId : chatsWithRequester) {
                    Chat chat = messageStore.getChatById(chatId);
                    if ((chat.getUserIds().size() == 2) && (chat.getUserIds().contains(otherUser.getId()))) {
                        //Found requested dialogue
                        log.info("User {} requested to create dialogue with {}, returning existing one {}",
                                session.getUser().getLogin(),
                                otherUserLogin,
                                chat.getId());
                        StatusMessage response = new StatusMessage();
                        response.setType(Type.MSG_STATUS);
                        response.setSenderId(null);
                        response.setText(String.format("Dialogue already exists with id %d", chat.getId()));
                        try {
                            session.send(response);
                        } catch (Exception e) {
                            log.error("Couldn't reply to chat create command", e);
                            throw new CommandException("Couldn't reply to chat create command");
                        }
                        return;
                    }
                }
                //Didn't find the dialogue, have to create
                Chat chat = new Chat();
                Set<Long> users = new HashSet<>();
                users.add(session.getUser().getId());
                users.add(otherUser.getId());
                chat.setUserIds(users);
                chat = messageStore.addChat(chat);
                log.info("User {} requested to create dialogue with {}, created new one {}",
                        session.getUser().getLogin(),
                        otherUserLogin,
                        chat.getId());
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText(String.format("Created dialogue with id %d", chat.getId()));
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to chat create command", e);
                    throw new CommandException("Couldn't reply to chat create command");
                }
                return;
            }
            //Requested chat with >2 users, one of which is the requester
            UserStore userStore = session.getServer().getUserStore();
            Chat chat = new Chat();
            Set<Long> users;
            try {
                users = chatCreateMessage
                        .getUsers()
                        .stream()
                        .map(login -> userStore.getUserByLogin(login).getId())
                        .collect(Collectors.toSet());
            } catch (NullPointerException e) {
                log.error("User for chat creation not found", e);
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText("One of the users not found");
                try {
                    session.send(response);
                } catch (Exception exc) {
                    log.error("Couldn't reply to chat create command", exc);
                    throw new CommandException("Couldn't reply to chat create command");
                }
                return;
            }
            users.add(session.getUser().getId());
            chat.setUserIds(users);
            MessageStore messageStore = session.getServer().getMessageStore();
            chat = messageStore.addChat(chat);
            log.info("User {} requested to create chat with {}, created new one {}",
                    session.getUser().getLogin(),
                    chatCreateMessage.getUsers(),
                    chat.getId());
            StatusMessage response = new StatusMessage();
            response.setType(Type.MSG_STATUS);
            response.setSenderId(null);
            response.setText(String.format("Created chat with id %d", chat.getId()));
            try {
                session.send(response);
            } catch (Exception e) {
                log.error("Couldn't reply to chat create command", e);
                throw new CommandException("Couldn't reply to chat create command");
            }
            return;
        }
        log.info("User requested chat create command without authenticating first");
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        response.setSenderId(null);
        response.setText("You have to log in first");
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to chat create command", e);
            throw new CommandException("Couldn't reply to chat create command");
        }
    }
}
