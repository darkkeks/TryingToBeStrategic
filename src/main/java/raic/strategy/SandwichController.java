package raic.strategy;

import raic.strategy.enemy.EnemyInfoProvider;
import raic.strategy.math.Vec2D;

public class SandwichController {

    public MyStrategy strategy;
    public EnemyInfoProvider provider;

    private Point centerPoint;
    private Vec2D orientation;

    public int lastOrientationTick = -Util.SANDWICH_ORIENTATION_DELAY;
    public int lastMoveTick = -Util.SANDWICH_MOVE_DELAY;
    public int lastAttackMoveUpdateTick = -Util.ATTACK_MODE_UPDATE_DELAY;

    private boolean orienting = false;

    public SandwichController(MyStrategy strategy) {
        this.strategy = strategy;
        this.provider = new EnemyInfoProvider(strategy);
        this.centerPoint = new Point(119, 119);
        this.orientation = new Vec2D(0, -1);
    }

    public void tick() {
        provider.update();
        Point attackPoint = provider.getAttackPoint();
        if(!orienting) {
            if(Util.delayCheck(Util.SANDWICH_ORIENTATION_DELAY, lastOrientationTick)) {
                lastOrientationTick = strategy.world.getTickIndex();

                updateCenterPoint();
                orient(attackPoint);
            }
            if(centerPoint.sqDist(attackPoint) > Util.ATTACK_MODE_THRESHOLD)
                if(Util.delayCheck(Util.SANDWICH_MOVE_DELAY, lastMoveTick)) {
                    lastMoveTick = strategy.world.getTickIndex();

                    updateCenterPoint();
                    move(attackPoint);
                }
            } else {
                double closestDist = getClosestDist();

                MyMove move;
                if(closestDist * 2 > strategy.game.getIfvGroundAttackRange()) {
                    // move back
                } else {
                    // move forward
                }

                if(strategy.lastSelection.getGroup() != Util.SANDWICH)
                    strategy.movementManager.add(new MyMove()
                            .clearAndSelect(Util.SANDWICH)
                            .next(move));
                else
                    strategy.movementManager.add(move);
            }
        }
    }

    private void move(Point attackPoint) {
        strategy.movementManager.add(new MyMove()
            .clearSelectMove(Util.SANDWICH,
                    attackPoint.getX() - centerPoint.x,
                    attackPoint.getY() - centerPoint.y,
                    Util.SANDWICH_MOVEMENT_SPEED));
    }

    private void orient(Point attackPoint) {
        double current = orientation.angle();
        double negCurrent = (current > 0 ? current - Math.PI : current + Math.PI);
        orientation = new Vec2D(centerPoint.getX() - attackPoint.getX(), centerPoint.getY() - attackPoint.getY());
        double needed = orientation.angle();
        double res = Util.absMin(current - needed, negCurrent - needed);
        strategy.movementManager.add(new MyMove()
                .clearAndSelect(Util.SANDWICH)
                .next(new MyMove()
                        .scale(centerPoint.getX(), centerPoint.getY(), 1.1)
                        .next(new MyMove()
                                .condition(Util.isGroupMovingCondition(Util.SANDWICH).negate())
                                .rotate(centerPoint.getX(), centerPoint.getY(), res)
                                .next(new MyMove()
                                        .condition(Util.isGroupMovingCondition(Util.SANDWICH).negate())
                                        .scale(centerPoint.getX(), centerPoint.getY(), 0.1)
                                        .next(new MyMove()
                                                .condition(Util.isGroupMovingCondition(Util.SANDWICH).negate())
                                                .onApply(() -> this.orienting = false))))));
        orienting = true;
    }

    private double getClosetsDist() {
        double dist = 1e9;
        for(MyVehicle a : vehicleByGroup.get(Util.SANDWICH)) {
            for(MyVehicle b : provider.getGroup().vehicles) {
                dist = min(dist, a.getSquaredDistanceTo(b.getX(), b.getY()));
            }
        }
        return dist;
    }

    private void updateCenterPoint() {
        double x = 0, y = 0;
        int count = 0;
        for(MyVehicle vehicle : strategy.vehicleByGroup.get(Util.SANDWICH).values()) {
            count++;
            x += vehicle.getX(); y += vehicle.getY();
        }
        if(count > 0)
            centerPoint.set(x / count, y / count);
        else centerPoint.set(0, 0);
    }
}
