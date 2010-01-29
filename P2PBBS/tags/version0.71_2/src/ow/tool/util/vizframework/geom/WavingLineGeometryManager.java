/*
 * Copyright 2006 National Institute of Advanced Industrial Science
 * and Technology (AIST), and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ow.tool.util.vizframework.geom;

import java.awt.Shape;
import java.awt.geom.Point2D;

import ow.id.ID;

public class WavingLineGeometryManager extends AbstractLineGeometryManager {
	public final static double WAVE_INTENSITY = 0.65;

	public Shape getShapeForMessage(ID src, ID dest) {
		double srcDbl = src.toBigInteger().doubleValue();
		double destDbl = dest.toBigInteger().doubleValue();

		return super.getShapeForMessage(src, dest,
				this.height((srcDbl + destDbl) / 2.0));
	}

	public Shape getShapeForConnection(ID src, ID dest) {
		double srcDbl = src.toBigInteger().doubleValue();
		double destDbl = dest.toBigInteger().doubleValue();

		return super.getShapeForConnection(src, dest,
				this.height((srcDbl + destDbl) / 2.0));
	}

	public Point2D getNodePoint2D(ID id) {
		return super.getNodePoint2D(id.toBigInteger().doubleValue(),
				this.height(id.toBigInteger().doubleValue()));
	}

	private double height(double id) {
		double idRatio = id / idSpaceSize;

		return (idRatio - 0.5) + Math.sin(idRatio * 2.0 * Math.PI) * WAVE_INTENSITY;
	}
}
