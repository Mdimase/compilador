package compilador.ast.instrucciones;

public abstract class Declaracion extends Sentencia {
    public Declaracion() {
    }

    public Declaracion(String nombre) {
        super(nombre);
    }
}
