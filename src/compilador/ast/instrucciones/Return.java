package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Return extends Sentencia{

    private Expresion expresion;

    public Return(String nombre, Expresion expresion) {
        super(nombre);
        this.expresion = expresion;
    }

    public Return(Expresion expresion) {
        this.expresion = expresion;
    }

    public Expresion getExpresion() {
        return expresion;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Return accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
