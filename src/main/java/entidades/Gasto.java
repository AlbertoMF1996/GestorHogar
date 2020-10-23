package entidades;

public class Gasto<Date> {

    private int id;
    private String categoria;
    private String subcategoria;
    private double importe;
    private String fecha;
    private String comentario;

    public Gasto(String categoria, String subcategoria, double importe, String fecha, String comentario) {
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.importe = importe;
        this.fecha = fecha;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "Gasto{" +
                "categoria='" + categoria + '\'' +
                ", subcategoria='" + subcategoria + '\'' +
                ", importe=" + importe +
                ", fecha=" + fecha +
                '}';
    }
}
