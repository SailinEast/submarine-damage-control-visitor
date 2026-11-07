package submarine.visitors;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;

public class CrewMaintenanceVisitor implements ModuleVisitor {
    private static final int COOLING_AMOUNT_CELSIUS = 250;
    private static final int WELD_REPAIR_PERCENT = 35;
    private static final int REWIRE_REPAIR_PERCENT = 40;
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final RepairTool tool;

    public CrewMaintenanceVisitor(RepairTool tool) {
        this.tool = tool;
    }

    @Override
    public void visitReactor(NuclearReactor reactor) {
        if (tool == RepairTool.COOLANT_ROD) {
            int before = reactor.getCoreTemp();
            reactor.injectCoolant(COOLING_AMOUNT_CELSIUS);
            logRepair("Coolant rod injected into Reactor",
                    before,
                    reactor.getCoreTemp(),
                    "°C");
        }
    }

    @Override
    public void visitBallast(BallastTank ballast) {
        if (tool == RepairTool.WELDING_TORCH) {
            int before = ballast.getHullBreachPercent();
            ballast.weldBreach(WELD_REPAIR_PERCENT);
            logRepair("Welded ballast breach",
                    before,
                    ballast.getHullBreachPercent(),
                    "%");
        }
    }

    @Override
    public void visitJunction(JunctionBox junction) {
        if (tool == RepairTool.SCREWDRIVER) {
            int before = junction.getFuseIntegrity();
            junction.rewire(REWIRE_REPAIR_PERCENT);
            logRepair("Rewired junction box fuses",
                    before,
                    junction.getFuseIntegrity(),
                    "%");
        }
    }

    private void logRepair(String action, int before, int after, String unit) {
        System.out.printf(ANSI_GREEN + ">> [MAINTENANCE LOG] %s (%d%s -> %d%s)%n" + ANSI_RESET,
                action, before, unit, after, unit);
    }
}