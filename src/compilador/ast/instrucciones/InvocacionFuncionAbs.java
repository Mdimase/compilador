package compilador.ast.instrucciones;

import compilador.ast.base.Nodo;

public abstract class InvocacionFuncionAbs extends Nodo {
    public InvocacionFuncionAbs() {
    }

    public InvocacionFuncionAbs(String nombre) {
        super(nombre);
    }
}
