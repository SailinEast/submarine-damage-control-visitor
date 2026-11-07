package submarine;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;
import submarine.structure.SubmarineHull;
import submarine.visitors.CrewMaintenanceVisitor;
import submarine.visitors.RepairTool;
import submarine.visitors.TuiTelemetryVisitor;
import submarine.visitors.TurnSimulationVisitor;

import java.util.Scanner;

class Main {
    private static final int MAX_TURNS = 6;
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_GREEN  = "\u001B[32m";
    private static final String ANSI_RESET  = "\u001B[0m";

    public static void main(String[] args) {
        SubmarineHull hull = new SubmarineHull();
        hull.addModule(new NuclearReactor())
                .addModule(new BallastTank())
                .addModule(new JunctionBox());

        Scanner scanner = new Scanner(System.in);
        clearTerminalWithANSI();

        for (int turn = 1; turn <= MAX_TURNS; turn++) {
            printTurnBanner(turn);

            TuiTelemetryVisitor telemetry = new TuiTelemetryVisitor();
            hull.runPass(telemetry);
            System.out.println(telemetry.getRenderedHud());

            printToolPrompt();
            RepairTool tool;
            switch (scanner.next()) {
                case "1": tool = RepairTool.COOLANT_ROD; break;
                case "2": tool = RepairTool.WELDING_TORCH; break;
                case "3": tool = RepairTool.SCREWDRIVER; break;
                default: {
                    clearTerminalWithANSI();
                    continue;
                }
            }

            clearTerminalWithANSI();
            hull.runPass(new CrewMaintenanceVisitor(tool));

            TurnSimulationVisitor simulation = new TurnSimulationVisitor();
            hull.runPass(simulation);
            if (simulation.isGameOver()) {
                printGameOverBanner(simulation.getDeathReason());
                return;
            }
        }
        printVictoryBanner();
    }

    private static void printTurnBanner(int turn) {
        System.out.printf(ANSI_YELLOW +
                "═══════════════════ ABYSSAL DEPTH PASS [TURN %d/%d] ══════════════════%n" +
                ANSI_RESET, turn, MAX_TURNS);
    }

    private static void printToolPrompt() {
        System.out.print(ANSI_YELLOW +
                "┌────────────────────── DISPATCH CREW TOOL ────────────────────────┐\n" +
                "│ [1] Coolant Rod (Reactor)                                        │\n" +
                "│ [2] Welding Torch (Ballast)                                      │\n" +
                "│ [3] Screwdriver (Junction)                                       │\n" +
                "└──────────────────────────────────────────────────────────────────┘\n" +
                ">> " + ANSI_RESET);
    }

    private static void printGameOverBanner(String deathReason) {
        System.out.println(ANSI_RED +
                "\n╔══════════════════════════════════════════════════════════════════╗\n" +
                "║                       SIGNAL LOST - S.O.S.                       ║\n" +
                "╚══════════════════════════════════════════════════════════════════╝\n" +
                ">> " + deathReason + ANSI_RESET);
    }

    private static void printVictoryBanner() {
        System.out.println(ANSI_GREEN +
                "\n╔══════════════════════════════════════════════════════════════════╗\n" +
                "║     === DOCKING COMPLETE: Reached Europan Outpost Safely! ===    ║\n" +
                "╚══════════════════════════════════════════════════════════════════╝" +
                ANSI_RESET);
    }

    public static void clearTerminalWithANSI() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception ignored) {
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}