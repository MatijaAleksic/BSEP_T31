package bsep.admin.controller;

import bsep.admin.dto.CertificateRequestDTO;
import bsep.admin.mapper.CertificateRequestMapper;
import bsep.admin.model.CertificateRequest;
import bsep.admin.service.CertificateRequestService;
import bsep.admin.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/certificate_request", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateRequestController {

    @Autowired
    CertificateRequestService certificateRequestService;

    CertificateRequestMapper certificateRequestMapper;

    public CertificateRequestController() {
        certificateRequestMapper = new CertificateRequestMapper();
    }

    @RequestMapping(value = "/send_request", method = RequestMethod.POST)
    public ResponseEntity<?> createCertificateRequest(@RequestBody CertificateRequestDTO certificateRequestDTO) {
        certificateRequestService.createCertificateRequest(certificateRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CertificateRequestDTO>> getAllCertificateRequests() {

        List<CertificateRequestDTO> reqs = certificateRequestMapper.toDtoList(certificateRequestService.findAll());
        return new ResponseEntity<List<CertificateRequestDTO>>(reqs, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCertificateRequest(@PathVariable Long id) {

        if (certificateRequestService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



}
