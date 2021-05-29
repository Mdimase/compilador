package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.visitor.Transformer;
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

    @Override
    public Continue accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
