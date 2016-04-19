package arhangel.dim.core.commands;

import arhangel.dim.core.session.Session;

public interface Command {

    void run(String[] args, Session session) throws Exception;

    String toString();
}
