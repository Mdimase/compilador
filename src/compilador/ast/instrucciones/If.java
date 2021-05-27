package compilador.ast.instrucciones;

import compilador.ast.base.Bloque;
import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.Expresion;
import compilador.visitor.Visitor;

public class If extends Sentencia{

    private Expresion condicion;
    private Bloque bloqueThen;
    private Bloque bloqueElse;

    public If(String nombre, Expresion condicion, Bloque bloqueThen, Bloque bloqueElse) {
        super(nombre);
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
        this.bloqueElse = bloqueElse;
    }

    public If(String nombre, Expresion condicion, Bloque bloqueThen) {
        super(nombre);
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
    }

    public If(Expresion condicion, Bloque bloqueThen) {
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
    }

    public If(Expresion condicion, Bloque bloqueThen, Bloque bloqueElse) {
        this.bloqueElse = bloqueElse;
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
    }

    public Expresion getCondicion() {
        return condicion;
    }

    public void setCondicion(Expresion condicion) {
        this.condicion = condicion;
    }

    public Bloque getBloqueThen() {
        return bloqueThen;
    }

    public void setBloqueThen(Bloque bloqueThen) {
        this.bloqueThen = bloqueThen;
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
}
