package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Mensaje extends Sentencia {
    private String cadena;

    public Mensaje(String cadena) {
        super(cadena);
        this.cadena = cadena;
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Mensaje accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }

}
