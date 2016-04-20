package arhangel.dim.core.message;

public interface Protocol<T> {

    byte[] encode(T msg) throws Exception;

    T decode(byte[] data) throws Exception;
}
