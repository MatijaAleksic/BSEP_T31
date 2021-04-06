package bsep.admin.repository;

import bsep.admin.model.CertificateSigningRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateSigningRequestRepository extends JpaRepository<CertificateSigningRequest, Integer> {

    CertificateSigningRequest findByUid(Long uid);
    CertificateSigningRequest findByEmail(String email);

}
