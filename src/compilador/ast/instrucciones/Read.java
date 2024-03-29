package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Read extends Expresion {

    public Read(String nombre, Tipo tipo) {
        super(tipo,nombre);
    }

    public Read(String nombre) {
        super(nombre);
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Read accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
