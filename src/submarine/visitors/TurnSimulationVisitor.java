package submarine.visitors;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;

import java.util.concurrent.ThreadLocalRandom;

public class TurnSimulationVisitor implements ModuleVisitor {
    private static final int FATAL_CORE_TEMP = 1000;
    private static final int FATAL_BREACH_PERCENT = 100;
    private static final int FATAL_FUSE_INTEGRITY = 0;

    private static final int MIN_HEAT_SPIKE = 120;
    private static final int MAX_HEAT_SPIKE_BOUND = 221;
    private static final int MIN_HAZARD_DAMAGE = 15;
    private static final int MAX_HAZARD_DAMAGE_BOUND = 31;

    private boolean gameOver = false;
    private String deathReason;

    public boolean isGameOver() {
        return gameOver;
    }

    public String getDeathReason() {
        return deathReason;
    }

    @Override
    public void visitReactor(NuclearReactor reactor) {
        int heatSpike = ThreadLocalRandom.current().nextInt(MIN_HEAT_SPIKE, MAX_HEAT_SPIKE_BOUND);
        reactor.spikeTemperature(heatSpike);
        checkOrApplyGameOver(reactor.getCoreTemp() >= FATAL_CORE_TEMP,
                "CATASTROPHIC FAILURE: Nuclear reactor meltdown! The core breached containment.");
    }

    @Override
    public void visitBallast(BallastTank ballast) {
        int breachDamage = ThreadLocalRandom.current().nextInt(MIN_HAZARD_DAMAGE, MAX_HAZARD_DAMAGE_BOUND);
        ballast.punctureHull(breachDamage);
        checkOrApplyGameOver(ballast.getHullBreachPercent() >= FATAL_BREACH_PERCENT,
                "CATASTROPHIC FAILURE: Ballast tank 100% flooded! The submarine sank below crush depth.");
    }

    @Override
    public void visitJunction(JunctionBox junction) {
        int wireDamage = ThreadLocalRandom.current().nextInt(MIN_HAZARD_DAMAGE, MAX_HAZARD_DAMAGE_BOUND);
        junction.fryWires(wireDamage);
        checkOrApplyGameOver(junction.getFuseIntegrity() <= FATAL_FUSE_INTEGRITY,
                "CATASTROPHIC FAILURE: Junction box fuses blown! Total electrical blackout on the grid.");
    }

    private void checkOrApplyGameOver(boolean condition, String deathReason) {
        if (condition) {
            gameOver = true;
            this.deathReason = deathReason;
        }
    }
}
