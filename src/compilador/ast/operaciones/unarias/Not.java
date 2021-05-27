package compilador.ast.operaciones.unarias;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Not extends OperacionUnaria{

    public Not(Expresion expresion) {
        super("NOT",expresion);
    }

    public Not(Expresion expresion, Tipo tipo) {
        super("NOT",expresion, tipo);
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    /*
    @Override
    public Not accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }

     */
}
