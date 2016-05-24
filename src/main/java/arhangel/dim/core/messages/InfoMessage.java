package arhangel.dim.core.messages;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class InfoMessage extends Message {
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public InfoMessage() {
        setType(Type.MSG_INFO);
    }
}
