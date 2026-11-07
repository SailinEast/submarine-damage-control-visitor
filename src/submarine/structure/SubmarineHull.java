package submarine.structure;

import submarine.elements.SubmarineModule;
import submarine.visitors.ModuleVisitor;

import java.util.ArrayList;
import java.util.List;

public class SubmarineHull {
    private final List<SubmarineModule> submarineModules = new ArrayList<>();

    public void addModule(SubmarineModule module) {
        submarineModules.add(module);
    }

    public void runPass(ModuleVisitor visitor) {
        submarineModules.forEach(module -> module.accept(visitor));
    }
}
