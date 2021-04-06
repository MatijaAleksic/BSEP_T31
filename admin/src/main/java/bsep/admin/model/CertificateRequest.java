package bsep.admin.model;

import javax.persistence.*;

@Entity
@Table(name = "certificate_requests")
public class CertificateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commonName", unique = false, nullable = false)
    private String commonName;

    @Column(name = "surname", unique = false, nullable = false)
    private String surname;

    @Column(name = "givenName", unique = false, nullable = false)
    private String givenName;

    @Column(name = "organization", unique = false, nullable = false)
    private String organization;

    @Column(name = "organizationUnit", unique = false, nullable = false)
    private String organizationUnit;

    @Column(name = "country", unique = false, nullable = false)
    private String country;

    @Column(name = "email", unique = false, nullable = false)
    private String email;

    @Column(name = "uid", unique = false, nullable = false)
    private Long uid;


    public CertificateRequest(String commonName, String surname, String givenName, String organization, String organizationUnit, String country, String email, Long uid) {
        this.commonName = commonName;
        this.surname = surname;
        this.givenName = givenName;
        this.organization = organization;
        this.organizationUnit = organizationUnit;
        this.country = country;
        this.email = email;
        this.uid = uid;
    }

    public CertificateRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
