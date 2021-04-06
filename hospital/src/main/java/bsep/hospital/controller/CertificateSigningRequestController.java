package bsep.hospital.controller;


import bsep.hospital.dto.CertificateSigningRequestDTO;
import bsep.hospital.service.CertificateSigningRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/certificate-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateSigningRequestController {

    @Autowired
    private CertificateSigningRequestService certificateSigningRequestService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> sendCertificateRequest(@RequestBody CertificateSigningRequestDTO certificateSigningRequestDTO){
        certificateSigningRequestService.sendRequest(certificateSigningRequestDTO);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}

