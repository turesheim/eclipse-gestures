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
import org.eclipse.swt.events.TouchEvent;
import org.sigtec.ink.input.Location;
import org.sigtec.ink.input.TimestampedLocation;
import org.sigtec.input.InputDeviceEvent;

/**
 * This types takes the SWT Touch event and wraps it in an API that is
 * consumable by the iGestures API.
 * 
 * @author Torkild U. Resheim
 * 
 */
public class SWTInputDeviceEvent implements InputDeviceEvent {

	private final TouchEvent touchEvent;
	private final long timeStamp;
	private final TimestampedLocation location;

	public TimestampedLocation getLocation() {
		return location;
	}

	public SWTInputDeviceEvent(TouchEvent touchEvent) {
		this.touchEvent = touchEvent;
		this.timeStamp = touchEvent.time;
		Location l = new Location("screen", 1, touchEvent.x, touchEvent.y);
		location = new TimestampedLocation(l, timeStamp);
	}

	@Override
	public long getTimestamp() {
		return timeStamp;
	}

	public TouchEvent getTouchEvent() {
		return touchEvent;
	}

}
