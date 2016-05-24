package arhangel.dim.core.messages;

import java.util.Set;

public class InfoResultMessage extends Message {
    private Long userId;
    private String login;
    private Set<Long> chatIds;

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

    public Set<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(Set<Long> chatIds) {
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
