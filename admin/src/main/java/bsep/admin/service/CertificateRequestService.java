package bsep.admin.service;

import bsep.admin.dto.CertificateRequestDTO;
import bsep.admin.model.CertificateRequest;
import bsep.admin.repository.CertificateRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateRequestService {

    @Autowired
    private CertificateRequestRepository certificateRequestRepository;
    

    public CertificateRequest findById(Long id) {
        return certificateRequestRepository.findById(id);
    }

    public CertificateRequest findByEmail(String email)
    {
        return certificateRequestRepository.findByEmail(email);
    }



    public void createCertificateRequest(CertificateRequestDTO certificateRequestDTO)
    {
        CertificateRequest cr = new CertificateRequest(certificateRequestDTO.getCommonName(),
                certificateRequestDTO.getSurname(),certificateRequestDTO.getGivenName(),
                certificateRequestDTO.getOrganization(),certificateRequestDTO.getOrganizationUnit(),
                certificateRequestDTO.getCountry(),certificateRequestDTO.getEmail(),certificateRequestDTO.getUid());

        certificateRequestRepository.save(cr);
    }

    public List<CertificateRequest> findAll()
    {
        return certificateRequestRepository.findAll();
    }

    public boolean delete(Long id) {
        CertificateRequest cr = certificateRequestRepository.findById(id);
        if (cr != null) {
            certificateRequestRepository.delete(cr);
            return true;
        }
        return false;

    }
}
