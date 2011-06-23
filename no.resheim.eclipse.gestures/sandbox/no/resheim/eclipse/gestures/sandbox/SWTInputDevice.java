/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.gestures.sandbox;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.widgets.Touch;
import org.sigtec.input.InputDevice;
import org.sigtec.input.InputDeviceEventListener;
import org.ximtec.igesture.io.ButtonDevice;
import org.ximtec.igesture.io.ButtonDeviceEventListener;

public class SWTInputDevice implements InputDevice, ButtonDevice, TouchListener {

	private final ListenerList deviceListeners = new ListenerList();
	private final ListenerList buttonListeners = new ListenerList();

	public void addInputDeviceEventListener(InputDeviceEventListener arg0) {
		deviceListeners.add(arg0);
	}

	public void removeInputDeviceEventListener(InputDeviceEventListener arg0) {
		deviceListeners.remove(arg0);
	}

	@Override
	public void touch(final TouchEvent e) {
		SWTInputDeviceEvent event = new SWTInputDeviceEvent(e);
		for (Touch t : e.touches) {
			// if ((t.state & SWT.TOUCHSTATE_MOVE) != 0) {
				for (Object o : deviceListeners.getListeners()) {
					if (o instanceof InputDeviceEventListener) {
						((InputDeviceEventListener) o).inputDeviceEvent(this,
								event);
					}
				}
			// }
			if ((t.state & SWT.TOUCHSTATE_UP) != 0) {
				for (Object o : buttonListeners.getListeners()) {
					if (o instanceof ButtonDeviceEventListener) {
						((ButtonDeviceEventListener) o)
								.handleButtonPressedEvent(event);
					}
				}
			}
		}
	}

	@Override
	public void addButtonDeviceEventListener(ButtonDeviceEventListener arg0) {
		buttonListeners.add(arg0);
	}

	@Override
	public void removeButtonDeviceEventListener(ButtonDeviceEventListener arg0) {
		buttonListeners.remove(arg0);
	}
}
