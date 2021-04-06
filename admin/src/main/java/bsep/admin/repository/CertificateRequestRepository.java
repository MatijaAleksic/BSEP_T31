package bsep.admin.repository;

import bsep.admin.model.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Integer> {

    CertificateRequest findByUid(Long uid);
    CertificateRequest findByEmail(String email);
    CertificateRequest findById(Long id);
    List<CertificateRequest> findAll();
}
