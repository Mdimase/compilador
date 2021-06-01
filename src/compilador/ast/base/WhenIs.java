package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class WhenIs extends Sentencia {

    private Comparador comparador;
    private Expresion expresion;
    private Bloque bloque;

    public WhenIs(Comparador comparador, Expresion expresion, Bloque bloque) {
        this.comparador = comparador;
        this.expresion = expresion;
        this.bloque = bloque;
    }

    public Comparador getComparador() {
        return comparador;
    }

    public void setComparador(Comparador comparador) {
        this.comparador = comparador;
    }

    public Expresion getExpresion() {
        return expresion;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }

    public Bloque getBloque() {
        return bloque;
    }

    public void setBloque(Bloque bloque) {
        this.bloque = bloque;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public WhenIs accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}