package ivanov.mikhail.lections.objects;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class StaticDemo {

    private static Logger log = LoggerFactory.getLogger(StaticDemo.class);

    public static void main(String[] args) {
        log.info("Account counter: " + Account.idCounter);

        int current = Account.idCounter;
        // Запрещено, id - это поле экземпляра, его нужно создать и инициализировать
        //int id = Account.id;

        for (int i = 0; i < 5; i++) {
            Account acc = new Account();
            log.info(acc.toString());
        }

        Account acc = null;

        // Так делать нехорошо!
        acc.idCounter = 10;

        // А так можно
        Account.idCounter = 10;


    }
}

class Account {

    static int idCounter = 0;
    int id;

    public Account() {
        id = idCounter++;
    }

    public static Account createAccount() {
        System.out.println("Account created");
        return new Account();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                '}';
    }
}

class ExtAccount extends Account {
    public static Account createAccount() {
        System.out.println("Ext Account created");
        return new Account();
    }
}
