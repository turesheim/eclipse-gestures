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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Touch;
import org.sigtec.input.AbstractInputDeviceEventListener;
import org.sigtec.input.BufferedInputDeviceEventListener;
import org.sigtec.input.InputDevice;
import org.sigtec.input.InputDeviceEvent;
import org.sigtec.input.InputDeviceEventListener;
import org.ximtec.igesture.Recogniser;
import org.ximtec.igesture.algorithm.AlgorithmException;
import org.ximtec.igesture.algorithm.siger.SigerRecogniser;
import org.ximtec.igesture.configuration.Configuration;
import org.ximtec.igesture.core.GestureClass;
import org.ximtec.igesture.core.GestureSet;
import org.ximtec.igesture.core.ResultSet;
import org.ximtec.igesture.core.TextDescriptor;
import org.ximtec.igesture.io.ButtonDeviceEventListener;
import org.ximtec.igesture.io.InputDeviceClient;

public class SWTiGestures {

	private static class CircleInfo {

		public CircleInfo(Point inCenter, Color inColor) {
			this.center = inCenter;
			this.color = inColor;
		}

		Point center;
		Color color;
	}

	static Map<Long, CircleInfo> touchLocations = new HashMap<Long, CircleInfo>();
	static int colorIndex = 0;
	static final int PAINTABLE_COLORS = 15;
	static final int CIRCLE_RADIUS = 40;

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setText("Touch demonstration");
		Canvas c = new Canvas(shell, SWT.NONE);
		c.setTouchEnabled(true);
		c.setSize(800, 800);

		c.addPaintListener(new TouchPaintListener());

		shell.setSize(800, 800);
		try {
			initGestures(c);
		} catch (AlgorithmException e) {
			e.printStackTrace();
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	static class TouchPaintListener implements PaintListener {
		public void paintControl(PaintEvent e) {
			Iterator<Map.Entry<Long, CircleInfo>> iter = touchLocations
					.entrySet().iterator();
			while (iter.hasNext()) {
				CircleInfo ci = iter.next().getValue();
				e.gc.setBackground(ci.color);
				e.gc.fillOval(ci.center.x - CIRCLE_RADIUS, ci.center.y
						- CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
			}
		}
	};

	private static void initGestures(final Canvas c) throws AlgorithmException {

		GestureClass leftRightLine = new GestureClass("Left-Right");
		leftRightLine.addDescriptor(new TextDescriptor("E"));
		GestureClass downRight = new GestureClass("Down-Right");
		downRight.addDescriptor(new TextDescriptor("S,E"));
		GestureClass upLeft = new GestureClass("Up-Left");
		upLeft.addDescriptor(new TextDescriptor("N,W"));
		GestureSet gestureSet = new GestureSet("GestureSet");
		gestureSet.addGestureClass(leftRightLine);
		gestureSet.addGestureClass(upLeft);
		gestureSet.addGestureClass(downRight);
		Configuration configuration = new Configuration();
		configuration.addParameter(SigerRecogniser.class.getName(),
				SigerRecogniser.Config.MIN_DISTANCE.name(),
				Integer.toString(100));
		configuration.addGestureSet(gestureSet);
		configuration.addAlgorithm(SigerRecogniser.class.getName());
		final Recogniser recogniser = new Recogniser(configuration);
		InputDevice device = new SWTInputDevice();
		c.addTouchListener((TouchListener) device);
		InputDeviceEventListener listener = new BufferedInputDeviceEventListener(
				new SWTInputDeviceEventListener(), 10000);
		final InputDeviceClient client = new InputDeviceClient(device, listener);
		final Display display = c.getDisplay();
		client.addInputDeviceEventListener(new AbstractInputDeviceEventListener() {

			@Override
			public void inputDeviceEvent(InputDevice arg0, InputDeviceEvent arg1) {
				if (arg1 instanceof SWTInputDeviceEvent) {
					TouchEvent te = ((SWTInputDeviceEvent) arg1)
							.getTouchEvent();
					Touch touches[] = te.touches;

					for (int i = 0; i < touches.length; i++) {
						Touch currTouch = touches[i];

						if ((currTouch.state & (SWT.TOUCHSTATE_UP)) != 0) {
							touchLocations.remove(currTouch.id);
						} else {
							CircleInfo info = touchLocations.get(currTouch.id);
							// XXX: Need to figure out the correct location.
							Point newPoint = Display.getCurrent().map(null,
									(Control) te.widget,
											new Point(currTouch.x * 2,
													currTouch.y * 2));
							if (info == null) {
								info = new CircleInfo(newPoint, display
										.getSystemColor((colorIndex + 2)
												% PAINTABLE_COLORS));
								colorIndex++;
							}

							info.center = newPoint;
							touchLocations.put(currTouch.id, info);
						}
					}
				}
				c.redraw();
			}
			
		});
		client.addButtonDeviceEventListener(new ButtonDeviceEventListener() {
			
			@Override
			public void handleButtonPressedEvent(final InputDeviceEvent event) {
				ResultSet result = recogniser.recognise(client.createNote(0,
						event.getTimestamp(), 70));
				client.clearBuffer();

				if (result.isEmpty()) {
					System.out.println("Not recognised");
				} else {
					System.out
							.println(result.getResult().getGestureClassName());
				}
			}
		});
	}

}