package bsep.admin.controller;

import bsep.admin.Exceptions.CertificateNotFoundException;
import bsep.admin.dto.CertificateDTO;
import bsep.admin.dto.CertificateRequestDTO;
import bsep.admin.dto.RevokeCertificateDTO;
import bsep.admin.model.Admin;
import bsep.admin.model.CertificateRequest;
import bsep.admin.service.CertificateRequestService;
import bsep.admin.service.CertificateService;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    @Autowired
    CertificateService certificateService;

    @Autowired
    CertificateRequestService cerRequestInfoService;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCertificate(@RequestBody CertificateDTO certificateDTO) {
        try {
            certificateService.createAdminCertificate(certificateDTO);
            cerRequestInfoService.delete(certificateDTO.getId());
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllCertificates() {

        List<Certificate> certificateInfoDTO = certificateService.getAllCertificates();
        List<String> certitificates = new ArrayList<>();
        for (Certificate a : certificateInfoDTO)
        {
            certitificates.add(a.toString());
        }
        return new ResponseEntity<List<String>>(certitificates, HttpStatus.OK);

    }


    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> revokeCertificate(@RequestBody RevokeCertificateDTO revokeCertificateDTO) {
        try {
            certificateService.revokeCertificate(revokeCertificateDTO);
        } catch (IOException | CRLException | CertificateException | OperatorCreationException | CertificateNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
