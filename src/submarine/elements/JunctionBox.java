package submarine.elements;

import submarine.visitors.ModuleVisitor;

public class JunctionBox implements SubmarineModule {
    private int fuseIntegrity = 100;
    private final int maxTransferKw = 600;

    public int getFuseIntegrity() {
        return fuseIntegrity;
    }

    public int getMaxTransferKw() {
        return maxTransferKw;
    }

    public void fryWires(int damage) {
        fuseIntegrity = Math.max(0, fuseIntegrity - damage);
    }

    public void rewire(int repairAmount) {
        fuseIntegrity = Math.min(100, fuseIntegrity + repairAmount);
    }

    @Override
    public void accept(ModuleVisitor visitor) {
        visitor.visitJunction(this);
    }
}
