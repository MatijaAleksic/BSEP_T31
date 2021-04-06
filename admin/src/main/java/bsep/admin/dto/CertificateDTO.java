package bsep.admin.dto;

public class CertificateDTO {

    Long id;
    String email;

    public CertificateDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public CertificateDTO() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
