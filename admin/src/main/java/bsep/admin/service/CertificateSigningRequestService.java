package bsep.admin.service;

import bsep.admin.model.CertificateSigningRequest;
import bsep.admin.repository.CertificateSigningRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateSigningRequestService {

    @Autowired
    private CertificateSigningRequestRepository certificateSigningRequestRepository;
    

    public CertificateSigningRequest findById(Long id) {
        return certificateSigningRequestRepository.findById(id);
    }
}
