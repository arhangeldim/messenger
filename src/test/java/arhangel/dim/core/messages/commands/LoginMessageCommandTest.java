package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class LoginMessageCommandTest {

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
    public void testExecute_already() throws Exception {
        doAnswer(invocation -> {
            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
            assertTrue(msg.getText().equals("already logged in"));
            return null;
        }).when(session).send(any(Message.class));

        when(session.getUser()).thenReturn(user);
        CommandByMessage.getCommand(Type.MSG_LOGIN).execute(session,
                new LoginMessage());
    }

//    @Test
//    public void testExecute_new() throws Exception {
//        doAnswer(invocation -> {
//            StatusMessage msg = (StatusMessage) invocation.getArguments()[0];
//            System.out.println(msg.getText());
//            assertTrue(msg.getText().equals("Id: 4\nLogin: tft\n" +
//                    "Password: citftif"));
//            return null;
//        }).when(session).send(any(Message.class));
//
//        LoginMessage loginMessage = new LoginMessage();
//        loginMessage.setLogin("tft");
//        loginMessage.setPassword("citftif");
//
//        when(session.getUser()).thenReturn(null);
//        when(activeUsers.get(anyLong())).thenReturn(null);
//        when(activeUsers.put(anyLong(), any(Session.class))).thenReturn(null);
//        doNothing().when(session).setUser(any());
//
//
//        User usr = new User();
//        usr.setName("tft");
//        usr.setPassword("citftif");
//
//
//
//        User usr2 = new User();
//
//        usr2.setName("tft");
//        usr2.setPassword("citftif");
//        usr2.setId(4L);
//        when(userStore.getUser(any())).thenReturn(usr2);
//        when(userStore.addUser(any())).thenReturn(usr2);
//
//        CommandByMessage.getCommand(Type.MSG_LOGIN).execute(session,
//                loginMessage);
//    }
}