package com.mobile.qg.qgtaxi.route.entity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11234 on 2018/8/17.
 */
public class RouteFactory {

    public static Routes getRoutes(DriveRouteResult driveRouteResult) {

        List<Route> routes = new ArrayList<>();
        int index = 1;

        List<DrivePath> drivePaths = driveRouteResult.getPaths();
        for (DrivePath drivePath : drivePaths) {

            int allTime = 0;
            int allDistance = 0;

            List<Step> steps = new ArrayList<>();

            List<DriveStep> driveSteps = drivePath.getSteps();
            for (DriveStep driveStep : driveSteps) {

                List<Point> path = new ArrayList<>();

                List<LatLonPoint> latLonPoints = driveStep.getPolyline();
                for (LatLonPoint latLonPoint : latLonPoints) {
                    path.add(new Point(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                }
                if (path.size() == 0) {
                    continue;
                }

                int time = (int) driveStep.getDuration();
                int distance = (int) driveStep.getDistance();

                allTime += time;
                allDistance += distance;

                Step step = new Step(path, path.get(0), path.get(path.size() - 1), time, distance);
                steps.add(step);
            }

            Route route = new Route(index++, steps, allTime, allDistance);
            routes.add(route);

        }
        return new Routes(routes);
    }


}
