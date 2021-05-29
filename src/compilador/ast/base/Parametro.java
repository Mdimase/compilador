package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Parametro extends Sentencia {
    private Tipo tipo;
    private Identificador identificador;
    private Constante valorDefecto;

    public Parametro(Tipo tipo, Identificador identificador, Constante valorDefecto) {
        this.tipo = tipo;
        this.identificador = identificador;
        this.valorDefecto = valorDefecto;
    }

    public Parametro(Tipo tipo, Identificador identificador) {
        this.tipo = tipo;
        this.identificador = identificador;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public Constante getValorDefecto() {
        return valorDefecto;
    }

    public void setValorDefecto(Constante valorDefecto) {
        this.valorDefecto = valorDefecto;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Parametro accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
