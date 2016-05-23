package arhangel.dim.core.messages;

/**
 * Created by olegchuikin on 23/05/16.
 */
public class ErrorMessage extends Message {
    public ErrorMessage() {
        super(Type.MSG_ERROR);
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ErrorMessage that = (ErrorMessage) o;

        return !(text != null ? !text.equals(that.text) : that.text != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
