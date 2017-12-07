package raic.strategy.enemy;

import raic.model.Facility;
import raic.model.VehicleType;
import raic.strategy.MyVehicle;
import raic.strategy.Point;

import java.util.ArrayList;
import java.util.EnumMap;

public class Group {

    public Facility facility;
    public Point center;
    public int unitCount;
    public EnumMap<VehicleType, Integer> count;
    public ArrayList<MyVehicle> vehicles;

    public Group() {
        unitCount = 0;
        count = new EnumMap<>(VehicleType.class);
        vehicles = new ArrayList<>();
    }

    public Group(Facility facility) {
        this.facility = facility;
    }


    public void add(MyVehicle veh) {
        unitCount++;
        vehicles.add(veh);
        count.put(veh.getType(), count.getOrDefault(veh.getType(), 0) + 1);
    }

    public Point getCenter() {
        if(center == null) {
            if(!isFacility()) {
                double x, y;
                int count = 0;
                x = y = 0;
                for (MyVehicle veh : vehicles) {
                    x += veh.getX();
                    y += veh.getY();
                    count++;
                }
                center = (count > 0 ? new Point(x / count, y / count) : new Point(0, 0));
            } else {
                center = new Point(facility.getLeft() + 32, facility.getTop() + 32);
            }
        }
        return center;
    }

    public boolean isFacility() {
        return facility != null;
    }
}