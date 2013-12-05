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

package com.endpoint.lg.support.message;

import interactivespaces.util.data.json.JsonBuilder;
import interactivespaces.util.data.json.JsonNavigator;

import com.endpoint.lg.support.domain.Location;
import com.endpoint.lg.support.domain.Orientation;

/**
 * Create messages for various domain objects.
 *
 * @author Keith M. Hughes
 */
public class DomainMessages {

  /**
   * Get an orientation out of the data.
   *
   * @param data
   *          the incoming data
   *
   * @return an orientation for the data
   */
  public static Orientation deserializeOrientation(JsonNavigator data) {
    data.down(MessageFields.MESSAGE_FIELD_ORIENTATION);

    Double heading = data.getDouble(MessageFields.MESSAGE_FIELD_ORIENTATION_HEADING);
    Double tilt = data.getDouble(MessageFields.MESSAGE_FIELD_ORIENTATION_TILT);
    Double roll = data.getDouble(MessageFields.MESSAGE_FIELD_ORIENTATION_ROLL);

    data.up();

    return new Orientation(heading, tilt, roll);
  }

  /**
   * Write an orientation object into the current data.
   *
   * @param orientation
   *          the orientation
   * @param data
   *          the data to write to
   */
  public static void serializeOrientation(Orientation orientation, JsonBuilder data) {
    data.newObject(MessageFields.MESSAGE_FIELD_ORIENTATION);

    data.put(MessageFields.MESSAGE_FIELD_ORIENTATION_HEADING, orientation.getHeading());
    data.put(MessageFields.MESSAGE_FIELD_ORIENTATION_TILT, orientation.getTilt());
    data.put(MessageFields.MESSAGE_FIELD_ORIENTATION_ROLL, orientation.getRoll());

    data.up();
  }

  /**
   * Get an location out of the data.
   *
   * @param data
   *          the incoming data
   *
   * @return a location for the data
   */
  public static Location deserializeLocation(JsonNavigator data) {
    data.down(MessageFields.MESSAGE_FIELD_LOCATION);

    Double altitude = data.getDouble(MessageFields.MESSAGE_FIELD_LOCATION_ALTITUDE);
    Double latitude = data.getDouble(MessageFields.MESSAGE_FIELD_LOCATION_LATITUDE);
    Double longitude = data.getDouble(MessageFields.MESSAGE_FIELD_LOCATION_LONGITUDE);

    data.up();

    return new Location(latitude, longitude, altitude);
  }
}
