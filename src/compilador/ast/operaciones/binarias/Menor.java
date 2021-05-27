package compilador.ast.operaciones.binarias;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Menor extends ComparacionDesigualdad{

    public Menor(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha, "<");
    }

    public Menor(Expresion izquierda, Expresion derecha, Tipo tipo) {
        super(izquierda, derecha, tipo, "<");
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    /*
    @Override
    public Menor accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }

     */
}
