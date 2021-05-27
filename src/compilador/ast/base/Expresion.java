package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;

public abstract class Expresion extends Sentencia {
    
    private Tipo tipo;

    public Expresion(Tipo tipo) {
        this.tipo = tipo;
    }

    public Expresion(String nombre) {
        super(nombre);
    }

    public Expresion(Tipo tipo, String nombre) {
        super(nombre);
        this.tipo = tipo;
    }
    
    public Expresion() {
        this.tipo = Tipo.UNKNOWN;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

}
