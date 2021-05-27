package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.visitor.Visitor;

public class Continue extends Sentencia{

    public Continue() {
    }

    public Continue(String nombre) {
        super(nombre);
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }
}
