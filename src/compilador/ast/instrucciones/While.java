package compilador.ast.instrucciones;

import compilador.ast.base.Bloque;
import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.Expresion;
import compilador.visitor.Visitor;

public class While extends Sentencia{

    private Expresion condicion;
    private Bloque bloque;

    public While(Expresion condicion, Bloque bloque) {
        this.condicion = condicion;
        this.bloque = bloque;
    }

    public While(String nombre, Expresion condicion, Bloque bloque) {
        super(nombre);
        this.condicion = condicion;
        this.bloque = bloque;
    }

    public Expresion getCondicion() {
        return condicion;
    }

    public void setCondicion(Expresion condicion) {
        this.condicion = condicion;
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
}
