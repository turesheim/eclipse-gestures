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

import org.sigtec.input.AbstractInputDeviceEventListener;
import org.sigtec.input.InputDevice;
import org.sigtec.input.InputDeviceEvent;

public class SWTInputDeviceEventListener extends
		AbstractInputDeviceEventListener {

	@Override
	public void inputDeviceEvent(InputDevice arg0, InputDeviceEvent arg1) {
		if (arg1 instanceof SWTInputDeviceEvent) {
			fireLocationEvent(((SWTInputDeviceEvent) arg1).getLocation());
		}
	}

}