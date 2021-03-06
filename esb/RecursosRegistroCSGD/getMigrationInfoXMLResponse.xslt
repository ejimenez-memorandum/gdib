<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:variable name="resultadoCorrecto" select="fn:boolean(//ws:getMigrationInfoResponse/ws:result/ws:valcertSign/text())"/>	 
     <xsl:template match="/">
         <csgd:getMigrationInfoResult>
             <csgd:result>
                 <xsl:choose>
                     <xsl:when test="$resultadoCorrecto">
                         <csgd:code>COD_000</csgd:code>
                         <csgd:description>Petición realizada correctamente.</csgd:description>
                     </xsl:when>
                     <xsl:otherwise>
                         <csgd:code>COD_001</csgd:code>
                         <csgd:description>Petición realizada correctamente, pero no fue posible incluir los contenidos de migración del documento.</csgd:description>
                     </xsl:otherwise>
                 </xsl:choose>
             </csgd:result>
             <xsl:if test="$resultadoCorrecto">
	             <csgd:resParam>
   					<csgd:binaryContents>
   						<csgd:binaryType>VALCERT_SIGNATURE</csgd:binaryType>
   						<csgd:content><xsl:value-of select="//ws:result/ws:valcertSign/text()"/></csgd:content>     					
   					</csgd:binaryContents>

   					<csgd:binaryContents>
   						<csgd:binaryType>MIGRATION_ZIP</csgd:binaryType>
   						<csgd:content><xsl:value-of select="//ws:result/ws:zipContent/text()"/></csgd:content>     					
   					</csgd:binaryContents>
	             </csgd:resParam>
             </xsl:if>
         </csgd:getMigrationInfoResult>
     </xsl:template>
 </xsl:stylesheet>