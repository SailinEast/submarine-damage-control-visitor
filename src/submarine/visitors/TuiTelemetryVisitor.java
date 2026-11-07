package submarine.visitors;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;

public class TuiTelemetryVisitor implements ModuleVisitor {
    private static final String ANSI_RED   = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final int CRITICAL_CORE_TEMP = 750;
    private static final int CRITICAL_BREACH_PERCENT = 60;
    private static final int CRITICAL_FUSE_INTEGRITY = 35;

    private final StringBuilder hudBuffer = new StringBuilder();

    public String getRenderedHud() {
        return hudBuffer.toString();
    }

    @Override
    public void visitReactor(NuclearReactor reactor) {
        appendStatus(reactor.getCoreTemp() >= CRITICAL_CORE_TEMP,
                "[CRITICAL: OVERHEAT]");

        hudBuffer.append("Core Temperature: ")
                .append(reactor.getCoreTemp())
                .append("°C\n");

        hudBuffer.append("Power Output: ")
                .append(reactor.getPowerOutputKw())
                .append("kW\n");
    }

    @Override
    public void visitBallast(BallastTank ballast) {
        appendStatus(ballast.getHullBreachPercent() >= CRITICAL_BREACH_PERCENT,
                "[WARNING: FLOODING]");

        hudBuffer.append("Ballast Hull Breach: ")
                .append(ballast.getHullBreachPercent())
                .append("%\n");

        hudBuffer.append("Pump Power Draw: ")
                .append(ballast.getPumpPowerDrawKw())
                .append("kW\n");
    }

    @Override
    public void visitJunction(JunctionBox junction) {
        appendStatus(junction.getFuseIntegrity() <= CRITICAL_FUSE_INTEGRITY,
                "[WARNING: FUSE FAILING]");

        hudBuffer.append("Fuse Integrity: ")
                .append(junction.getFuseIntegrity())
                .append("%\n");

        hudBuffer.append("Max Transfer: ")
                .append(junction.getMaxTransferKw())
                .append("kW\n");
    }

    private void appendRedToBuffer(String message) {
        hudBuffer.append(ANSI_RED)
                .append(message)
                .append(ANSI_RESET)
                .append("\n");
    }

    private void appendNominalToBuffer() {
        hudBuffer.append(ANSI_GREEN)
                .append("[STATUS: NOMINAL]")
                .append(ANSI_RESET)
                .append("\n");
    }

    private void appendStatus(boolean condition, String message) {
        if (condition) {
            appendRedToBuffer(message);
        } else {
            appendNominalToBuffer();
        }
    }
}
