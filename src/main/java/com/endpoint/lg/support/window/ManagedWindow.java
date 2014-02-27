/*
 * Copyright (C) 2014 Google Inc.
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

package com.endpoint.lg.support.window;

import interactivespaces.activity.impl.BaseActivity;
import interactivespaces.configuration.Configuration;
import interactivespaces.configuration.SystemConfiguration;
import interactivespaces.util.process.NativeCommandsExecutor;
import interactivespaces.util.resource.ManagedResource;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Support class for placing windows within Space-configured viewports.
 * 
 * @author Matt Vollrath <matt@endpoint.com>
 */
public class ManagedWindow implements ManagedResource {
  /**
   * Configuration key for viewport name.
   */
  public static final String CONFIG_KEY_VIEWPORT_TARGET = "lg.window.viewport.target";

  /**
   * Configuration key for window width relative to viewport.
   */
  public static final String CONFIG_KEY_WINDOW_REL_WIDTH = "lg.window.rel.width";

  /**
   * Configuration key for window height relative to viewport.
   */
  public static final String CONFIG_KEY_WINDOW_REL_HEIGHT = "lg.window.rel.height";

  /**
   * Configuration key for window x placement relative to viewport.
   */
  public static final String CONFIG_KEY_WINDOW_REL_X = "lg.window.rel.x";

  /**
   * Configuration key for window y placement relative to viewport.
   */
  public static final String CONFIG_KEY_WINDOW_REL_Y = "lg.window.rel.y";

  /**
   * Configuration key for viewport width.
   */
  public static final String CONFIG_KEY_VIEWPORT_WIDTH = "lg.window.viewport.%s.width";

  /**
   * Configuration key for viewport height.
   */
  public static final String CONFIG_KEY_VIEWPORT_HEIGHT = "lg.window.viewport.%s.height";

  /**
   * Configuration key for viewport x offset.
   */
  public static final String CONFIG_KEY_VIEWPORT_X = "lg.window.viewport.%s.x";

  /**
   * Configuration key for viewport y offset.
   */
  public static final String CONFIG_KEY_VIEWPORT_Y = "lg.window.viewport.%s.y";

  private static final String XDOTOOL_BIN = "/usr/bin/xdotool";
  private static final String MSG_NOT_IMPLEMENTED =
      "Window management not implemented for this platform";
  // TODO: these should be defined somewhere else
  private static final String PLATFORM_LINUX = "linux";
  private static final String PLATFORM_OSX = "osx";

  private BaseActivity activity;
  private WindowIdentity identity;
  private WindowGeometry geometryOffset;
  private WindowGeometry finalGeometry;

  /**
   * Ensures that window management is supported for this platform.
   * 
   * @return true if the platform is supported
   */
  private boolean checkPlatformSupport() {
    String platform =
        activity.getConfiguration().getPropertyString(SystemConfiguration.PLATFORM_OS);

    if (platform.equals(PLATFORM_LINUX))
      return true;
    else if (platform.equals(PLATFORM_OSX))
      return false;
    else
      return false;
  }

  /**
   * Fetches the viewport geometry.
   * 
   * @return absolute viewport geometry
   */
  private WindowGeometry findViewportGeometry() {
    Configuration activityConfig = activity.getConfiguration();

    String viewportName = activityConfig.getPropertyString(CONFIG_KEY_VIEWPORT_TARGET, null);

    if (viewportName == null) {
      activity.getLog().debug("Viewport not configured");
      return null; // bypass if viewport not configured
    }

    String widthKey = String.format(CONFIG_KEY_VIEWPORT_WIDTH, viewportName);
    String heightKey = String.format(CONFIG_KEY_VIEWPORT_HEIGHT, viewportName);
    String xKey = String.format(CONFIG_KEY_VIEWPORT_X, viewportName);
    String yKey = String.format(CONFIG_KEY_VIEWPORT_Y, viewportName);

    Integer width = activityConfig.getPropertyInteger(widthKey, null);
    Integer height = activityConfig.getPropertyInteger(heightKey, null);
    Integer x = activityConfig.getPropertyInteger(xKey, null);
    Integer y = activityConfig.getPropertyInteger(yKey, null);

    if (width != null && height != null && x != null && y != null) {
      return new WindowGeometry(width, height, x, y);
    } else {
      activity.getLog().error("Viewport configuration is incomplete for " + viewportName);
      return null; // bypass if viewport configuration is incomplete
    }
  }

  /**
   * Gets relative window position from the activity configuration.
   * 
   * @return relative window geometry
   */
  private WindowGeometry findRelativeGeometry() {
    Configuration activityConfig = activity.getConfiguration();

    int relativeWidth = activityConfig.getPropertyInteger(CONFIG_KEY_WINDOW_REL_WIDTH, 0);
    int relativeHeight = activityConfig.getPropertyInteger(CONFIG_KEY_WINDOW_REL_HEIGHT, 0);
    int relativeX = activityConfig.getPropertyInteger(CONFIG_KEY_WINDOW_REL_X, 0);
    int relativeY = activityConfig.getPropertyInteger(CONFIG_KEY_WINDOW_REL_Y, 0);

    return new WindowGeometry(relativeWidth, relativeHeight, relativeX, relativeY);
  }

  /**
   * Combines viewport geometry with offsets.
   * 
   * @return shifted absolute window geometry
   */
  private WindowGeometry calculateGeometry() {
    WindowGeometry geometry = findViewportGeometry();

    if (geometry == null)
      return null; // bypass when viewport geometry is not found

    geometry.offsetBy(findRelativeGeometry());

    geometry.offsetBy(geometryOffset);

    return geometry;
  }

  /**
   * Builds xdotool args for finding the X window id.
   * 
   * @param id
   *          window identity, should be unique to the live activity's runner
   * @return xdotool args
   */
  private List<String> buildSearchArgs(WindowIdentity id) {
    List<String> args = Lists.newArrayList("search", "--maxdepth", "1", "--limit", "1", "--sync", "--onlyvisible");

    String identifier = id.getIdentifier();

    if (id.getType() == WindowIdentity.IdType.NAME) {
      args.addAll(Lists.newArrayList("--name", identifier));
    } else if (id.getType() == WindowIdentity.IdType.CLASS) {
      args.addAll(Lists.newArrayList("--class", identifier));
    } else if (id.getType() == WindowIdentity.IdType.INSTANCE) {
      args.addAll(Lists.newArrayList("--classname", identifier));
    }

    return args;
  }

  /**
   * Builds xdotool args for moving the window.
   * 
   * @param geometry
   *          absolute window size
   * @return xdotool args
   */
  private List<String> buildMoveArgs(WindowGeometry geometry) {
    return Lists.newArrayList("windowmove", geometry.getX().toString(), geometry.getY().toString());
  }

  /**
   * Builds xdotool args for resizing the window.
   * 
   * @param geometry
   *          absolute window position
   * @return xdotool args
   */
  private List<String> buildSizeArgs(WindowGeometry geometry) {
    return Lists.newArrayList("windowsize", geometry.getWidth().toString(), geometry.getHeight()
        .toString());
  }

  /**
   * Positions the window.
   */
  private void positionWindow() {
    if (!checkPlatformSupport()) {
      activity.getLog().warn(MSG_NOT_IMPLEMENTED);
      return;
    }

    finalGeometry = calculateGeometry();

    if (finalGeometry == null)
      return; // bypass when geometry could not be found

    ScheduledExecutorService executor = activity.getSpaceEnvironment().getExecutorService();

    executor.execute(new Runnable() {
      public void run() {
        NativeCommandsExecutor exec = new NativeCommandsExecutor();

        List<List<String>> commands = Lists.newArrayList();

        List<String> xdoCommand = Lists.newArrayList(XDOTOOL_BIN);
        xdoCommand.addAll(buildSearchArgs(identity));
        xdoCommand.addAll(buildMoveArgs(finalGeometry));
        xdoCommand.addAll(buildSizeArgs(finalGeometry));

        commands.add(xdoCommand);
        exec.executeCommands(commands);
      }
    });
  }

  /**
   * Constructs a ManagedWindow for the given activity with a relative window
   * geometry offset.
   * 
   * @param activity
   *          the activity responsible for the window
   * @param identity
   *          identifier for the window
   * @param geometryOffset
   *          relative geometry within the configured viewport
   */
  public ManagedWindow(BaseActivity activity, WindowIdentity identity, WindowGeometry geometryOffset) {
    this.activity = activity;
    this.identity = identity;
    this.geometryOffset = geometryOffset;
  }

  /**
   * Constructs a ManagedWindow for the given activity.
   * 
   * @param activity
   *          the activity responsible for the window
   * @param identity
   *          identifier for the window
   */
  public ManagedWindow(BaseActivity activity, WindowIdentity identity) {
    this(activity, identity, new WindowGeometry(0, 0, 0, 0));
  }

  @Override
  public void shutdown() {
  }

  /**
   * Start managing the window.
   */
  @Override
  public void startup() {
    positionWindow();
  }

  /**
   * Updates the window positioning, picking up changes in configuration.
   */
  public void update() {
    positionWindow();
  }
}
