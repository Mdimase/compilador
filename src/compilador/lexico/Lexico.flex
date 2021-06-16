package compilador.lexico;

import java.util.ArrayList;import java.util.Stack;
import java_cup.runtime.*;
import java_cup.sym;
import compilador.sintactico.SintacticoSym;


%%

%public
%class Lexico
%cup
%line
%column

%{
    /*************************************************************************
            * En esta sección se puede incluir código que se copiará textualmente
            * como parte de la definición de la clase del analizador léxico.
            * Típicamente serán variables de instancia o nuevos métodos de la clase.
            *************************************************************************/

            int string_yyline = 0;
            int string_yycolumn = 0;

            StringBuffer string = new StringBuffer();

            public enum TipoComentario {LLAVES,PARENTESIS};     //para los 2 tipos de comentarios multilinea
            public Stack<TipoComentario> comentarioAbiertos = new Stack<Lexico.TipoComentario>();  //pila para almacenar

            public ArrayList<MiToken> tablaDeSimbolos = new ArrayList<>();

            private MiToken token(String nombre) {
                return new MiToken(nombre, this.yyline, this.yycolumn);
            }

            private MiToken token(String nombre, Object valor) {
                return new MiToken(nombre, this.yyline, this.yycolumn, valor);
            }

            private MiToken token(String nombre, int line, int column, Object valor) {
                    return new MiToken(nombre, line, column, valor);
                }

            private void errorLexico(String message){
                throw new Error("Linea " + this.yyline + ", columna " + this.yycolumn + ": " + message );
            }
%}

/* espacios en blancos y caracteres validos */

FinLinea = \r|\n|\r\n
Caracter = [^\r\n]
EspacioBlanco = {FinLinea} | [ \t\f]

/* comentarios simples */
ComentarioSimple = "#" {Caracter}* {FinLinea}?

/*Cierra inesperado*/
CierreInesperado = \}|\*\);

/*String*/
string = \"|\\\"

/* enteros */
Digito = [0-9]
Natural = [1-9]{Digito}*
IntegerLiteral = {Natural} | 0

/* flotantes */
ParteDecimal = {Digito}*
FloatLiteral = {IntegerLiteral}*\.{ParteDecimal} | 0\.({Natural}|0+{Natural}) | 0\.0
ReducidoFloatLiteral = \.{ParteDecimal} | \.({Natural}|0+{Natural}) | \.0 | {IntegerLiteral}\. //.99 por ejemplo

/* identificadores*/
//Letra=[a-z]|ñ|[A-Z]|Ñ
Identificador= [^\W\d]\w*\??

%state COMENTARIOPAR
%state COMENTARIOLLA
%state STRING

%%

<YYINITIAL> {
/* palabras claves */

  "boolean"                  { return token("BOOLEAN", yytext()); }
  "integer"                  { return token("INTEGER",yytext());}
  "float"                    { return token("FLOAT", yytext());}
  "begin"                    { return token("BEGIN", yytext());}
  "end"                      { return token("END", yytext());}
  "variable"                 { return token("VARIABLE", yytext());}
  "is"                       { return token("IS", yytext());}
  "if"                       { return token("IF", yytext());}
  "then"                     { return token("THEN" ,yytext());}
  "else"                     { return token("ELSE", yytext());}
  "write"                    { return token("WRITE", yytext());}
  "writeln"                  { return token("WRITELN", yytext());}
  "read_integer"             { return token("READ_INTEGER", yytext());}
  "read_float"               { return token("READ_FLOAT", yytext());}
  "read_boolean"             { return token("READ_BOOLEAN", yytext());}
  "break"                    { return token("BREAK" , yytext());}
  "continue"                 { return token("CONTINUE", yytext());}
  "while"                    { return token("WHILE", yytext());}
  "do"                       { return token("DO", yytext());}
  "main"                     { return token("MAIN", yytext());}
  "function"                 { return token("FUNCTION", yytext());}
  "return"                   { return token("RETURN", yytext());}
  "for"                      { return token("FOR", yytext());}
  "from"                     { return token("FROM", yytext());}
  "to"                       { return token("TO", yytext());}
  "by"                       { return token("BY",yytext());}
  "when"                     { return token("WHEN",yytext());}

  /* espacios en blanco */
  {EspacioBlanco}                   { /* ignore */ }

  /* comentarios */

  {ComentarioSimple}                { /* ignore */ }

  "(*"                              {comentarioAbiertos.push(TipoComentario.PARENTESIS);
                                        yybegin(COMENTARIOPAR);} //invoco al automata comentariop

  "{"                               {comentarioAbiertos.push(TipoComentario.LLAVES);
                                        yybegin(COMENTARIOLLA);} // invoco al automata comentariol

  {CierreInesperado}                { errorLexico("cierre de comentario desbalanceado"); }

  /* operadores aritmeticos */

  "+"                               { return token("MAS", yytext()); }
  "-"                               { return token("MENOS", yytext()); }
  "*"                               { return token("MULTIPLICAR", yytext()); }
  "/"                               { return token("DIVIDIR", yytext()); }

  /* operadores extras */

  "="                               { return token("IGUAL", yytext()); }

  /* operadores logicos */

  "and"                             { return token("AND", yytext()); }
  "or"                              { return token("OR", yytext()); }
  "not"                             { return token("NOT", yytext()); }

  /* comparadores */

  ">"                               { return token("MAYOR", yytext()); }
  "<"                               { return token("MENOR", yytext()); }
  ">="                              { return token("MAYOR_IGUAL", yytext()); }
  "<="                              { return token("MENOR_IGUAL", yytext()); }
  "=="                              { return token("IGUAL_IGUAL", yytext()); }
  "!="                              { return token("DISTINTO", yytext());}

  /* simbolos */

  "("                               { return token("APERTURA_PARENTESIS", yytext()); }
  ")"                               { return token("CIERRE_PARENTESIS", yytext()); }
  "["                               { return token("APERTURA_CORCHETE", yytext());}
  "]"                               { return token("CIERRE_CORCHETE",yytext());}
  ";"                               { return token("PUNTO_COMA", yytext()); }
  ","                               { return token("COMA", yytext()); }
  "."                               { return token("PUNTO", yytext());}

  /* literales */

  "true"                            { return token("TRUE", yytext()); }
  "false"                           { return token("FALSE", yytext()); }

  {IntegerLiteral}                  { return token("INTEGER_LITERAL", yytext());}

  {FloatLiteral}                    { return token("FLOAT_LITERAL", yytext());}

  {ReducidoFloatLiteral}            { return token("FLOAT_LITERAL", yytext());}

  /* identificadores */
  {Identificador}                   { return token("IDENTIFICADOR", yytext()); }

  /* cadenas de caracteres */

    \"                              {   string.setLength(0);   //inicializo el buffer vacio
                                        yybegin(STRING); //invoco a el automata string que lo analize
                                        string_yyline = this.yyline;   //le mando los yyline y column para que los tenga al querer devolver el token
                                        string_yycolumn = this.yycolumn; }

}

/* comentario multiple con llaves */
<COMENTARIOLLA>{
    "}"                         {
                                    if(comentarioAbiertos.pop() == TipoComentario.LLAVES){
                                        // se cerro un comentario de llaves
                                        if(comentarioAbiertos.empty()){
                                            // se cerraron todos los comentarios
                                            yybegin(YYINITIAL);
                                        } else if (comentarioAbiertos.peek() == TipoComentario.PARENTESIS){
                                            yybegin(COMENTARIOPAR);
                                        }
                                    } else {
                                            errorLexico("comentario desbalanceado");
                                    }
                                }

    "{"                         { comentarioAbiertos.push(TipoComentario.LLAVES); }  //comentario anidado
    "(*"                        { comentarioAbiertos.push(TipoComentario.PARENTESIS); yybegin(COMENTARIOPAR);}

    "*)"                        { errorLexico("se esperaba un cierrra llaves, no uno con parentesis");}

    [^]                           { /* ignorar el resto */}

}

/* comentarios multiples con parentesis */
<COMENTARIOPAR>{
    "*)"                        {
                                    if (comentarioAbiertos.pop() == TipoComentario.PARENTESIS){
                                        /* se cerro un comentario parentesis*/
                                        if(comentarioAbiertos.empty()){
                                            // se cerraron todos los comentarios
                                            yybegin(YYINITIAL);
                                        } else if (comentarioAbiertos.peek() == TipoComentario.LLAVES){
                                            yybegin(COMENTARIOLLA);
                                        }
                                    } else {
                                        errorLexico("comentario desbalanceado");
                                    }
                                }
    "(*"                        { comentarioAbiertos.push(TipoComentario.PARENTESIS); }
    "{"                         { comentarioAbiertos.push(TipoComentario.LLAVES); yybegin(COMENTARIOLLA);}

    "}"                         { errorLexico("se esperaba un cierre de parentesis, no uno de llaves"); }

    [^]                         {/* ignoro el resto*/}

}

//automata que verifica los string
<STRING> {
  \"                           { yybegin(YYINITIAL); //vuelvo a initial
                                 return token("STRING_LITERAL", string_yyline, string_yycolumn, string.toString()); }

  \\t                          { string.append('\t'); }  // tab
  \\n                          { string.append('\n'); }  // fin de linea
  \\r                          { string.append('\r'); }  // salto de linea con carriage
  \\\"                         { string.append('\"'); }  // comillas dobles dentro del string con una barra adelante. al buffer solo agrega la comilla
  \\\\                         { string.append('\\'); }  // una barra,protegida, dentro del string. al buffer agrego solo una barra

  [^\n\r\"\\]+                 { string.append( yytext() ); } //lo que no sea fin de linea comillas o barra lo agrego al buffer
}

/* error fallback */
//para todo lo que no este contenido en las definiciones de arriba tira un error

[^]                            { throw new Error("Caracter no permitido: <" + yytext() + ">"); }
