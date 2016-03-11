package arhangel.dim.lections.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Button {
    private ClickListener[] listeners = new ClickListener[10];
    private int count = 0;

    // закоментирован вариант с List
    //private List<ClickListener> listenerList = new ArrayList<>();

    // Добавляем себе подписчиков
    public void addListener(ClickListener listener) {
        listeners[count++] = listener;

        //listenerList.add(listener);
    }

    // На кнопку кто-то нажал и теперь все об этом узнают
    public void click() {
        for (int i = 0; i < count; i++) {
            listeners[i].onClick();
        }

//        for (ClickListener listener : listenerList) {
//            listener.onClick();
//        }
    }



}
