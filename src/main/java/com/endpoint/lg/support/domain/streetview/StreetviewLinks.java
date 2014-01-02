package com.endpoint.lg.support.domain.streetview;

import interactivespaces.util.data.json.JsonNavigator;

/**
 * A collection of links to neighboring Street View panoramas.
 * 
 * @author Matt Vollrath <matt@endpoint.com>
 */
public class StreetviewLinks {

  /**
   * StreetviewLinks field for links to neighboring panos.
   */
  public static final String FIELD_LINKS = "links";

  private StreetviewLink[] links;

  public StreetviewLink[] getLinks() {
    return links;
  }

  public boolean hasLinks() {
    return links.length > 0;
  }

  /**
   * Constructs an empty link collection.
   */
  public StreetviewLinks() {
    links = new StreetviewLink[0];
  }

  /**
   * Constructs a link collection from given <code>JsonNavigator</code>.
   * 
   * <p>
   * Incoming data should be in the format: { "links": [ { "pano": <panoid>,
   * "heading": <heading> }, { ... } ] }
   */
  public StreetviewLinks(JsonNavigator json) {
    json.down(FIELD_LINKS);

    int numLinks = json.getSize();
    links = new StreetviewLink[numLinks];

    for (int i = 0; i < numLinks; i++) {
      JsonNavigator item = new JsonNavigator(json.getItem(i));
      links[i] = new StreetviewLink(item);
    }

    json.up(); // leave it the way you found it
  }

  /**
   * Helper for finding the angular difference between two headings.
   * 
   * @param h1
   *          first heading in degrees
   * @param h2
   *          second heading in degrees
   * @return difference in degrees
   */
  private double headingDifference(double h1, double h2) {
    return Math.abs(((h1 - h2) + 180) % 360 - 180);
  }

  /**
   * Get the nearest link to the given heading.
   * 
   * @param heading
   *          in degrees
   * @return nearest link or null if empty
   */
  public StreetviewLink getNearestLink(double heading) {
    heading %= 360;
    StreetviewLink nearest = null;
    double nearestDifference = 360;

    for (StreetviewLink link : links) {
      double difference = headingDifference(link.getHeading(), heading);

      if (difference < nearestDifference) {
        nearestDifference = difference;
        nearest = link;
      }
    }

    return nearest;
  }

  /**
   * Get the furthest link from the given heading.
   * 
   * @param heading
   *          in degrees
   * @return furthest link or null if empty
   */
  public StreetviewLink getFurthestLink(double heading) {
    return getNearestLink(heading - 180);
  }
}
