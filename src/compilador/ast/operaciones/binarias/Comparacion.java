package compilador.ast.operaciones.binarias;

import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;

public abstract class Comparacion extends OperacionBinaria {

    public Comparacion(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
    }

    public Comparacion(Expresion izquierda, Expresion derecha, String nombre) {
        super(izquierda, derecha, nombre);
    }

    public Comparacion(Expresion izquierda, Expresion derecha, Tipo tipo) {
        super(izquierda, derecha, tipo);
    }

    public Comparacion(Expresion izquierda, Expresion derecha, Tipo tipo, String nombre) {
        super(izquierda, derecha, tipo, nombre);
    }
}
