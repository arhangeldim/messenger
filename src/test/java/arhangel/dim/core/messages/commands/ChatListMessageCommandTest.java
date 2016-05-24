package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class ChatListMessageCommandTest {

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
        CommandByMessage.getCommand(Type.MSG_CHAT_LIST).execute(session,
                new ChatListMessage());
    }

    @Test
    public void testExecute_normal() throws Exception {
        doAnswer(invocation -> {
            ChatListResultMessage msg
                    = (ChatListResultMessage) invocation.getArguments()[0];
            assertThat(msg.getChats(), hasItems(1L, 2L, 3L, 15L));
            assertThat(msg.getChats().size(), is(4));
            return 0;
        }).when(session).send(any(Message.class));

        when(session.getUser()).thenReturn(user);
        List<Long> result = new ArrayList<>();
        result.add(1L);
        result.add(2L);
        result.add(3L);
        result.add(15L);
        when(messageStore.getChatsByUserId(1L)).thenReturn(result);
        CommandByMessage.getCommand(Type.MSG_CHAT_LIST).execute(session,
                new ChatListMessage());
    }
}