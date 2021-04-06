package bsep.admin.mapper;

import bsep.admin.dto.CertificateRequestDTO;
import bsep.admin.model.CertificateRequest;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CertificateRequestMapper implements MapperInterface<CertificateRequest,CertificateRequestDTO>{

    @Override
    public CertificateRequest toEntity(CertificateRequestDTO dto) {
        return new CertificateRequest(dto.getCommonName(),dto.getSurname(),dto.getGivenName(),dto.getOrganization(),dto.getOrganizationUnit(),dto.getCountry(),dto.getEmail(),dto.getUid());
    }

    @Override
    public CertificateRequestDTO toDto(CertificateRequest entity) {
        return new CertificateRequestDTO(entity.getCommonName(),entity.getSurname(),entity.getGivenName(),entity.getOrganization(),entity.getOrganizationUnit(),entity.getCountry(),entity.getEmail(),entity.getUid());
    }

    public List<CertificateRequestDTO> toDtoList(List<CertificateRequest> requests)
    {
        ArrayList<CertificateRequestDTO> lista = new ArrayList<>();
        for(CertificateRequest r : requests)
        {
            lista.add(this.toDto(r));
        }
        return lista;
    }

}
