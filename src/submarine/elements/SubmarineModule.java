package submarine.elements;

import submarine.visitors.ModuleVisitor;

public interface SubmarineModule {
    void accept(ModuleVisitor visitor);
}
