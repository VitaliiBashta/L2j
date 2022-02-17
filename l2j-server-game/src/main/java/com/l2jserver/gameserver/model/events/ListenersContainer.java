package com.l2jserver.gameserver.model.events;

import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.util.EmptyQueue;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Predicate;

public class ListenersContainer {
  private volatile Map<EventType, Queue<AbstractEventListener>> listeners = null;

  public AbstractEventListener addListener(AbstractEventListener listener) {
    if ((listener == null)) {
      throw new NullPointerException("Listener cannot be null!");
    }
    getListeners()
        .computeIfAbsent(listener.getType(), k -> new PriorityBlockingQueue<>())
        .add(listener);
    return listener;
  }

  /** Creates the listeners container map if doesn't exists. */
  private Map<EventType, Queue<AbstractEventListener>> getListeners() {
    if (listeners == null) {
      synchronized (this) {
        if (listeners == null) {
          listeners = new ConcurrentHashMap<>();
        }
      }
    }
    return listeners;
  }

  /** Unregisters listener for a callback when specified event is executed. */
  public AbstractEventListener removeListener(AbstractEventListener listener) {
    if ((listener == null)) {
      throw new NullPointerException("Listener cannot be null!");
    } else if (listeners == null) {
      throw new NullPointerException("Listeners container is not initialized!");
    } else if (!listeners.containsKey(listener.getType())) {
      throw new IllegalAccessError(
          "Listeners container doesn't had " + listener.getType() + " event type added!");
    }

    listeners.get(listener.getType()).remove(listener);
    return listener;
  }

  public void removeListenerIf(EventType type, Predicate<? super AbstractEventListener> filter) {
    getListeners(type).stream().filter(filter).forEach(AbstractEventListener::unregisterMe);
  }

  /** */
  public Queue<AbstractEventListener> getListeners(EventType type) {
    return (listeners != null) && listeners.containsKey(type)
        ? listeners.get(type)
        : EmptyQueue.emptyQueue();
  }

  public void removeListenerIf(Predicate<? super AbstractEventListener> filter) {
    if (listeners != null) {
      getListeners()
          .values()
          .forEach(
              queue -> queue.stream().filter(filter).forEach(AbstractEventListener::unregisterMe));
    }
  }

  public boolean hasListener(EventType type) {
    return !getListeners(type).isEmpty();
  }
}
