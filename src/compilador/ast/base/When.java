package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class When extends Sentencia {

    private Expresion expresionBase;
    private List<WhenIs> whenIs;
    private Bloque bloqueElse;

    public When(Expresion expresionBase, List<WhenIs> whenIs, Bloque bloqueElse) {
        this.expresionBase = expresionBase;
        this.whenIs = whenIs;
        this.bloqueElse = bloqueElse;
    }

    public When(Expresion expresionBase, List<WhenIs> whenIs) {
        this.expresionBase = expresionBase;
        this.whenIs = whenIs;
    }

    public When(String nombre, Expresion expresionBase, List<WhenIs> whenIs, Bloque bloqueElse) {
        super(nombre);
        this.expresionBase = expresionBase;
        this.whenIs = whenIs;
        this.bloqueElse = bloqueElse;
    }

    public When(String nombre, Expresion expresionBase, List<WhenIs> whenIs) {
        super(nombre);
        this.whenIs = whenIs;
        this.expresionBase = expresionBase;
    }

    public Expresion getExpresionBase() {
        return expresionBase;
    }

    public void setExpresionBase(Expresion expresionBase) {
        this.expresionBase = expresionBase;
    }

    public List<WhenIs> getWhenIs() {
        return whenIs;
    }

    public void setWhenIs(List<WhenIs> whenIs) {
        this.whenIs = whenIs;
    }

    public Bloque getBloqueElse() {
        return bloqueElse;
    }

    public void setBloqueElse(Bloque bloqueElse) {
        this.bloqueElse = bloqueElse;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Sentencia accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}