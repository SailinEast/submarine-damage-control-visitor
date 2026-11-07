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

    public static void main(String[] args) {
        SubmarineHull hull = new SubmarineHull();
        hull.addModule(new NuclearReactor())
                .addModule(new BallastTank())
                .addModule(new JunctionBox());

        TerminalRenderer renderer = new TerminalRenderer();
        Scanner scanner = new Scanner(System.in);

        renderer.playStartAnimation();

        for (int turn = 1; turn <= MAX_TURNS; turn++) {
            renderer.printTurnBanner(turn, MAX_TURNS);

            TuiTelemetryVisitor telemetry = new TuiTelemetryVisitor();
            hull.runPass(telemetry);
            System.out.println(telemetry.getRenderedHud());

            renderer.printToolPrompt();
            RepairTool tool;
            switch (scanner.next()) {
                case "1": tool = RepairTool.COOLANT_ROD; break;
                case "2": tool = RepairTool.WELDING_TORCH; break;
                case "3": tool = RepairTool.SCREWDRIVER; break;
                default: {
                    turn--;
                    renderer.clearTerminal();
                    renderer.printInvalidInputWarning();
                    continue;
                }
            }

            renderer.playWaterCurrentAnimation();
            hull.runPass(new CrewMaintenanceVisitor(tool));

            TurnSimulationVisitor simulation = new TurnSimulationVisitor();
            hull.runPass(simulation);
            if (simulation.isGameOver()) {
                renderer.playLoseAnimation(simulation.getDeathReason());
                return;
            }
        }
        renderer.playVictoryAnimation();
    }
}