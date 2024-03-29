package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Break extends Sentencia{

    public Break() {
    }

    public Break(String nombre) {
        super(nombre);
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Break accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
