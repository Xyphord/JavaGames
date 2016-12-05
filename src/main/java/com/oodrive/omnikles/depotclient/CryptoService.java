package com.oodrive.omnikles.depotclient;

import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivier on 21/11/16.
 */
public class CryptoService {

    public File crypteByCertificats(File file, List<String> certificats) throws IOException {
        if(file.exists()) {
            try {
                final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                List<X509Certificate> certificatsX509 = new ArrayList<>();
                for(String certificat:certificats) {
                    InputStream is = new ByteArrayInputStream(certificat.getBytes());
                    certificatsX509.add((X509Certificate) certFactory.generateCertificate(is));
                }
                return AESUtils.encrypt(file, certificatsX509);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new FileNotFoundException("fichier introuvable : "+ file.getAbsolutePath());
        }
        return null;
    }

    public String decryptWindows(File file) throws FileNotFoundException {
        if(file.exists()){
            CertificatesManager cm = new CertificatesManager();
            List<KeyPair> certificats =  new ArrayList<>();
            String decrypte = null;
            try {
                KeyStore ks = cm.getKeyStore();
                certificats = cm.loadKeyPairsFromKeystore(ks);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                decrypte = AESUtils.decryptByPk(file, certificats.get(0).getPrivateKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return decrypte;
        }else{
            throw new FileNotFoundException("Fichier introuvable : "+ file.getAbsolutePath());
        }
    }
}
