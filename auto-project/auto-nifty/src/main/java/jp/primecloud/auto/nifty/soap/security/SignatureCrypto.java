/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.nifty.soap.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.util.Base64;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class SignatureCrypto implements Crypto {

    protected X509Certificate certificate;

    protected PrivateKey privateKey;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param certificate
     * @param privateKey
     */
    public SignatureCrypto(String certificate, String privateKey) {
        this.certificate = toCertificate(certificate);
        this.privateKey = toPrivateKey(privateKey);
    }

    protected X509Certificate toCertificate(String certificate) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certificate.getBytes()));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    protected PrivateKey toPrivateKey(String privateKey) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(privateKey));
            StringBuilder key = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("-----")) {
                    continue;
                }
                key.append(line);
                key.append("\n");
            }
            reader.close();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.decode(key.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(privKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getCertificates(String alias) throws WSSecurityException {
        return new X509Certificate[] { certificate };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivateKey getPrivateKey(String alias, String password) throws Exception {
        return privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasForX509Cert(Certificate cert) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasForX509Cert(String issuer) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasForX509Cert(byte[] skiBytes) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasForX509Cert(String issuer, BigInteger serialNumber) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasForX509CertThumb(byte[] thumb) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getAliasesForDN(String subjectDN) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCertificateData(boolean reverse, X509Certificate[] certs) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CertificateFactory getCertificateFactory() throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultX509Alias() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeyStore getKeyStore() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSKIBytesFromCert(X509Certificate cert) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getX509Certificates(byte[] data, boolean reverse) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate loadCertificate(InputStream in) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateCertPath(X509Certificate[] certs) throws WSSecurityException {
        throw new UnsupportedOperationException();
    }

}
