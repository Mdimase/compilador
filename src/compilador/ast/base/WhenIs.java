package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class WhenIs extends Sentencia {

    private SimboloCmp simboloCmp;
    private Expresion expresion;
    private Bloque bloque;

    public WhenIs(String nombre, SimboloCmp simboloCmp, Expresion expresion, Bloque bloque) {
        super(nombre);
        this.simboloCmp = simboloCmp;
        this.expresion = expresion;
        this.bloque = bloque;
    }

    public WhenIs(SimboloCmp simboloCmp, Expresion expresion, Bloque bloque) {
        this.simboloCmp = simboloCmp;
        this.expresion = expresion;
        this.bloque = bloque;
    }

    public SimboloCmp getSimboloCmp() {
        return simboloCmp;
    }

    public void setSimboloCmp(SimboloCmp simboloCmp) {
        this.simboloCmp = simboloCmp;
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