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

import java.util.List;

import com.google.common.collect.Lists;

import com.endpoint.lg.support.window.WindowVisibility;

/**
 * Generates partials for managing the visibility of a window.
 * 
 * @author Matt Vollrath <matt@endpoint.com>
 */
public class AwesomeWindowVisibility {
  /**
   * Get a list of properties for managing window visibility.
   * 
   * @param visibility
   *          the window's visibility
   * @return properties for managing visibility
   */
  public static List<String> getProps(WindowVisibility visibility) {
    List<String> list = Lists.newArrayList();

    list.add(String.format("hidden = %s", visibility.getVisible() ? "false" : "true"));
    list.add(String.format("minimized = %s", visibility.getVisible() ? "false" : "true"));
    list.add(String.format("opacity = %d", visibility.getVisible() ? 1 : 0));

    return list;
  }
}
