<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:for-each select="/StringTableFile/Entries/Entry">
			<xsl:value-of select="DefaultText" />
			<xsl:text>&#10;</xsl:text>
			<xsl:value-of select="FemaleText" />
			<xsl:text>&#10;</xsl:text>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
