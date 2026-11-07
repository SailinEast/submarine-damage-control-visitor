package submarine.visitors;

import submarine.elements.BallastTank;
import submarine.elements.JunctionBox;
import submarine.elements.NuclearReactor;

public interface ModuleVisitor {
    void visitReactor(NuclearReactor reactor);
    void visitBallast(BallastTank ballast);
    void visitJunction(JunctionBox junction);
}
