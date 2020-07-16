package es.caib.gdib.schedulejobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.util.encoders.Base64;


import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.gdib.rm.utils.ExportUtils;
import es.caib.gdib.rm.utils.ImportUtils;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.FilterPlaceholderProperties;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.InputStreamDataSource;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;
import es.gob.afirma.integraFacade.IntegraFacade;
import es.gob.afirma.integraFacade.IntegraFacadeWSDSS;
import es.gob.afirma.integraFacade.pojo.Repository;
import es.gob.afirma.integraFacade.pojo.VerifyCertificateRequest;
import es.gob.afirma.integraFacade.pojo.VerifyCertificateResponse;
import es.gob.afirma.wsServiceInvoker.Afirma5ServiceInvokerFacade;

public class UpgradeIndex {
	private static final Logger LOGGER = Logger.getLogger(UpgradeIndex.class);
	public static final String STRING_SPLIT = ",";

	private Date jobRunDate;

	private FilterPlaceholderProperties upgradeIndexPropertiesFilter;
	private NodeService nodeService;
	private SubTypeDocUtil subTypeDocUtil;
	private SearchService searchService;
	private FileFolderService fileFolderService;
	private SignatureService signatureService;
	private ImportUtils importUtils;
	private ExportUtils exportUtils;
	
	private boolean active; // Whether cron is active or not
	private String lucene_query;// Lucene query to obtain indexes
	private String tmpDir; // Tmp dir to export Indexes and upgrade Signature

	@Autowired
	private GdibUtils utils;

	/**
	 * M�todo que dispara el m�todo en base al par�metro upgrade.active del fichero schedule-job-upgrade.properties
	 */
	public void execute() {
		// compruebo si el job esta activo, por la property "upgrade.active"
		if (active) {
			LOGGER.info("Lanzando el cronjob - UpgradeIndex Job");
			jobRunDate = new Date();
			try {
				run();
			} catch (GdibException e) {
				LOGGER.error("Ha ocurrido un error. " + e.getMessage());
			}
			LOGGER.info("El cronjob ha finalizado");
		} 
	}

	/**
	 * M�todo que ejecuta el trabajo de upgradeo de �ndices
	 * 
	 * @throws GdibException wrapper para propagar la excepci�n
	 */
	public void run() throws GdibException {
		List<NodeRef> upgradeDocs;
		NodeRef nodeTmp = utils.idToNodeRef(tmpDir);
		FileInfo tmpParentFileInfo = fileFolderService.create(nodeTmp, "tmpIndexFolder", ContentModel.TYPE_FOLDER);
		NodeRef tmpFolder = tmpParentFileInfo.getNodeRef();
		//Si ocurre alg�n error ( la carpeta solo debe crearse para llevar a cabo el trabajo temporal)
		if(tmpFolder == null)
			throw new GdibException("Ocurri� un error creando el directorio temporal");
		
		upgradeDocs = null;

		try {
			LOGGER.debug("Obtiendo los �ndices");
			// Get Index To Apply TSA upgrade
			upgradeDocs = getDocumentsToUpgrade();
		} catch (GdibException e) {
			LOGGER.error("Error obteniendo los documentos. Excepcion : " + e.getMessage());
			throw new GdibException("Error obteniendo los documentos. Excepcion : " + e.getMessage());
		}

		if (!CollectionUtils.isEmpty(upgradeDocs)) {
			for (NodeRef doc : upgradeDocs) {

				// DOC ES �NDICE ORIGINAL
				try {
					LOGGER.debug("Upgradeando el sello el documento: " + doc.getId());

					ChildAssociationRef rmParentChild = nodeService.getPrimaryParent(doc);
					if (rmParentChild == null)
						continue;
					NodeRef rmParent = rmParentChild.getParentRef();
					Set<QName> toSearch = new HashSet<>();
					toSearch.add(ConstantUtils.TYPE_FILE_INDEX_QNAME);
					//Obtengo lista de �ndices antiguos que dejar�n de ser v�lidos al completar el proceso
					List<ChildAssociationRef> listaHijos  = nodeService.getChildAssocs(rmParent, toSearch);
					// copyToTemp
					// Mover a carpeta temporal y devuelvo NodeRef de carpeta temporal
					NodeRef tmpExpFolder = this.moveToTmp(rmParent,tmpParentFileInfo.getNodeRef());
					// Actualizo firma y devuelvo NodeRefs de indices resellados
					List<NodeRef> listIndexes= this.upgradeTSASeal(tmpExpFolder);
					//Actualizo metadatos de los antiguos y nuevos
                    

					for(ChildAssociationRef oldIndex : listaHijos)
					{
						nodeService.setProperty(oldIndex.getChildRef(),ConstantUtils.PROP_INDEX_CERT_DATE_QNAME, ISO8601DateFormat.format(new Date()));
						nodeService.setProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME, "NO");
					}
					for(NodeRef newIndex : listIndexes)
					{
						nodeService.setProperty(newIndex,ConstantUtils.PROP_INDEX_CERT_DATE_QNAME, ISO8601DateFormat.format(new Date()));
						nodeService.setProperty(newIndex, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
						nodeService.setProperty(newIndex, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME, 
								nodeService.getProperty(rmParent, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME));
						nodeService.setProperty(newIndex, ConstantUtils.PROP_FASE_ARCHIVO_QNAME, 
								nodeService.getProperty(rmParent, ConstantUtils.PROP_FASE_ARCHIVO_QNAME));
					
					}
					
					// Return to original and change name
					returnToRMExp(listIndexes,rmParent);
					LOGGER.debug("After Return TO RMEXP");
					
					//Delete tmp folder					
					nodeService.deleteNode(tmpParentFileInfo.getNodeRef());
					//Add Metadata
					
					active = false;
					break;
				} catch (GdibException e) {
					LOGGER.error(
							"Error realizando el ugradeo del indice (" + doc.getId() + "). " + e.getMessage());
					active = false;
					break;
				} catch (Exception e) {
					LOGGER.error(
							"Error realizando el upgradeo del indice (" + doc.getId() + "). " + e.getMessage());
					active = false;
					break;
				}
			}
		}
		//Finalizaci�n del trabajo de upgradeo de �ndices
		LOGGER.debug("Upgrade Finalizado");
	}

	/**
	 * M�todo que ejecuta la operaci�n de upgradeo/resellado de la firma de un
	 * �ndice
	 * 
	 * @param indexIdentifier Nodo del expediente en el espacio temporal
	 * @throws GdibException wrapper para propagar la excepci�n
	 * @throws IOException 
	 * @throws ContentIOException 
	 */
	private List<NodeRef> upgradeTSASeal(NodeRef tempParentRef) throws GdibException, ContentIOException, IOException {

		// Check Cert
		List<NodeRef> listaIndices = new ArrayList<NodeRef>();
		LOGGER.debug("Actualizando firmas de los indices de la carpeta temporal :" + tempParentRef.getId());
		Set<QName> toSearch = new HashSet<>();
		toSearch.add(ConstantUtils.TYPE_FILE_INDEX_QNAME);
		List<ChildAssociationRef> listaHijos  = nodeService.getChildAssocs(tempParentRef, toSearch);
		if(!listaHijos.isEmpty())
		{	
			for(ChildAssociationRef it : listaHijos)
			{
				if("NO".equals((String) nodeService.getProperty(it.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME)  ) )
					continue;
				//Call Upgrade Signature
				LOGGER.debug("Actualizando firma del nodo : "+it.getChildRef().getId());
				//retrieve answer data (check serial)
				
    			byte[] signature = utils.getByteArrayFromHandler(utils.getDataHandler(it.getChildRef(), ContentModel.PROP_CONTENT));
    			//signatureService.verifySignature(null, signature);
    			//byte[] newSignature  = signatureService.signXadesDocument(signature, SignatureFormat.XAdES_A, null, null);
    			byte[] newSignature = signatureService.upgradeSignature(signature, SignatureFormat.XAdES_A);
    			//Afirma5ServiceInvokerFacade.getInstance().invokeService(newSignature, "validate", method, serviceProperties)
    			try {
    				Document toParseXml = obtenerDocumentDeByte(newSignature);
        			toParseXml.getDocumentElement().normalize();
        			//LOGGER.debug(parseTimeStamp(toParseXml));
        			
        			String certValue = utils.parseTimeStampASN1(toParseXml);
        			nodeService.setProperty(it.getChildRef(),ConstantUtils.PROP_INDEX_CERT_QNAME, certValue);
    			}catch(Exception e)
    			{
    				
    				LOGGER.debug("GOt Exception While reading XML = "+e.getMessage());
        			active = false;

    				throw new GdibException(e.getMessage());
    			}
    			
    			
    	        
    	        
    			active = false;
    			//set new Signature
    			//signatureService.verifySignature(null, newSignature);
    	    	DataHandler dh = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(newSignature)));
    	    	
                utils.setUnsecureDataHandler(it.getChildRef(), ConstantUtils.PROP_CONTENT, dh, MimetypeMap.MIMETYPE_BINARY);
                LOGGER.debug("Firma actualizada");
                
                
                listaIndices.add(it.getChildRef());
                
    			//break;
			}
		}
		// Upgrade Signature / call SignXades Document

		return listaIndices;
	}

	/**
	 * M�todo que ejecuta la b�squeda de aquellos �ndices que deban ser resellados.
	 * La consulta se especifica en el fichero schedule-job-upgrade.properties, en
	 * la property lucene_query
	 * 
	 * @return List<NodeRef> Lista con aquellos �ndices que deban ser resellados
	 * @throws GdibException Wrapper de cualquier excepci�n para propagar
	 */
	private List<NodeRef> getDocumentsToUpgrade() throws GdibException {
		List<NodeRef> result = null;
		List<SubTypeDocInfo> resealInfo = subTypeDocUtil.getReselladoInfo();
		// String luceneQuery = upgradeIndexPropertiesFilter.getProperty(typeDoc,
		// LUCENE_QUERY_TEMPLATE);
		final SearchParameters params = new SearchParameters();
		params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		params.setLanguage(SearchService.LANGUAGE_LUCENE);
		int queryResultLength = 0;
		// +(TYPE:"gdib:indiceExpediente") AND @eni\\:cod_clasificacion:"%s"
		for (SubTypeDocInfo info : resealInfo) {
			final StringBuilder query = new StringBuilder(400);

			Formatter formatterDocumento = new Formatter(query);

			formatterDocumento.format(lucene_query, info.getDocumentarySeries()).toString();

			query.trimToSize();
			LOGGER.debug("Query Upgrading Indexes: " + lucene_query);

			params.setQuery(lucene_query);

			// query.append(luceneQuery);
			query.trimToSize();
			LOGGER.debug("Query: " + lucene_query);

			// params.setQuery(query.toString());

			ResultSet resultSet = null;
			try {
				resultSet = searchService.query(params);
				if (resultSet != null && resultSet.length() > 0) {
					queryResultLength += resultSet.length();
					result = resultSet.getNodeRefs();
					LOGGER.debug("Found " + resultSet.length() + " total indexes from cod serie:"
							+ info.getDocumentarySeries() + " to upgradeSignature");

				}
				LOGGER.info("N�mero de documentos obtenidos al ejecutar la consulta Lucene de upgradear: "
						+ queryResultLength + ".");
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
			break;

		}

		return result;
	}
	
	/**
	 * M�todo para migrar un expediente a una carpeta temporal.
	 * @param original Nodo del expediente original a copiar al directorio temporal
	 * @param tmpFolder Referencia al nodo raiz de la carpetatemporal
	 * @return
	 * @throws GdibException
	 */
	private NodeRef moveToTmp(NodeRef original,NodeRef tmpFolder) throws GdibException
    {
    	NodeRef res = null;

    	try {
			//FileInfo resFI = fileFolderService.copy(original, tmpFolder, null);
			//res = resFI.getNodeRef();
    		res = importUtils.importExpedientWithTarget(original, tmpFolder);
		} catch (Exception e) {
			LOGGER.debug("UpgradeIndexJob -- moveToTmp:"+e.getMessage());
			throw new GdibException(e.getMessage());
		}
    	return res;
    }
	
	/**
	 * M�todo para migrar una lista de NodeRefs(�ndices) a un expediente en el RM, previamente obtenidos del mismo, para llevar a cabo el trabajo
	 * programado UpgradeIndex.
	 * @param List<NodeRef> updatedIndexes Lista contenedora de los NodeRef de los �ndices a exportar.
	 * @param NodeRef parent NodeRef destino al que devolver los �ndices
	 * @throws GdibException
	 */
	private void returnToRMExp(List<NodeRef> updatedIndexes, NodeRef parent) throws GdibException{
		
		try {
			for(NodeRef it : updatedIndexes)
			{
				changeIndexName(it);
				if(exportUtils == null)
					LOGGER.debug("EXPORT UTILS IS NULLLLL");
				exportUtils.createRMRecord(it, parent);
			}
			
		}catch(Exception e)
		{
			for(StackTraceElement err : e.getStackTrace())
				LOGGER.error(err.getFileName() + " line "+err.getLineNumber());
			LOGGER.debug("Excepcion devolviendo indices al RM: "+e.getMessage());
			throw new GdibException(e.getMessage());
			
		}	
	}
	/**
	 * M�todo privado para cambiar el nombre de un �ndice. Al crearse los �ndices, se les concatena
	 * al final del nombre la fecha en formato YYYYMMDDHHmm , por lo que la cambiamos y ponemos la fecha con el mismo formato
	 * del d�a de ejecuci�n.
	 * @param indexToChange NodeRef del �ndice a cambiar el nombre
	 */
	private void changeIndexName(NodeRef indexToChange)
	{
		//Change property CM:NAME
		String nodeRefName = (String) nodeService.getProperty(indexToChange, ConstantUtils.PROP_NAME);
		int posExtension = nodeRefName.lastIndexOf(".");
		StringBuilder sb= new StringBuilder();
		sb.append(nodeRefName.substring(0,posExtension-12));
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
		String strDate = dateFormat.format(date);
		sb.append(strDate);
		sb.append(".xml");
		LOGGER.debug(sb.toString());
		nodeService.setProperty(indexToChange, ConstantUtils.PROP_NAME, sb.toString());
		
	}
	/**
	 * M�todo auxiliar para parsear un documento XML en forma de byte Array
	 * @param documentoXml byte[] que contiene el el resultado de firma
	 * @return Document contenedor del resultado de parsear el xml
	 * @throws Exception en caso de que no sea un XML.
	 */
	private Document obtenerDocumentDeByte(byte[] documentoXml) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}

	/**
	 * M�todo que devuelve una cadena del identificador del CN del OCSP provider
	 * @param signature XML resultado de firma
	 * @return Identificador por nombre del certificado usado por el servicio OCSP
	 * @throws IOException
	 */
	private String parseTimeStamp(Document signature) throws IOException{

        //NodeList completeCertificateRefs = signature.getElementsByTagName("xades:CompleteCertificateRefs");
        NodeList OCSPRefs = signature.getElementsByTagName("xades:OCSPRefs");
        if(OCSPRefs.getLength() != 0)
        {
        	Node OCSPREfsList = OCSPRefs.item(0);
        	if(OCSPREfsList != null)
        	{
        		if(OCSPREfsList.hasChildNodes())
        		{
        			Node lastOCSPCert = OCSPREfsList.getLastChild();
        			if(lastOCSPCert  != null)
        			{
        				Element lastOCSPCertEle= (Element) lastOCSPCert ;
        				Node OCSPIdentifier= lastOCSPCertEle.getElementsByTagName("xades:OCSPIdentifier").item(0);
                		if(OCSPIdentifier != null)
                		{	
                			Element OCSPIdentifierElement = (Element)OCSPIdentifier;
                			Node OCSPResponderID = OCSPIdentifierElement.getElementsByTagName("xades:ResponderID").item(0);
                			if(OCSPResponderID != null)
                			{
                				Element identifier = (Element) OCSPResponderID;
            	                Node identifierTag = identifier.getElementsByTagName("xades:ByName").item(0);
            	                if(identifierTag != null)
            	                {	
            	                	String res = identifierTag.getTextContent();
            	                	LOGGER.debug("OCSP Cert Identifier by name : "+res);
            	                }
                			}
                		}
        			}
        		}
        	}
        }
		return "PARSETIMESTAMP";
			
		
		
	}
		public void setJobRunDate(Date jobRunDate) {
		this.jobRunDate = jobRunDate;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setUpgradeIndexPropertiesFilter(FilterPlaceholderProperties upgradeIndexPropertiesFilter) {
		this.upgradeIndexPropertiesFilter = upgradeIndexPropertiesFilter;
	}

	public void setSubTypeDocUtil(SubTypeDocUtil subTypeDocUtil) {
		this.subTypeDocUtil = subTypeDocUtil;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setLucene_query(String lucene_query) {
		this.lucene_query = lucene_query;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public void setImportUtils(ImportUtils importUtils) {
		this.importUtils = importUtils;
	}

	public void setSignatureService(SignatureService signatureService) {
		this.signatureService = signatureService;
	}

	public void setExportUtils(ExportUtils exportUtils) {
		this.exportUtils = exportUtils;
	}

}
