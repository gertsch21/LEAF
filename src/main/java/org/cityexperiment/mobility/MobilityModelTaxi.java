package org.cityexperiment.mobility;

import org.cityexperiment.city.Street;
import org.leaf.location.Location;
import org.jgrapht.GraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Manages the location a taxi the city.
 */
public class MobilityModelTaxi {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobilityModelTaxi.class.getSimpleName());

    private GraphPath<Location, Street> path;
    private SortedMap<Long, Location> timeLocationMap = new TreeMap<>();
    private double endTime;

    public MobilityModelTaxi(GraphPath<Location, Street> path, double speed, double interval) {
        this.path = path;
        init(speed, interval);
    }

    /**
     * Returns a taxi's location at a specific time step.
     */
    public Location getLocation(double time) {
        if (timeLocationMap.containsKey(timeToMapKey(time))) {
            return timeLocationMap.get(timeToMapKey(time));
        } else {
            LOGGER.error("No precomputed location for time step " + time);
            return Location.NULL;
        }
    }

    /**
     * Computes the random path of the taxi and precomputes its location at specific time steps.
     */
    private void init(double speed, double interval) {
        double distancePerInterval = speed * interval;

        Location lastLocation = null;
        double remainingMetersFromLastPath = 0;
        double time = 0;
        for (Location nextLocation : path.getVertexList()) {
            if (lastLocation != null) {
                double distance = nextLocation.distance(lastLocation);
                for (double fraction : getFractions(distance, remainingMetersFromLastPath, distancePerInterval)) {
                    double newX = lastLocation.getX() + fraction * (nextLocation.getX() - lastLocation.getX());
                    double newY = lastLocation.getY() + fraction * (nextLocation.getY() - lastLocation.getY());
                    timeLocationMap.put(timeToMapKey(time), new Location(newX, newY));
                    time += interval;
                }
                remainingMetersFromLastPath = distance % distancePerInterval;
            }
            lastLocation = nextLocation;
        }
        endTime = time - interval + remainingMetersFromLastPath / speed;
    }

    private List<Double> getFractions(double pathDistance, double remainingLastPathDistance, double distancePerStep) {
        double totalDistance = pathDistance + remainingLastPathDistance;
        int nSteps = (int) (totalDistance / distancePerStep);

        List<Double> fractions = new ArrayList<>();
        for (int i = 1; i <= nSteps; i++) {
            double distanceOnPath = i * distancePerStep - remainingLastPathDistance;
            fractions.add(distanceOnPath / pathDistance);
        }
        return fractions;
    }

    private long timeToMapKey(double time) {
        return Math.round(time * 100);
    }

    public GraphPath<Location, Street> getPath() {
        return path;
    }

    public double getEndTime() {
        return endTime;
    }

}
