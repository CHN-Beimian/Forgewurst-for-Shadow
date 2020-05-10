package net.wurstclient.fmlevents.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import net.wurstclient.forge.ForgeWurst;

public final class EventManager {
	
	private final ForgeWurst wurst;
	private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap = new HashMap<>();

	public EventManager(ForgeWurst wurst) {
		this.wurst = wurst;
	}

	public <L extends Listener, E extends Event<L>> void fire(E event) {
		if (!wurst.isEnabled())
			return;

		try {
			Class<L> type = event.getListenerType();
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

			if (listeners == null || listeners.isEmpty())
				return;

			// Creating a copy of the list to avoid concurrent modification
			// issues.
			ArrayList<L> listeners2 = new ArrayList<>(listeners);

			// remove() sets an element to null before removing it. When one
			// thread calls remove() while another calls fire(), it is possible
			// for this list to contain null elements, which need to be filtered
			// out.
			listeners2.removeIf(Objects::isNull);

			event.fire(listeners2);

		} catch (Throwable e) {
			e.printStackTrace();

		}
	}

	public <L extends Listener> void add(Class<L> type, L listener) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

			if (listeners == null) {
				listeners = new ArrayList<>(Arrays.asList(listener));
				listenerMap.put(type, listeners);
				return;
			}

			listeners.add(listener);

		} catch (Throwable e) {
			e.printStackTrace();

		}
	}

	public <L extends Listener> void remove(Class<L> type, L listener) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

			if (listeners != null)
				listeners.remove(listener);

		} catch (Throwable e) {
			e.printStackTrace();

		}
	}
}
