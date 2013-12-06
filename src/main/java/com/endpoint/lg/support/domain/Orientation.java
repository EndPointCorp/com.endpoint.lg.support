/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.endpoint.lg.support.domain;

/**
 * An orientation in space.
 *
 * @author Keith M. Hughes
 */
public class Orientation {

  private Double heading;

  /**
   * The tilt, in degrees.
   */
  private Double tilt;

  /**
   * The tilt, in degrees.
   */
  private Double roll;

  public Orientation() {
  }

  public Orientation(Double heading, Double tilt, Double roll) {
    this.heading = heading;
    this.tilt = tilt;
    this.roll = roll;
  }

  public Double getHeading() {
    return heading;
  }

  public void setHeading(Double heading) {
    this.heading = heading;
  }

  public Double getTilt() {
    return tilt;
  }

  public void setTilt(Double tilt) {
    this.tilt = tilt;
  }

  public Double getRoll() {
    return roll;
  }

  public void setRoll(Double roll) {
    this.roll = roll;
  }
}