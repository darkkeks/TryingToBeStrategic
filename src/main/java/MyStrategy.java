import model.*;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public final class MyStrategy implements Strategy {

    public static MyStrategy MY_STRATEGY;

    private Random random;

    public Player player;
    public World world;
    public Game game;
    public Move move;
    private MovementManager movementManager;

    public Map<VehicleType, Integer> groupByType = new HashMap<>();
    public Map<Long, Vehicle> vehicleById = new HashMap<>();

    @Override
    public void move(Player player, World world, Game game, Move move) {
        this.player = player;
        this.world = world;
        this.game = game;
        this.move = move;
        this.movementManager = new MovementManager(this);

        boolean isFirstTick = random == null;

        if(isFirstTick) {
            random = new Random(game.getRandomSeed());
        }

        initMove();
        if(isFirstTick) new GroupGenerator(this);

        if(movementManager.canMove())
            process();
        movementManager.move();
    }

    private void initMove() {
        for(Vehicle veh : world.getNewVehicles()) {
            vehicleById.put(veh.getId(), veh);
        }

        for(VehicleUpdate update : world.getVehicleUpdates()) {
            long id = update.getId();
            if(update.getDurability() > 0)
                vehicleById.put(id, new Vehicle(vehicleById.get(id), update));
            else
                vehicleById.remove(update.getId());
        }
    }

    private void process() {

    }
}