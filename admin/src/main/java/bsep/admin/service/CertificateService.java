package bsep.admin.service;

import bsep.admin.Exceptions.CertificatNotCa;
import bsep.admin.Exceptions.CertificateNotFoundException;
import bsep.admin.Exceptions.CertificateNotValid;
import bsep.admin.Exceptions.MultipleAliasFound;
import bsep.admin.dto.CertificateDTO;
import bsep.admin.dto.RevokeCertificateDTO;
import bsep.admin.keystores.KeyStoreReader;
import bsep.admin.keystores.KeyStoreWriter;
import bsep.admin.model.IssuerData;
import ch.qos.logback.classic.Level;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Service
public class CertificateService {

    @Autowired
    CertificateRequestService certificateRequestService;

    @Autowired
    private KeyStoreWriter keyStoreWriter;

    @Autowired
    private KeyStoreReader keyStoreReader;


    public enum RevocationReason {

        RAZLOG1,
        RAZLOG2,
        RAZLOG3

    }

    @PostConstruct
    private void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void createAdminCertificate(CertificateDTO certificateDTO) throws OperatorCreationException, CertificateException, CRLException, IOException, CertificateNotValid, MultipleAliasFound {

        Certificate[] issuerCertificateChain = keyStoreReader.readCertificateChain(certificateDTO.getEmail());

        X509Certificate issuer = (X509Certificate) issuerCertificateChain[0];
        if (!isCertificateValid(issuerCertificateChain))
            throw new CertificateNotValid();

        try {
            if (issuer.getBasicConstraints() == -1 || !issuer.getKeyUsage()[5]) { //sertifikat nije ca
                throw new CertificatNotCa();
            }
        } catch (NullPointerException | CertificatNotCa ignored) {
        }

        //String alias = cerRequestInfoService.findOne(certificateCreationDTO.getSubjectID()).getEmail();
        String alias = getLastAlias(certificateDTO.getEmail());


        Certificate certInfo = keyStoreReader.readCertificate(alias);
        if (certInfo != null) {
            if (!isRevoked(certInfo))
                throw new MultipleAliasFound();
        }
    }

    public boolean isCertificateValid(Certificate[] chain) throws CertificateException, CRLException, IOException {

        if (chain == null) {
            return false;
        }

        X509Certificate cert;
        for (int i = 0; i < chain.length; i++) {
            cert = (X509Certificate) chain[i];

            if (isRevoked(cert)) {
                return false;
            }

            Date now = new Date();

            if (now.after(cert.getNotAfter()) || now.before(cert.getNotBefore())) {
                return false;
            }

            try {
                if (i == chain.length - 1) {
                    return isSelfSigned(cert);
                }
                X509Certificate issuer = (X509Certificate) chain[i + 1];
                cert.verify(issuer.getPublicKey());
            } catch (SignatureException | InvalidKeyException e) {
                return false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean isRevoked(Certificate cer) throws IOException, CertificateException, CRLException {

        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        File file = new File("src/main/resources/adminCRLs.crl");

        byte[] bytes = Files.readAllBytes(file.toPath());
        X509CRL crl = (X509CRL) factory.generateCRL(new ByteArrayInputStream(bytes));

        return crl.isRevoked(cer);
    }

    public static boolean isSelfSigned(X509Certificate cert) {
        try {
            cert.verify(cert.getPublicKey());
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getLastAlias(String email) {

        int lastAliasNumber = 0;
        Enumeration<String> aliases = keyStoreReader.getAllAliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (alias.contains(email) && alias.contains("|")) {
                int number = Integer.parseInt(alias.split("\\|")[1]);
                lastAliasNumber = Math.max(number, lastAliasNumber);
            }
        }
        if (lastAliasNumber != 0) {
            email += lastAliasNumber;
        }


        return email;
    }

    public List<Certificate> getAllCertificates() {

        List<Certificate> certificates = new ArrayList<>();

        Enumeration<String> aliases = keyStoreReader.getAllAliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate cer = keyStoreReader.readCertificate(alias);
            certificates.add(cer);
        }

        return certificates;
    }

    public void revokeCertificate(RevokeCertificateDTO revokeCertificateDTO) throws IOException, CRLException, CertificateNotFoundException, OperatorCreationException, CertificateEncodingException {

        File file = new File("src/main/resources/adminCRLs.crl");

        byte[] bytes = Files.readAllBytes(file.toPath());


        X509CRLHolder holder = new X509CRLHolder(bytes);
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(holder);

        Certificate cer = keyStoreReader.readCertificate(revokeCertificateDTO.getSubjectAlias());
        JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) cer);

        crlBuilder.addCRLEntry(certHolder.getSerialNumber()/*The serial number of the revoked certificate*/, new Date() /*Revocation time*/, RevocationReason.valueOf(revokeCertificateDTO.getRevocationReason()).ordinal() /*Reason for cancellation*/);


        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        contentSignerBuilder.setProvider("BC");


        IssuerData issuer = keyStoreReader.readIssuerFromStore(revokeCertificateDTO.getEmail());

        X509CRLHolder crlHolder = crlBuilder.build(contentSignerBuilder.build(issuer.getPrivateKey()));
        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider("BC");

        X509CRL crl = converter.getCRL(crlHolder);

        bytes = crl.getEncoded();


        OutputStream os = new FileOutputStream("src/main/resources/adminCRLs.crl");
        os.write(bytes);
        os.close();
    }
}
