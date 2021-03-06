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
 * <b>File:</b><p>es.gob.afirma.signature.SignatureConstants.java.</p>
 * <b>Description:</b><p>Class that defines constants related to processes with signatures.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>28/06/2011.</p>
 * @author Gobierno de España.
 * @version 1.0, 28/06/2011.
 */
package es.gob.afirma.signature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.dsig.DigestMethod;

import es.gob.afirma.utils.CryptoUtil;

/**
 * <p>Class that defines constants related to processes with signatures.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 28/06/2011.
 */
public final class SignatureConstants {

	/**
	 * Constructor method for the class SignatureConstants.java.
	 */
	private SignatureConstants() {
	}

	/**
	 * Constant attribute that identifies the UTF-8 encoding.
	 */
	public static final String UTF8_ENCODING = "UTF-8";

	/**
	 * Constant attribute that represents CAdES signature format.
	 */
	public static final String SIGN_FORMAT_CADES = "CAdES";

	/**
	 * Constant attribute that identifies the XAdES detached signature.
	 */
	public static final String SIGN_FORMAT_XADES_DETACHED = "XAdES Detached";

	/**
	 * Constant attribute that identifies the XAdES externally detached signature.
	 */
	public static final String SIGN_FORMAT_XADES_EXTERNALLY_DETACHED = "XAdES Externally Detached";

	/**
	 * Constant attribute that identifies the XAdES enveloped signature.
	 */
	public static final String SIGN_FORMAT_XADES_ENVELOPED = "XAdES Enveloped";

	/**
	 * Constant attribute that identifies the XAdES enveloping signature.
	 */
	public static final String SIGN_FORMAT_XADES_ENVELOPING = "XAdES Enveloping";

	/**
	 * Constant attribute that represents PAdES signature format.
	 */
	public static final String SIGN_FORMAT_PADES = "PAdES";

	/**
	 * Constant attribute that represents XAdES signature format.
	 */
	public static final String SIGN_FORMAT_XADES = "XAdES";

	/**
	 * Constant attribute that represents explicit signature mode.
	 */
	public static final String SIGN_MODE_EXPLICIT = "explicit mode";

	/**
	 * Constant attribute that represents implicit signature mode.
	 */
	public static final String SIGN_MODE_IMPLICIT = "implicit mode";

	/**
	 * Constant attribute that represents default signature mode.
	 */
	public static final String DEFAULT_SIGN_MODE = SIGN_MODE_EXPLICIT;

	/**
	 * Constant attribute that represents supported XAdES signature formats.
	 */
	public static final Set<String> SUPPORTED_XADES_SIGN_FORMAT = new HashSet<String>() {

		/**
		 * Class serial version.
		 */
		private static final long serialVersionUID = -3526322449162410326L;

		{
			add(SIGN_FORMAT_XADES_ENVELOPED);
			add(SIGN_FORMAT_XADES_ENVELOPING);
			add(SIGN_FORMAT_XADES_DETACHED);
			add(SIGN_FORMAT_XADES_EXTERNALLY_DETACHED);
		}
	};

	/**
	 * Constant attribute that represents SHA1withRSA algorithm.
	 */
	public static final String SIGN_ALGORITHM_SHA1WITHRSA = "SHA1withRSA";

	/**
	 * Constant attribute that represents SHA256withRSA algorithm.
	 */
	public static final String SIGN_ALGORITHM_SHA256WITHRSA = "SHA256withRSA";

	/**
	 * Constant attribute that represents SHA384withRSA algorithm.
	 */
	public static final String SIGN_ALGORITHM_SHA384WITHRSA = "SHA384withRSA";

	/**
	 * Constant attribute that represents SHA512withRSA algorithm.
	 */
	public static final String SIGN_ALGORITHM_SHA512WITHRSA = "SHA512withRSA";

	/**
	 * Constant attribute that represents the URI of allowed signature algorithms to use with XADES signatures.
	 */
	public static final Map<String, String> SIGN_ALGORITHM_URI = new HashMap<String, String>() {

		/**
		 * Class serial version.
		 */
		private static final long serialVersionUID = -3091842386857850550L;

		{
			put(SIGN_ALGORITHM_SHA1WITHRSA, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
			put(SIGN_ALGORITHM_SHA256WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
			put(SIGN_ALGORITHM_SHA384WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
			put(SIGN_ALGORITHM_SHA512WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
		}
	};

	/**
	 * Constant attribute that represents the URI of allowed hash algorithms to use with XADES signatures.
	 */
	public static final Map<String, String> DIGEST_METHOD_ALGORITHMS_XADES = new HashMap<String, String>() {

		/**
		 * Class serial version.
		 */
		private static final long serialVersionUID = -3091842386857850550L;

		{
			put(SIGN_ALGORITHM_SHA1WITHRSA, DigestMethod.SHA1);
			put(SIGN_ALGORITHM_SHA256WITHRSA, DigestMethod.SHA256);
			put(SIGN_ALGORITHM_SHA384WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#sha384");
			put(SIGN_ALGORITHM_SHA512WITHRSA, DigestMethod.SHA512);
		}
	};

	/**
	 * Constant attribute that represents the allowed signature algorithms to use with CADES signatures.
	 */
	public static final Map<String, String> SIGN_ALGORITHMS_SUPPORT_CADES = new HashMap<String, String>() {

		/**
		 * Class serial version.
		 */
		private static final long serialVersionUID = -3091842386857850550L;

		{
			put(SIGN_ALGORITHM_SHA1WITHRSA, CryptoUtil.HASH_ALGORITHM_SHA1);
			put(SIGN_ALGORITHM_SHA256WITHRSA, CryptoUtil.HASH_ALGORITHM_SHA256);
			put(SIGN_ALGORITHM_SHA384WITHRSA, CryptoUtil.HASH_ALGORITHM_SHA384);
			put(SIGN_ALGORITHM_SHA512WITHRSA, CryptoUtil.HASH_ALGORITHM_SHA512);
		}
	};

	/**
	 * Constant attribute that represents property name used in Manifest references list (external references in a detached signature).
	 */
	public static final String MF_REFERENCES_PROPERTYNAME = "Manifest-References";

	/**
	 *  Constant attribute that represents the string to identify the value for the key <i>SubFilter</i> of the signature dictionary for a
	 *  PAdES Enhanced signature.
	 */
	public static final String PADES_SUBFILTER_VALUE = "ETSI.CAdES.detached";

	/**
	 * Constant attribute that identifies a Certified PDF signature.
	 */
	public static final String PDF_CERTIFIED = "CERTIFIED_NO_CHANGES_ALLOWED";

	/**
	 * Constant attribute that identifies an Approval PDF signature.
	 */
	public static final String PDF_APPROVAL = "NOT_CERTIFIED";
}
