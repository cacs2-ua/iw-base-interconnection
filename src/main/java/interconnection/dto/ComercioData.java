package interconnection.dto;

import java.util.Objects;

public class ComercioData {

    private Long id;
    private String nombre;
    private String cif;
    private String pais;
    private String provincia;
    private String direccion;
    private String iban;
    private String apiKey;
    private String url_back;
    private boolean activo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUrl_back() {
        return url_back;
    }

    public void setUrl_back(String url_back) {
        this.url_back = url_back;
    }

    public boolean getActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComercioData that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
