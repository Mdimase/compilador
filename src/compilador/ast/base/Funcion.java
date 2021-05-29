package compilador.ast.base;

import compilador.ast.instrucciones.DeclaracionFuncion;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Funcion extends Expresion{

    private DeclaracionFuncion declaracionFuncion;

    public Funcion(DeclaracionFuncion declaracionFuncion) {
        this.declaracionFuncion = declaracionFuncion;
    }

    public DeclaracionFuncion getDeclaracionFuncion() {
        return declaracionFuncion;
    }

    public void setDeclaracionFuncion(DeclaracionFuncion declaracionFuncion) {
        this.declaracionFuncion = declaracionFuncion;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Funcion accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
