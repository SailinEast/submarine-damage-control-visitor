package submarine.elements;

import submarine.visitors.ModuleVisitor;

public class NuclearReactor implements SubmarineModule {
    private int coreTemp = 300;
    private final int powerOutputKw = 500;

    public int getCoreTemp() {
        return coreTemp;
    }

    public int getPowerOutputKw() {
        return powerOutputKw;
    }

    public void spikeTemperature(int delta) {
        coreTemp += delta;
    }

    public void injectCoolant(int coolingAmount) {
        coreTemp = Math.max(200, coreTemp - coolingAmount);
    }

    @Override
    public void accept(ModuleVisitor visitor) {
        visitor.visitReactor(this);
    }
}
