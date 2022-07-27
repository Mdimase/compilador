package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

// tuvimos que hacer que extienda de sentencia por problemas de tipos en el .cup
public class Parametro extends Sentencia {
    private Tipo tipo;
    private Identificador identificador;
    private Constante valorDefecto;
    private String irRef;

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

    public String getIrRef() {
        return irRef;
    }

    public void setIrRef(String irRef) {
        this.irRef = irRef;
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
