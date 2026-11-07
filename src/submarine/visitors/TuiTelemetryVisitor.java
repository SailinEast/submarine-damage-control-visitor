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

    private static final int MAX_CORE_TEMP = 1000;
    private static final int MAX_PERCENT = 100;
    private static final int PROGRESS_BAR_WIDTH = 20;

    private static final String SUBMARINE_ASCII_HEADER =
            ANSI_CYAN +
                    "╔════════════════════════════════════════════════════════════════════════════════════╗\n" +
                    "║  ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~   _..----.._    |=|   ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~   ║\n" +
                    "║   ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~    .'  [o]  [o] `. | |   .    ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~    ║\n" +
                    "║             ______________/_______________\\|_|___|_________________                ║\n" +
                    "║         _.-'                  |                     |              `-._    |\\      ║\n" +
                    "║       .'     [ ((*)) ]        |    [ ~~~ | ~~~ ]    |   [ #=#=# ]      `.__| \\     ║\n" +
                    "║      |     NUCLEAR REACTOR    |     BALLAST TANK    |   JUNCTION BOX     |  |  |>==║\n" +
                    "║       `.     [ ((*)) ]        |    [ ~~~ | ~~~ ]    |   [ #=#=# ]      .'--| /     ║\n" +
                    "║         `-._                  |                     |              _.-'    |/      ║\n" +
                    "║  ~ ~ ~ ~ ~  `-----------------------------------------------------'  ~ ~ ~ ~ ~ ~   ║\n" +
                    "╚════════════════════════════════════════════════════════════════════════════════════╝\n" +
                    ANSI_RESET;

    private final StringBuilder hudBuffer = new StringBuilder(SUBMARINE_ASCII_HEADER);

    public String getRenderedHud() {
        return hudBuffer.toString();
    }

    @Override
    public void visitReactor(NuclearReactor reactor) {
        boolean isCritical = reactor.getCoreTemp() >= CRITICAL_CORE_TEMP;

        hudBuffer.append("┌─ [01: NUCLEAR REACTOR] ────────────────────────────────────────────────────────────┐\n")
                .append("│ System Status       : ");
        appendStatus(isCritical, "[CRITICAL: OVERHEAT]");

        hudBuffer.append("│ Core Temperature    : ");
        appendProgressBar(reactor.getCoreTemp(), MAX_CORE_TEMP, isCritical);
        hudBuffer.append(" ")
                .append(reactor.getCoreTemp())
                .append("°C / 1000°C\n");

        hudBuffer.append("│ Fission Output      : ")
                .append(reactor.getPowerOutputKw())
                .append("kW\n")
                .append("└────────────────────────────────────────────────────────────────────────────────────┘\n");
    }

    @Override
    public void visitBallast(BallastTank ballast) {
        boolean isCritical = ballast.getHullBreachPercent() >= CRITICAL_BREACH_PERCENT;

        hudBuffer.append("┌─ [02: BALLAST TANK] ───────────────────────────────────────────────────────────────┐\n")
                .append("│ System Status       : ");
        appendStatus(isCritical, "[WARNING: FLOODING]");

        hudBuffer.append("│ Ballast Hull Breach : ");
        appendProgressBar(ballast.getHullBreachPercent(), MAX_PERCENT, isCritical);
        hudBuffer.append(" ")
                .append(ballast.getHullBreachPercent())
                .append("% / 100%\n");

        hudBuffer.append("│ Pump Power Draw     : ")
                .append(ballast.getPumpPowerDrawKw())
                .append("kW\n")
                .append("└────────────────────────────────────────────────────────────────────────────────────┘\n");
    }

    @Override
    public void visitJunction(JunctionBox junction) {
        boolean isCritical = junction.getFuseIntegrity() <= CRITICAL_FUSE_INTEGRITY;

        hudBuffer.append("┌─ [03: JUNCTION BOX] ───────────────────────────────────────────────────────────────┐\n")
                .append("│ System Status       : ");
        appendStatus(isCritical, "[WARNING: FUSE FAILING]");

        hudBuffer.append("│ Fuse Integrity      : ");
        appendProgressBar(junction.getFuseIntegrity(), MAX_PERCENT, isCritical);
        hudBuffer.append(" ")
                .append(junction.getFuseIntegrity())
                .append("% / 100%\n");

        hudBuffer.append("│ Max Grid Transfer   : ")
                .append(junction.getMaxTransferKw())
                .append("kW\n")
                .append("└────────────────────────────────────────────────────────────────────────────────────┘");
    }

    private void appendProgressBar(int current, int max, boolean isCritical) {
        int filledBlocks = Math.clamp(((long) current * PROGRESS_BAR_WIDTH) / max, 0, PROGRESS_BAR_WIDTH);
        int emptyBlocks = PROGRESS_BAR_WIDTH - filledBlocks;
        String barColor = isCritical ? ANSI_RED : ANSI_GREEN;

        hudBuffer.append("[")
                .append(barColor).repeat("█", filledBlocks)
                .append(ANSI_RESET).repeat("░", emptyBlocks)
                .append("]");
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