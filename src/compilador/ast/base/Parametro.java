package compilador.ast.base;

public class Parametro {
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
}
