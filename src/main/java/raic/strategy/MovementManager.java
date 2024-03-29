package raic.strategy;

import raic.model.ActionType;

import java.util.*;

public class MovementManager {
    public static int time = 0;

    public TreeSet<MyMove> delayedMoves = new TreeSet<>(Comparator.comparingInt(a -> a.addTime));

    public MyStrategy strategy;

    public MovementManager(MyStrategy strategy) {
        this.strategy = strategy;
    }

    public void add(MyMove move) {
        move.addTime = time++;
        delayedMoves.add(move);
    }

    public boolean canMove() {
        return delayedMoves.size() > 0 && MyStrategy.player.getRemainingActionCooldownTicks() == 0;
    }

    public void move() {
        if(canMove()) {
            for(MyMove myMove : delayedMoves) {
                if(myMove.canBeApplied()) {
                    if(myMove.hasGenerator)
                        myMove.applyGenerator();

                    if(myMove.move.getAction() == ActionType.CLEAR_AND_SELECT)
                        myMove.apply(strategy.lastSelection);
                    
                    myMove.apply(MyStrategy.move);
                    myMove.onApply.run();

                    if (myMove.hasNext) {
                        MyMove next = myMove.next;
                        next.addTime = (myMove.delay == 0 ? myMove.addTime : time++);
                        next.minTick = MyStrategy.world.getTickIndex() + myMove.delay;
                        delayedMoves.remove(myMove);
                        delayedMoves.add(next);
                    } else {
                        delayedMoves.remove(myMove);
                    }
                    return;
                } else {
                    myMove.retry();
                }
            }
        }
    }
}
