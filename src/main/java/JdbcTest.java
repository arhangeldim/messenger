import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.store.dao.GenericDao;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.core.store.PostgresDaoFactory;

/**
 * Created by olegchuikin on 08/04/16.
 */
public class JdbcTest {
    public static void main(String[] args) {
        try {
            PostgresDaoFactory postgresDaoFactory = new PostgresDaoFactory();
            GenericDao<User, Long> userDao = postgresDaoFactory.getDao(User.class);
//            for (User user : userDao.getAll()) {
//                System.out.println(user.getId() + " " + user.getName() + " " + user.getPassword());
//            }

//            User user = new User();
//            user.setName("admin");
//            user.setPassword("admin");
//            userDao.persist(user);
//            userDao.getAll().stream().forEach(System.out::println);

//            System.out.println(userDao.getByPK(22L));
//            System.out.println(userDao.getByPK(300L));

//            TextMessage msg = new TextMessage();
//            msg.setType(Type.MSG_TEXT);
//            msg.setText("Pica pica");
//            msg.setTimestamp(System.currentTimeMillis());
//            msg.setChatId(1L);
//            msg.setSenderId(1L);
//
//            GenericDao<TextMessage, Long> msgDao = postgresDaoFactory.getDao(TextMessage.class);
//            msgDao.persist(msg);
//            msgDao.getAll().stream().forEach(System.out::println);


//            User admin = userDao.getByPK(19L);
//            Chat chat = new Chat();
//            chat.setAdmin(admin);
//            List<Long> prts = new ArrayList<>();
//            prts.add(13L);
//            prts.add(15L);
//            prts.add(17L);
//            chat.setParticipants(prts);
            GenericDao<Chat, Long> chatDao = postgresDaoFactory.getDao(Chat.class);
            Chat byPK = chatDao.getByPK(7L);
            System.out.println(byPK);

        } catch (PersistException e) {
            e.printStackTrace();
        }
    }
}
