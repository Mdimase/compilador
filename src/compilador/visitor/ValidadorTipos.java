/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.Asignacion;
import compilador.ast.instrucciones.DeclaracionFuncion;
import compilador.ast.instrucciones.DeclaracionVariable;
import compilador.ast.operaciones.binarias.Division;
import compilador.ast.operaciones.binarias.Multiplicacion;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.binarias.Resta;
import compilador.ast.operaciones.binarias.Suma;
import compilador.ast.operaciones.unarias.EnteroAFlotante;
import compilador.ast.operaciones.unarias.FlotanteAEntero;
import compilador.ast.operaciones.unarias.OperacionUnaria;


public class ValidadorTipos extends Transformer{

    private Alcance alcance_actual; //bloque actual, si no esta aca, busco en el padre hasta llegar a null
    
    public void procesar(Programa programa) throws ExcepcionDeTipos{
        this.transform(programa);
    }

    public Bloque transform(Bloque bloque){
        this.alcance_actual = bloque.getAlcance();
        return bloque;
    }
    
    private static Tipo tipo_comun(Tipo tipo_1, Tipo tipo_2) throws ExcepcionDeTipos{
        if (tipo_1 == tipo_2){
            return tipo_1;
        }
        if(tipo_1 == Tipo.INTEGER && tipo_2 == Tipo.FLOAT){
            return tipo_2;
        }
        if(tipo_1 == Tipo.FLOAT && tipo_2 == Tipo.INTEGER){
            return tipo_1;
        }
        throw new ExcepcionDeTipos(
                String.format("No existe un tipo común entre %1$s y %2$s\n", tipo_1, tipo_2 ));
    }    

    // recibe la expresion y el tipo al cual convertirla
    private static Expresion convertir_a_tipo(Expresion expresion, Tipo tipo_destino) throws ExcepcionDeTipos{
        Tipo tipo_origen = expresion.getTipo();
        if(tipo_origen == tipo_destino){
            return expresion;
        }
        if(tipo_origen == Tipo.INTEGER && tipo_destino == Tipo.FLOAT){
            return new EnteroAFlotante(expresion);
        }
        if(tipo_origen == Tipo.FLOAT && tipo_destino == Tipo.INTEGER){
            return new FlotanteAEntero(expresion);
        }
        throw new ExcepcionDeTipos(
                String.format("No existe un tipo común entre %1$s y %2$s\n", tipo_origen, tipo_destino ));
    }
    
    @Override
    public Asignacion transform(Asignacion a) throws ExcepcionDeTipos{
        System.out.println("asignacion");
        Asignacion asignacion = super.transform(a);
        asignacion.setExpresion(convertir_a_tipo(asignacion.getExpresion(), asignacion.getIdentificador().getTipo()));
        return asignacion;
    }
    
    private OperacionUnaria transformarOperacionUnaria(OperacionUnaria ou) throws ExcepcionDeTipos{
        if(ou.getTipo() == Tipo.UNKNOWN){
            ou.setTipo(ou.getExpresion().getTipo());
        }else{
            ou.setExpresion(convertir_a_tipo(ou.getExpresion(), ou.getTipo()));
        }
        return ou;
    }
    
    private OperacionBinaria transformarOperacionBinaria(OperacionBinaria ob) throws ExcepcionDeTipos{
        Tipo tipo_en_comun = tipo_comun(ob.getIzquierda().getTipo(), ob.getDerecha().getTipo());
        ob.setIzquierda(convertir_a_tipo(ob.getIzquierda(),tipo_en_comun));
        ob.setDerecha(convertir_a_tipo(ob.getDerecha(), tipo_en_comun));
        ob.setTipo(tipo_en_comun);
        return ob;
    }

    @Override
    public Division transform(Division d) throws ExcepcionDeTipos {
        Division nueva_op = super.transform(d);
        nueva_op = (Division) transformarOperacionBinaria(nueva_op);
        return nueva_op;
    }

    @Override
    public Multiplicacion transform(Multiplicacion m) throws ExcepcionDeTipos {
        Multiplicacion nueva_op = super.transform(m);
        nueva_op = (Multiplicacion) transformarOperacionBinaria(nueva_op);
        return nueva_op;
    }

    @Override
    public Resta transform(Resta r) throws ExcepcionDeTipos {
        Resta nueva_op = super.transform(r);
        nueva_op = (Resta) transformarOperacionBinaria(nueva_op);
        return nueva_op;
    }

    @Override
    public Suma transform(Suma s) throws ExcepcionDeTipos {
        Suma nueva_op = super.transform(s);
        nueva_op = (Suma) transformarOperacionBinaria(nueva_op);
        return nueva_op;
    }

    @Override
    public FlotanteAEntero transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        FlotanteAEntero nueva_op = super.transform(fae);
        nueva_op = (FlotanteAEntero) transformarOperacionUnaria(nueva_op);
        return nueva_op;
    }

    @Override
    public EnteroAFlotante transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        EnteroAFlotante nueva_op = super.transform(eaf);
        nueva_op = (EnteroAFlotante) transformarOperacionUnaria(nueva_op);
        return nueva_op;
    }

    // x;
    // pregunta si esta declarado variable x is boolean;
    // pregunta si eso que encontro es una instancia de declaracion de variable -> obtengo su tipo
    // se lo seteo al identificador
    // x es un boolean
    // x=2; -> error

    @Override
    public Identificador transform(Identificador identificador) throws ExcepcionDeTipos{
        Object elemento = alcance_actual.resolver(identificador.getNombre());   //verifica si el nombre esta declarado
        Tipo tipo = Tipo.UNKNOWN;
        if(elemento instanceof DeclaracionVariable){
            tipo = ((DeclaracionVariable) elemento).getTipo();
        }
        if(elemento instanceof DeclaracionFuncion){
            tipo = ((DeclaracionFuncion) elemento).getTipoRetorno();
        }
        if (tipo != Tipo.UNKNOWN){  //se encontro y modifico el tipo, ahora es de lo encontrado
            identificador.setTipo(tipo);
            System.out.println(identificador.getNombre());
            System.out.println(identificador.getTipo());
            return identificador;
        }
        throw new ExcepcionDeTipos(String.format("No se declaró el nombre %1$s\n", identificador.getNombre()));
    }
}
