package compilador.ast.instrucciones;

import compilador.ast.base.*;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeclaracionFuncion extends Declaracion{
    private Identificador identificador;
    private Tipo tipoRetorno;
    private List<Parametro> parametros = new ArrayList<>();
    private Bloque bloque;

    public DeclaracionFuncion(Identificador identificador, Tipo tipoRetorno, List<Parametro> parametros, Bloque bloque) {
        this.identificador = identificador;
        this.tipoRetorno = tipoRetorno;
        this.parametros =  parametros;
        this.bloque=bloque;
    }

    public DeclaracionFuncion(Identificador identificador, Tipo tipoRetorno, Bloque bloque) {
        this.identificador = identificador;
        this.tipoRetorno = tipoRetorno;
        this.bloque = bloque;
    }

    public DeclaracionFuncion(String nombre, Identificador identificador, Tipo tipoRetorno, List<Parametro> parametros, Bloque bloque) {
        super(nombre);
        this.identificador = identificador;
        this.tipoRetorno = tipoRetorno;
        this.parametros = parametros;
        this.bloque = bloque;
    }

    public DeclaracionFuncion(String nombre, Identificador identificador, Tipo tipoRetorno, Bloque bloque) {
        super(nombre);
        this.identificador = identificador;
        this.tipoRetorno = tipoRetorno;
        this.bloque = bloque;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public Tipo getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTipoRetorno(Tipo tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public List<Parametro> getParametros() {
        return parametros;
    }

    public void setParametros(List<Parametro> parametros) {
        this.parametros = parametros;
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

    @Override
    public DeclaracionFuncion accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
