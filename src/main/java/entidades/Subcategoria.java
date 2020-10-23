package entidades;

public class Subcategoria {

    private int id;
    private String subcategoria;
    private String categoria;
    private double importeTotal;
    private int gastosTotal;


    public Subcategoria(int id, String subcategoria, String categoria, double importeTotal, int totalGastos) {
        this.id = id;
        this.subcategoria = subcategoria;
        this.categoria = categoria;
        this.importeTotal = importeTotal;
        this.gastosTotal = totalGastos;
    }

    public Subcategoria(String subcategoria, double importeTotal, int gastosTotal) {
        this.subcategoria = subcategoria;
        this.importeTotal = importeTotal;
        this.gastosTotal = gastosTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public int getGastosTotal() {
        return gastosTotal;
    }

    public void setGastosTotal(int totalGastos) {
        this.gastosTotal = totalGastos;
    }
}
