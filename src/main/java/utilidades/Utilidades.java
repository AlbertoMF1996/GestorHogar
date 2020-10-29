package utilidades;

public class Utilidades {

    //constantes campos tabla subcategorias
    public static final String TABLA_SUBCATEGORIA = "subcategorias";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_SUBCATEGORIA = "subcategoria";
    public static final String CAMPO_CATEGORIA = "categoria";

    public static final String TABLA_GASTOS = "gastos";
    public static final String CAMPO_IMPORTE= "importe";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_COMENTARIO= "comentario";

    public static final String CREAR_TABLA_SUBCATEGORIA = "CREATE TABLE "+TABLA_SUBCATEGORIA+" ("+ CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_SUBCATEGORIA+" TEXT, "+CAMPO_CATEGORIA+" TEXT)";
    public static final String CREAR_TABLA_GASTOS = "CREATE TABLE " +TABLA_GASTOS+" ("+ CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +CAMPO_CATEGORIA+ " TEXT, " +CAMPO_SUBCATEGORIA+ " TEXT, "+CAMPO_IMPORTE+" DOUBLE, "+CAMPO_FECHA+" TEXT, "+CAMPO_COMENTARIO+")";
}
