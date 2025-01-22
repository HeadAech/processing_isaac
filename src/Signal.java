import java.util.ArrayList;
import java.util.List;

public class Signal<T> {
    private final List<SignalListener<T>> listeners = new ArrayList<>();

    // Add a listener to the signal
    public void connect(SignalListener<T> listener) {
        listeners.add(listener);
    }

    // Remove a listener from the signal
    public void disconnect(SignalListener<T> listener) {
        listeners.remove(listener);
    }

    // Emit the signal to all connected listeners
    public void emit(T data) {
        for (SignalListener<T> listener : listeners) {
            listener.onSignal(data);
        }
    }
}

// Listener interface for handling signals
@FunctionalInterface
interface SignalListener<T> {
    void onSignal(T data);
}