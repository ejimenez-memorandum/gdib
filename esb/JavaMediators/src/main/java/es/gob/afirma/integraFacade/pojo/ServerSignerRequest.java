// Copyright (C) 2012-13 MINHAP, Gobierno de España
// This program is licensed and may be used, modified and redistributed under the terms
// of the European Public License (EUPL), either version 1.1 or (at your
// option) any later version as soon as they are approved by the European Commission.
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied. See the License for the specific language governing permissions and
// more details.
// You should have received a copy of the EUPL1.1 license
// along with this program; if not, you may find it at
// http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

/**
 * <b>File:</b><p>es.gob.afirma.integraWSFacade.ServerSignerRequest.java.</p>
 * <b>Description:</b><p>Class that represents the request for the server signature service.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>13/11/2014.</p>
 * @author Gobierno de España.
 * @version 1.0, 13/11/2014.
 */
package es.gob.afirma.integraFacade.pojo;

import java.io.Serializable;

/**
 * <p>Class that represents the request for the server signature service.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 13/11/2014.
 */
public class ServerSignerRequest implements Serializable {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = -3340893948911425856L;

	/**
	 * Attribute that represents document to be signed.
	 */
	private byte[ ] document;

	/**
	 * Attribute that represents document identifier to be signed.
	 */
	private String documentId;

	/**
	 * Attribute that represents the location of a document in a document manager or repository.
	 */
	private Repository documentRepository;

	/**
	 * Attribute that represents application identifier.
	 */
	private String applicationId;

	/**
	 * Attribute that contains the ID of the key to be used for generating a signature delegate.
	 */
	private String keySelector;

	/**
	 * Attribute that represents signature format.
	 */
	private SignatureFormatEnum signatureFormat;

	/**
	 * Attribute that indicates the hash algorithm to be used in the calculation of the signature.
	 */
	private HashAlgorithmEnum hashAlgorithm;

	/**
	 * Attribute that indicates signature mode to generate.
	 */
	private String xmlSignatureMode;

	/**
	 * Attribute that represents the identifier of the signature policy.
	 */
	private String signaturePolicyIdentifier;

	/**
	 * Attribute that indicates to server that is not going to save a possible grace period.
	 */
	private boolean ignoreGracePeriod;

	/**
	 * Attribute that contains the hash of the original data for verification with respect to the data included in the signature.
	 */
	private DocumentHash documentHash;

	/**
	 * Constructor method for the class ServerSignerRequest.java.
	 */
	public ServerSignerRequest() {
	}

	/**
	 * Gets the value of the attribute {@link #document}.
	 * @return the value of the attribute {@link #document}.
	 */
	public final byte[ ] getDocument() {
		return document;
	}

	/**
	 * Sets the value of the attribute {@link #document}.
	 * @param documentParam The value for the attribute {@link #document}.
	 */
	public final void setDocument(byte[ ] documentParam) {
		if (documentParam != null) {
			this.document = documentParam.clone();
		}
	}

	/**
	 * Gets the value of the attribute {@link #documentId}.
	 * @return the value of the attribute {@link #documentId}.
	 */
	public final String getDocumentId() {
		return documentId;
	}

	/**
	 * Sets the value of the attribute {@link #documentId}.
	 * @param documentIdParam The value for the attribute {@link #documentId}.
	 */
	public final void setDocumentId(String documentIdParam) {
		this.documentId = documentIdParam;
	}

	/**
	 * Gets the value of the attribute {@link #documentRepository}.
	 * @return the value of the attribute {@link #documentRepository}.
	 */
	public final Repository getDocumentRepository() {
		return documentRepository;
	}

	/**
	 * Sets the value of the attribute {@link #documentRepository}.
	 * @param documentRepositoryParam The value for the attribute {@link #documentRepository}.
	 */
	public final void setDocumentRepository(Repository documentRepositoryParam) {
		this.documentRepository = documentRepositoryParam;
	}

	/**
	 * Gets the value of the attribute {@link #applicationId}.
	 * @return the value of the attribute {@link #applicationId}.
	 */
	public final String getApplicationId() {
		return applicationId;
	}

	/**
	 * Sets the value of the attribute {@link #applicationId}.
	 * @param applicationIdParam The value for the attribute {@link #applicationId}.
	 */
	public final void setApplicationId(String applicationIdParam) {
		this.applicationId = applicationIdParam;
	}

	/**
	 * Gets the value of the attribute {@link #keySelector}.
	 * @return the value of the attribute {@link #keySelector}.
	 */
	public final String getKeySelector() {
		return keySelector;
	}

	/**
	 * Sets the value of the attribute {@link #keySelector}.
	 * @param keySelectorParam The value for the attribute {@link #keySelector}.
	 */
	public final void setKeySelector(String keySelectorParam) {
		this.keySelector = keySelectorParam;
	}

	/**
	 * Gets the value of the attribute {@link #signatureFormat}.
	 * @return the value of the attribute {@link #signatureFormat}.
	 */
	public final SignatureFormatEnum getSignatureFormat() {
		return signatureFormat;
	}

	/**
	 * Sets the value of the attribute {@link #signatureFormat}.
	 * @param signatureFormatParam The value for the attribute {@link #signatureFormat}.
	 */
	public final void setSignatureFormat(SignatureFormatEnum signatureFormatParam) {
		this.signatureFormat = signatureFormatParam;
	}

	/**
	 * Gets the value of the attribute {@link #hashAlgorithm}.
	 * @return the value of the attribute {@link #hashAlgorithm}.
	 */
	public final HashAlgorithmEnum getHashAlgorithm() {
		return hashAlgorithm;
	}

	/**
	 * Sets the value of the attribute {@link #hashAlgorithm}.
	 * @param hashAlgorithmParam The value for the attribute {@link #hashAlgorithm}.
	 */
	public final void setHashAlgorithm(HashAlgorithmEnum hashAlgorithmParam) {
		this.hashAlgorithm = hashAlgorithmParam;
	}

	/**
	 * Gets the value of the attribute {@link #xmlSignatureMode}.
	 * @return the value of the attribute {@link #xmlSignatureMode}.
	 */
	public final String getXmlSignatureMode() {
		return xmlSignatureMode;
	}

	/**
	 * Sets the value of the attribute {@link #xmlSignatureMode}.
	 * @param xmlSignatureModeParam The value for the attribute {@link #xmlSignatureMode}.
	 */
	public final void setXmlSignatureMode(String xmlSignatureModeParam) {
		this.xmlSignatureMode = xmlSignatureModeParam;
	}

	/**
	 * Gets the value of the attribute {@link #signaturePolicyIdentifier}.
	 * @return the value of the attribute {@link #signaturePolicyIdentifier}.
	 */
	public final String getSignaturePolicyIdentifier() {
		return signaturePolicyIdentifier;
	}

	/**
	 * Sets the value of the attribute {@link #signaturePolicyIdentifier}.
	 * @param signaturePolicyIdentifierParam The value for the attribute {@link #signaturePolicyIdentifier}.
	 */
	public final void setSignaturePolicyIdentifier(String signaturePolicyIdentifierParam) {
		this.signaturePolicyIdentifier = signaturePolicyIdentifierParam;
	}

	/**
	 * Gets the value of the attribute {@link #ignoreGracePeriod}.
	 * @return the value of the attribute {@link #ignoreGracePeriod}.
	 */
	public final boolean isIgnoreGracePeriod() {
		return ignoreGracePeriod;
	}

	/**
	 * Sets the value of the attribute {@link #ignoreGracePeriod}.
	 * @param ignoreGracePeriodParam The value for the attribute {@link #ignoreGracePeriod}.
	 */
	public final void setIgnoreGracePeriod(boolean ignoreGracePeriodParam) {
		this.ignoreGracePeriod = ignoreGracePeriodParam;
	}

	/**
	 * Gets the value of the attribute {@link #documentHash}.
	 * @return the value of the attribute {@link #documentHash}.
	 */
	public final DocumentHash getDocumentHash() {
		return documentHash;
	}

	/**
	 * Sets the value of the attribute {@link #documentHash}.
	 * @param documentHashParam The value for the attribute {@link #documentHash}.
	 */
	public final void setDocumentHash(DocumentHash documentHashParam) {
		this.documentHash = documentHashParam;
	}

}
