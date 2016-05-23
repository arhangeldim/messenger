package arhangel.dim.core.messages;

import java.util.List;

public class InfoResultMessage extends Message {
    private Long userId;
    private String login;
    private List<Long> chatIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
    }

    @Override
    public String toString() {
        return "InfoResultMessage{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", chatIds=" + chatIds +
                "} " + super.toString();
    }
}
