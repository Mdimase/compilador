package compilador.ast.operaciones.binarias;
import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;

public abstract class ComparacionDesigualdad extends Comparacion{

    public ComparacionDesigualdad(Expresion izquierda, Expresion derecha) {
            super(izquierda, derecha);
            }

    public ComparacionDesigualdad(Expresion izquierda, Expresion derecha, Tipo tipo) {
            super(izquierda, derecha, tipo);
            }

    public ComparacionDesigualdad(Expresion izquierda, Expresion derecha, String nombre) {
            super(izquierda, derecha,nombre);
            }

    public ComparacionDesigualdad(Expresion izquierda, Expresion derecha, Tipo tipo, String nombre) {
            super(izquierda, derecha, tipo, nombre);
            }
}
