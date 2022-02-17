package com.l2jserver.gameserver.model.events;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.events.returns.AbstractEventReturn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
public class EventDispatcher {
  private static final Logger LOG = LogManager.getLogger(EventDispatcher.class.getName());

  public static EventDispatcher getInstance() {
    return SingletonHolder._instance;
  }

  public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event) {
    return notifyEvent(event, null, null);
  }

  public <T extends AbstractEventReturn> T notifyEvent(
      IBaseEvent event, ListenersContainer container, Class<T> callbackClass) {
    try {
      return Containers.Global().hasListener(event.getType())
              || ((container != null) && container.hasListener(event.getType()))
          ? notifyEventImpl(event, container, callbackClass)
          : null;
    } catch (Exception e) {
      LOG.warn(" Couldn't notify event " + event.getClass().getSimpleName(), e);
    }
    return null;
  }

  private <T extends AbstractEventReturn> T notifyEventImpl(
      IBaseEvent event, ListenersContainer container, Class<T> callbackClass) {
    if (event == null) {
      throw new NullPointerException("Event cannot be null!");
    }

    T callback = null;
    // Local listener container first.
    if (container != null) {
      callback =
          notifyToListeners(
              container.getListeners(event.getType()), event, callbackClass, callback);
    }

    // Global listener container.
    if ((callback == null) || !callback.abort()) {
      callback =
          notifyToListeners(
              Containers.Global().getListeners(event.getType()), event, callbackClass, callback);
    }

    return callback;
  }

  private <T extends AbstractEventReturn> T notifyToListeners(
      Queue<AbstractEventListener> listeners,
      IBaseEvent event,
      Class<T> returnBackClass,
      T callback) {
    for (AbstractEventListener listener : listeners) {
      try {
        final T rb = listener.executeEvent(event, returnBackClass);
        if (rb == null) {
          continue;
        }

        if ((callback == null) || rb.override()) {
          // Let's check if this listener wants to override previous return object or we simply
          // don't have one
          callback = rb;
        } else if (rb.abort()) {
          // This listener wants to abort the notification to others.
          break;
        }
      } catch (Exception e) {
        LOG.warn(
            "Exception during notification of event: "
                + event.getClass().getSimpleName()
                + " listener: "
                + listener.getClass().getSimpleName(),
            e);
      }
    }

    return callback;
  }

  public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event, Class<T> callbackClass) {
    return notifyEvent(event, null, callbackClass);
  }

  public <T extends AbstractEventReturn> T notifyEvent(
      IBaseEvent event, ListenersContainer container) {
    return notifyEvent(event, container, null);
  }

  /** Executing current listener notification asynchronously */
  public void notifyEventAsync(IBaseEvent event, ListenersContainer... containers) {
    if (event == null) {
      throw new NullPointerException("Event cannot be null!");
    }

    boolean hasListeners = Containers.Global().hasListener(event.getType());
    if (!hasListeners) {
      for (ListenersContainer container : containers) {
        if (container.hasListener(event.getType())) {
          hasListeners = true;
          break;
        }
      }
    }

    if (hasListeners) {
      ThreadPoolManager.getInstance()
          .executeEvent(() -> notifyEventToMultipleContainers(event, containers, null));
    }
  }

  private <T extends AbstractEventReturn> T notifyEventToMultipleContainers(
      IBaseEvent event, ListenersContainer[] containers, Class<T> callbackClass) {
    if (event == null) {
      throw new NullPointerException("Event cannot be null!");
    }

    try {
      T callback = null;
      if (containers != null) {
        // Local listeners container first.
        for (ListenersContainer container : containers) {
          if ((callback == null) || !callback.abort()) {
            callback =
                notifyToListeners(
                    container.getListeners(event.getType()), event, callbackClass, callback);
          }
        }
      }

      // Global listener container.
      if ((callback == null) || !callback.abort()) {
        callback =
            notifyToListeners(
                Containers.Global().getListeners(event.getType()), event, callbackClass, callback);
      }

      return callback;
    } catch (Exception e) {
      LOG.warn("Couldn't notify event " + event.getClass().getSimpleName(), e);
    }
    return null;
  }

  /** Scheduling current listener notification asynchronously after specified delay. */
  public void notifyEventAsyncDelayed(IBaseEvent event, ListenersContainer container, long delay) {
    if (Containers.Global().hasListener(event.getType())
        || container.hasListener(event.getType())) {
      ThreadPoolManager.getInstance()
          .scheduleEvent(() -> notifyEvent(event, container, null), delay);
    }
  }

  private static class SingletonHolder {
    protected static final EventDispatcher _instance = new EventDispatcher();
  }
}
