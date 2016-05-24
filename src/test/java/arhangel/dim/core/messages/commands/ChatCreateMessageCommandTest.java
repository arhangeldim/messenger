package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.lections.jdbc.QueryExecutor;
import arhangel.dim.server.Server;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.any;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class ChatCreateMessageCommandTest {

    private static Session session;
    private static Server server;
    private static MessageStore messageStore;
    private static UserStore userStore;
    private static ConcurrentHashMap<Long, Session> activeUsers;
    private static User user;

    @Before
    public  void before() {
        user = new User();
        user.setName("kirill");
        user.setPassword("123456");
        user.setId(1L);
        session = mock(Session.class);
        server = mock(Server.class);
        messageStore = mock(MessageStore.class);
        activeUsers = mock(ConcurrentHashMap.class);
        userStore = mock(UserStore.class);
        session.getServer();
        when(session.getServer()).thenReturn(server);
        when(server.getUserStore()).thenReturn(userStore);
        when(server.getMessageStore()).thenReturn(messageStore);
        when(server.getActiveUsers()).thenReturn(activeUsers);
    }



    @Test
    public void logoutTestExecute() throws Exception {
        doAnswer(invocation -> {
            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
            assertTrue(msg.getText().equals(StatusMessage
                    .logInFirstMessage().getText()));
            return null;
        }).when(session).send(any(Message.class));
        when(session.getUser()).thenReturn(null);
        CommandByMessage.getCommand(Type.MSG_CHAT_CREATE).execute(session,
                new ChatCreateMessage());
    }

    @Test public void testExecute_valid2() throws Exception {
        when(session.getUser()).thenReturn(user);
        User user2 = new User();
        user2.setName("2");
        user2.setPassword("2");
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        user3.setPassword("3");
        user3.setName("3");
        Chat chat = new Chat(1L);
        chat.getUsers().add(1L);
        chat.getUsers().add(2L);
        chat.getUsers().add(3L);
        Chat chat2 = new Chat(2L);
        chat2.getUsers().add(1L);
        chat2.getUsers().add(2L);
        List<Long> chatsOfUser2 = new ArrayList<>();
        chatsOfUser2.add(1L);

        when(messageStore.getChatsByUserId(2L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatsByUserId(1L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatById(1L)).thenReturn(chat);
        when(messageStore.getChatById(2L)).thenReturn(chat2);
        when(userStore.getUserById(1L)).thenReturn(user);
        when(userStore.getUserById(2L)).thenReturn(user2);
        when(userStore.getUserById(3L)).thenReturn(user3);
        when(messageStore.addChat()).thenReturn(2L);
        when(activeUsers.get(anyLong())).thenReturn(null);

        doNothing().when(messageStore).addUserToChat(anyLong(), anyLong());
        doAnswer(invocation -> {
            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
            assertEquals(msg.getText(), "New chat created, id: 2");
            return null;
        }).when(session).send(any(Message.class));
        ChatCreateMessage message = new ChatCreateMessage();
        message.setSenderId(1L);
        message.setUsers(new ArrayList<>());
        message.getUsers().add(2L);
        CommandByMessage.getCommand(Type.MSG_CHAT_CREATE).execute(session,
                message);
    }

    @Test public void testExecute_exists2() throws Exception {
        when(session.getUser()).thenReturn(user);
        User user2 = new User();
        user2.setName("2");
        user2.setPassword("2");
        user2.setId(2L);
        Chat chat = new Chat(1L);
        chat.getUsers().add(1L);
        chat.getUsers().add(2L);
        Chat chat2 = new Chat(2L);
        chat2.getUsers().add(1L);
        chat2.getUsers().add(2L);
        List<Long> chatsOfUser2 = new ArrayList<>();
        chatsOfUser2.add(1L);

        when(messageStore.getChatsByUserId(2L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatsByUserId(1L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatById(1L)).thenReturn(chat);
        when(messageStore.getChatById(2L)).thenReturn(chat2);
        when(userStore.getUserById(1L)).thenReturn(user);
        when(userStore.getUserById(2L)).thenReturn(user2);
        when(messageStore.addChat()).thenReturn(2L);
        when(activeUsers.get(anyLong())).thenReturn(null);

        doNothing().when(messageStore).addUserToChat(anyLong(), anyLong());
        doAnswer(invocation -> {
            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
            assertEquals(msg.getText(), "Chat existed, id: 1");
            return null;
        }).when(session).send(any(Message.class));
        ChatCreateMessage message = new ChatCreateMessage();
        message.setSenderId(1L);
        message.setUsers(new ArrayList<>());
        message.getUsers().add(2L);
        CommandByMessage.getCommand(Type.MSG_CHAT_CREATE).execute(session,
                message);
    }

    @Test public void testExecute_exists3() throws Exception {
        when(session.getUser()).thenReturn(user);
        User user2 = new User();
        user2.setName("2");
        user2.setPassword("2");
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        user3.setPassword("3");
        user3.setName("3");
        Chat chat = new Chat(1L);
        chat.getUsers().add(1L);
        chat.getUsers().add(2L);
        chat.getUsers().add(3L);
        Chat chat2 = new Chat(2L);
        chat2.getUsers().add(1L);
        chat2.getUsers().add(2L);
        chat2.getUsers().add(3L);
        List<Long> chatsOfUser2 = new ArrayList<>();
        chatsOfUser2.add(1L);

        when(messageStore.getChatsByUserId(2L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatsByUserId(1L)).thenReturn(chatsOfUser2);
        when(messageStore.getChatById(1L)).thenReturn(chat);
        when(messageStore.getChatById(2L)).thenReturn(chat2);
        when(userStore.getUserById(1L)).thenReturn(user);
        when(userStore.getUserById(2L)).thenReturn(user2);
        when(userStore.getUserById(3L)).thenReturn(user3);
        when(messageStore.addChat()).thenReturn(2L);
        when(activeUsers.get(anyLong())).thenReturn(null);

        doNothing().when(messageStore).addUserToChat(anyLong(), anyLong());
        doAnswer(invocation -> {
            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
            assertEquals(msg.getText(), "New chat created, id: 2");
            return null;
        }).when(session).send(any(Message.class));
        ChatCreateMessage message = new ChatCreateMessage();
        message.setSenderId(1L);
        message.setUsers(new ArrayList<>());
        message.getUsers().add(2L);
        message.getUsers().add(3L);
        CommandByMessage.getCommand(Type.MSG_CHAT_CREATE).execute(session,
                message);
    }
}