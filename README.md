# Assignment #6 — Visitor Pattern: Europan Submarine Damage-Control Simulation

A turn-based terminal (TUI) submarine survival simulation inspired by *Barotrauma*, demonstrating the **Visitor** behavioral design pattern in Java.

The submarine's physical hardware modules form a fixed **Element** hierarchy, while completely unrelated game phases—ANSI telemetry rendering, tool-based crew repairs, and abyssal hazard simulation—are implemented as separate **Visitors** without modifying the hardware classes.

---

## 1. Architecture & Technical Requirements Mapping

| Visitor Pattern Component | Project Class / Interface | Role & Implementation Details |
| :--- | :--- | :--- |
| **Element Interface** | `SubmarineModule` | Declares `void accept(ModuleVisitor visitor)`. |
| **Concrete Elements (3)** | `NuclearReactor`<br>`BallastTank`<br>`JunctionBox` | Represent fixed submarine hardware compartments. Each implements `accept(visitor)` by invoking its corresponding `visitor.visitX(this)` method (Double Dispatch). |
| **Visitor Interface** | `ModuleVisitor` | Declares `visitReactor(NuclearReactor)`, `visitBallast(BallastTank)`, and `visitJunction(JunctionBox)`. |
| **Concrete Visitors (3)** | `TuiTelemetryVisitor`<br>`CrewMaintenanceVisitor`<br>`TurnSimulationVisitor` | Each encapsulates one distinct operation across all hardware modules:<br>• **`TuiTelemetryVisitor`**: Renders an 86-column ASCII submarine cross-section and color-coded status progress bars.<br>• **`CrewMaintenanceVisitor`**: Applies tool-specific crew repairs (`COOLANT_ROD`, `WELDING_TORCH`, `SCREWDRIVER`) and logs state changes.<br>• **`TurnSimulationVisitor`**: Applies randomized abyssal wear-and-tear and evaluates catastrophic failure thresholds. |
| **Object Structure** | `SubmarineHull` | Encapsulates a `private final List<SubmarineModule>` and provides `runPass(ModuleVisitor visitor)` to traverse all registered modules via `module.accept(visitor)`. |

---

## 2. Project Structure

```text
src/
└── submarine/
    ├── Main.java                          # Turn loop & user input orchestration
    ├── TerminalRenderer.java              # Console clearing & multi-frame ASCII water animations
    ├── elements/
    │   ├── SubmarineModule.java           # Element Interface
    │   ├── NuclearReactor.java            # Concrete Element 1
    │   ├── BallastTank.java               # Concrete Element 2
    │   └── JunctionBox.java               # Concrete Element 3
    ├── structure/
    │   └── SubmarineHull.java             # Object Structure
    └── visitors/
        ├── ModuleVisitor.java             # Visitor Interface
        ├── RepairTool.java                # Enum: COOLANT_ROD, WELDING_TORCH, SCREWDRIVER
        ├── TuiTelemetryVisitor.java       # Concrete Visitor 1 (HUD & Progress Bars)
        ├── CrewMaintenanceVisitor.java    # Concrete Visitor 2 (Player Repair Actions)
        └── TurnSimulationVisitor.java     # Concrete Visitor 3 (Abyssal Hazards & Win/Loss Check)
```

---

## 3. Clean Code Principles & Justifications

### 1. Meaningful Names Distinguishing Element vs. Visitor Responsibilities
* **Elements** are named as concrete physical nouns (`NuclearReactor`, `BallastTank`, `JunctionBox`) representing ship hardware.
* **Visitors** are named by the specific operational pass they perform (`TuiTelemetryVisitor`, `CrewMaintenanceVisitor`, `TurnSimulationVisitor`).
* **Zero Magic Numbers**: Thresholds and bounds (`CRITICAL_CORE_TEMP = 750`, `FATAL_CORE_TEMP = 1000`, `COOLING_AMOUNT_CELSIUS = 250`, `PROGRESS_BAR_WIDTH = 20`) are extracted as descriptive `private static final` constants.

### 2. Zero `instanceof` or Typecasting Chains (Pure Double Dispatch)
* `SubmarineHull` iterates strictly over the abstract `SubmarineModule` interface (`submarineModules.forEach(module -> module.accept(visitor))`).
* Even though `CrewMaintenanceVisitor` reacts differently depending on both the equipped `RepairTool` and the hardware module type, it never uses `instanceof` or downcasting; double dispatch routes execution directly to `visitReactor`, `visitBallast`, or `visitJunction` with the exact compile-time type.

### 3. Single Responsibility Principle (SRP)
* **Hardware Elements** (`NuclearReactor`, `BallastTank`, `JunctionBox`) only store module state and enforce domain bounds—they contain zero UI formatting, random number generation, or game-over rules.
* **Each Concrete Visitor** has a single reason to change: `TuiTelemetryVisitor` only changes if the HUD layout changes; `CrewMaintenanceVisitor` only changes if tool repair values change; `TurnSimulationVisitor` only changes if hazard rules change.
* **`TerminalRenderer`** isolates OS screen clearing and frame-by-frame ASCII animations from `Main`, keeping the game loop concise and readable.

### 4. Minimal, Well-Justified Encapsulation Trade-Offs
* **Where Visitors Need Access**: Visitors must read module telemetry to render progress bars/check fail states, and modify module state during repairs/hazards.
* **How It Was Handled Safely**: All element fields (`coreTemp`, `hullBreachPercent`, `fuseIntegrity`) are strictly `private` (and static ratings like `powerOutputKw` are `private final`). Instead of exposing raw, unrestricted setters (such as `setCoreTemp(int)`), elements expose:
    1. **Read-only getters** (`getCoreTemp()`, `getHullBreachPercent()`, `getFuseIntegrity()`) for inspection.
    2. **Bounded domain mutators** (`injectCoolant`, `spikeTemperature`, `weldBreach`, `punctureHull`, `rewire`, `fryWires`) that internally clamp values via `Math.min` and `Math.max` so external visitors can never violate hardware invariants (e.g., breach percentage cannot drop below `0%` or exceed `100%`, and reactor temperature cannot drop below its `200°C` baseline).

### 5. Demonstrated Extensibility (Open-Closed Principle)
* Adding the third visitor (`TurnSimulationVisitor`)—or adding future visitors such as an `OutpostRepairCostVisitor` or `JsonSaveVisitor`—requires **zero modifications** to `SubmarineModule`, `SubmarineHull`, `NuclearReactor`, `BallastTank`, or `JunctionBox`.

---

## 4. How Double Dispatch Works in This Project

When `hull.runPass(visitor)` executes inside `SubmarineHull`:

1. **First Polymorphic Dispatch (Resolves the Concrete Element at runtime):**
    * `module.accept(visitor)` is called on a `SubmarineModule` reference.
    * Java's runtime polymorphism resolves the actual object type (e.g., `NuclearReactor.accept(ModuleVisitor visitor)`).
2. **Second Polymorphic Dispatch (Resolves the Concrete Visitor + passes exact Element type):**
    * Inside `NuclearReactor.accept(visitor)`, the element executes `visitor.visitReactor(this)`.
    * Because `this` inside `NuclearReactor` has the exact compile-time type `NuclearReactor`, the compiler binds to `visitReactor(NuclearReactor)`, and Java's runtime resolves which Concrete Visitor (`TuiTelemetryVisitor`, `CrewMaintenanceVisitor`, or `TurnSimulationVisitor`) is executing.

---

## 5. How to Run

Compile and run from a terminal supporting ANSI escape codes (e.g., IntelliJ Terminal tab, Windows Terminal, macOS/Linux Terminal):

```bash
javac -d out src/submarine/elements/*.java src/submarine/structure/*.java src/submarine/visitors/*.java src/submarine/*.java
java -cp out submarine.Main
```