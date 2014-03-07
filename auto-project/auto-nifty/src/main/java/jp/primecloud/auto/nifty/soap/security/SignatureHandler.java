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

import java.io.StringWriter;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

    protected Log log = LogFactory.getLog(SignatureHandler.class);

    protected Crypto crypto;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param certificate
     * @param privateKey
     */
    public SignatureHandler(String certificate, String privateKey) {
        crypto = new SignatureCrypto(certificate, privateKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound != null && outbound.booleanValue()) {
            try {
                Document document = context.getMessage().getSOAPPart();

                WSSecHeader header = new WSSecHeader();
                header.insertSecurityHeader(document);

                WSSecSignature signature = new WSSecSignature();
                signature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
                signature.prepare(document, crypto, header);

                signature.appendBSTElementToHeader(header);
                signature.appendToHeader(header);
                signature.computeSignature();
            } catch (WSSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        if (log.isDebugEnabled()) {
            try {
                String envelope = transform(context.getMessage().getSOAPPart());
                log.debug(envelope);
            } catch (TransformerException e) {
                log.warn(e.getMessage());
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        if (log.isDebugEnabled()) {
            try {
                String envelope = transform(context.getMessage().getSOAPPart());
                log.debug(envelope);
            } catch (TransformerException e) {
                log.warn(e.getMessage());
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(MessageContext context) {
    }

    protected String transform(Node node) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

}
