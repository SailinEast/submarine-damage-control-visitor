package submarine.elements;

import submarine.visitors.ModuleVisitor;

public class BallastTank implements SubmarineModule {
    private int hullBreachPercent = 0;
    private final int pumpPowerDrawKw = 200;

    public int getHullBreachPercent() {
        return hullBreachPercent;
    }

    public int getPumpPowerDrawKw() {
        return pumpPowerDrawKw;
    }

    public void punctureHull(int damage) {
        hullBreachPercent = Math.min(100, hullBreachPercent + damage);
    }

    public void weldBreach(int repairAmount) {
        hullBreachPercent = Math.max(0, hullBreachPercent - repairAmount);
    }

    @Override
    public void accept(ModuleVisitor visitor) {
        visitor.visitBallast(this);
    }
}
