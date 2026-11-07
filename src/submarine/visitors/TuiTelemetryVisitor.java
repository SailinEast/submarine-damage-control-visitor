package submarine.visitors;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;

public class TuiTelemetryVisitor implements ModuleVisitor {
    private static final String ANSI_RED   = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN  = "\u001B[36m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final int CRITICAL_CORE_TEMP = 750;
    private static final int CRITICAL_BREACH_PERCENT = 60;
    private static final int CRITICAL_FUSE_INTEGRITY = 35;

    private static final String SUBMARINE_ASCII_HEADER =
            ANSI_CYAN +
                    "╔══════════════════════════════════════════════════════════════════╗\n" +
                    "║                   .__.      |                                    ║\n" +
                    "║          _________/__\\______|___                                 ║\n" +
                    "║       .-'   _    |       |   _  `-.   |\\                         ║\n" +
                    "║      /     (_)   |  [+]  |  (_)    \\__| \\                        ║\n" +
                    "║     |   REACTOR  |BALLAST| JUNCTION   |  |===                    ║\n" +
                    "║      \\___________|_______|_________/ _| /                        ║\n" +
                    "║       `-._______________________.-'   |/                         ║\n" +
                    "╚══════════════════════════════════════════════════════════════════╝\n" +
                    ANSI_RESET;

    private final StringBuilder hudBuffer = new StringBuilder(SUBMARINE_ASCII_HEADER);

    public String getRenderedHud() {
        return hudBuffer.toString();
    }

    @Override
    public void visitReactor(NuclearReactor reactor) {
        hudBuffer.append("┌─ [REACTOR MODULE] ───────────────────────────────────────────────┐\n")
                .append("│ Status              : ");
        appendStatus(reactor.getCoreTemp() >= CRITICAL_CORE_TEMP,
                "[CRITICAL: OVERHEAT]");

        hudBuffer.append("│ Core Temperature    : ")
                .append(reactor.getCoreTemp())
                .append("°C\n");

        hudBuffer.append("│ Power Output        : ")
                .append(reactor.getPowerOutputKw())
                .append("kW\n")
                .append("└──────────────────────────────────────────────────────────────────┘\n");
    }

    @Override
    public void visitBallast(BallastTank ballast) {
        hudBuffer.append("┌─ [BALLAST MODULE] ───────────────────────────────────────────────┐\n")
                .append("│ Status              : ");
        appendStatus(ballast.getHullBreachPercent() >= CRITICAL_BREACH_PERCENT,
                "[WARNING: FLOODING]");

        hudBuffer.append("│ Ballast Hull Breach : ")
                .append(ballast.getHullBreachPercent())
                .append("%\n");

        hudBuffer.append("│ Pump Power Draw     : ")
                .append(ballast.getPumpPowerDrawKw())
                .append("kW\n")
                .append("└──────────────────────────────────────────────────────────────────┘\n");
    }

    @Override
    public void visitJunction(JunctionBox junction) {
        hudBuffer.append("┌─ [JUNCTION MODULE] ──────────────────────────────────────────────┐\n")
                .append("│ Status              : ");
        appendStatus(junction.getFuseIntegrity() <= CRITICAL_FUSE_INTEGRITY,
                "[WARNING: FUSE FAILING]");

        hudBuffer.append("│ Fuse Integrity      : ")
                .append(junction.getFuseIntegrity())
                .append("%\n");

        hudBuffer.append("│ Max Transfer        : ")
                .append(junction.getMaxTransferKw())
                .append("kW\n")
                .append("└──────────────────────────────────────────────────────────────────┘");
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