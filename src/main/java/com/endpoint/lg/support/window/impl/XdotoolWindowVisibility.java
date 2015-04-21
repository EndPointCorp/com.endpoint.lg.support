/*
 * Copyright (C) 2014 Google Inc.
 * Copyright (C) 2015 End Point Corporation
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

package com.endpoint.lg.support.window.impl;

import com.endpoint.lg.support.window.WindowVisibility;

/**
 * Generates xdotool flags for showing or hiding a window.
 * 
 * @author Matt Vollrath <matt@endpoint.com>
 */
public class XdotoolWindowVisibility {
  /**
   * Generates xdotool flags for showing or hiding a window.
   * 
   * @param visibility
   *          the window's intended visibility
   * @return xdotool flags
   */
  public static String getFlags(WindowVisibility visibility) {
    return visibility.getVisible() ? "windowactivate windowfocus" : "windowminimize";
  }
}
