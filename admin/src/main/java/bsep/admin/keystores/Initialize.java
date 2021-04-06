package bsep.admin.keystores;

import bsep.admin.model.SubjectData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

@Component
public class Initialize {

    @Autowired
    KeyStoreReader keyStoreReader;

    @Autowired
    KeyStoreWriter keyStoreWriter;


    @PostConstruct
    public void init() {
        createCerAndCrlForRootCA();
    }

    public void createCerAndCrlForRootCA() {
        try {
            if (!keyStoreWriter.loadKeyStore())
                createCertificate();
        } catch (OperatorCreationException | CertificateException | CRLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createCertificate() throws OperatorCreationException, CertificateException, CRLException, IOException {
        keyStoreWriter.createKeyStore();
        KeyPair keyPair = generateKeyPair();
        SubjectData subjectData = generateSubjectDataPredefined();

        assert keyPair != null;
        subjectData.setPublicKey(keyPair.getPublic());
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        ContentSigner contentSigner = builder.build(keyPair.getPrivate());

        Date startDate = new Date();
        Date endDate = new Date();

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(subjectData.getX500name(), new BigInteger(subjectData.getSerialNumber()),
                startDate, endDate, subjectData.getX500name(), keyPair.getPublic());

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");

        X509Certificate createdCertificate = certConverter.getCertificate(certHolder);

        keyStoreWriter.writeRootCA("admin@gmail.com", keyPair.getPrivate(), createdCertificate);
        keyStoreWriter.saveKeyStore();

        createCRL(keyPair.getPrivate(), subjectData.getX500name());
    }

    private void createCRL(PrivateKey pk, X500Name issuerName) throws CRLException, IOException, OperatorCreationException {
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerName, new Date());
        crlBuilder.setNextUpdate(new Date(System.currentTimeMillis() + 86400 * 1000));

        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        contentSignerBuilder.setProvider("BC");

        //issuer pk
        X509CRLHolder crlHolder = crlBuilder.build(contentSignerBuilder.build(pk));
        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider("BC");

        X509CRL crl = converter.getCRL(crlHolder);

        byte[] bytes = crl.getEncoded();


        OutputStream os = new FileOutputStream("src/main/resources/adminCRLs.crl");
        os.write(bytes);
        os.close();
    }

    private SubjectData generateSubjectDataPredefined() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Datumi od kad do kad vazi sertifikat
        LocalDateTime startDate = LocalDateTime.now();
        startDate.format(dtf);

        LocalDateTime endDate = startDate.plusYears(2);
        endDate.format(dtf);

        Calendar curCal = new GregorianCalendar(TimeZone.getDefault());
        curCal.setTimeInMillis(System.currentTimeMillis());

        String serialNumber = curCal.getTimeInMillis() + "";

        // klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "admin");
        builder.addRDN(BCStyle.SURNAME, "Matija");
        builder.addRDN(BCStyle.GIVENNAME, "Aleksic");
        builder.addRDN(BCStyle.O, "Organizacija");
        builder.addRDN(BCStyle.OU, "OU");
        builder.addRDN(BCStyle.C, "CC");
        builder.addRDN(BCStyle.E, "admin@gmail.com");

        // UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, String.valueOf(1));

        // Kreiraju se podaci za sertifikat, sto ukljucuje:
        // - javni kljuc koji se vezuje za sertifikat
        // - podatke o vlasniku
        // - serijski broj sertifikata
        // - od kada do kada vazi sertifikat
        return new SubjectData(builder.build(), serialNumber, startDate, endDate);

    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);

            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

}
