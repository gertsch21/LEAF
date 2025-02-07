package org.cityexperiment.application;

import org.cityexperiment.infrastructure.InfrastructureGraphCity;
import org.leaf.location.Location;
import org.cityexperiment.infrastructure.Taxi;
import org.cityexperiment.infrastructure.TrafficLightSystem;
import org.leaf.application.Application;
import org.leaf.application.Task;
import org.leaf.application.TaskFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cityexperiment.Settings.*;
import static org.leaf.LeafTags.UPDATE_NETWORK;

/**
 * Factory class for constructing STM Applications.
 */
public class STMApplicationFactory {

    public static Application create(Taxi taxi) {
        Application application = new Application(taxi.getSimulation())
            .addSourceTask(sourceTask(), taxi, CAR_TO_TRAFFIC_MANAGER_BIT_RATE)
            .addProcessingTask(processingTask(), TRAFFIC_MANAGER_TO_TRAFFIC_LIGHT_BIT_RATE);
        // For each traffic light system a sink task is created
        for (TrafficLightSystem tls : getTrafficLightSystemsOnPath(taxi)) {
            application.addSinkTask(sinkTask(), tls);
        }
        application.schedule(UPDATE_NETWORK);
        return application;
    }

    private static Task sourceTask() {
        return TaskFactory.createTask(CAR_OPERATOR_MIPS);
    }

    private static Task processingTask() {
        return TaskFactory.createTask(TRAFFIC_MANAGER_OPERATOR_MIPS);
    }

    private static Task sinkTask() {
        return TaskFactory.createTask(TRAFFIC_LIGHT_OPERATOR_MIPS);
    }

    /**
     * Calculates which traffic light systems are in the path of the taxi.
     */
    private static List<TrafficLightSystem> getTrafficLightSystemsOnPath(Taxi taxi) {
        List<Location> locations = taxi.getMobilityModel().getPath().getVertexList();
        locations.remove(locations.size() - 1);
        locations.remove(0);
        Set<Location> locationSet = new HashSet<>(locations);

        InfrastructureGraphCity topologyExp = (InfrastructureGraphCity) taxi.getSimulation().getNetworkTopology();
        return topologyExp.getTraficLightSystems().stream()
            .filter(tls -> locationSet.contains(tls.getLocation()))
            .collect(Collectors.toList());
    }
}
