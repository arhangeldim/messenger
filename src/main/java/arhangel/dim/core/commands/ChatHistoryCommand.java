package arhangel.dim.core.commands;

import arhangel.dim.core.session.Session;

public class ChatHistoryCommand implements Command {
    /**
     * args 0 - название команды, 1 - идентификатор чата
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        return;
    }

    @Override
    public String toString() {
        return "/chat_history";
    }
}
