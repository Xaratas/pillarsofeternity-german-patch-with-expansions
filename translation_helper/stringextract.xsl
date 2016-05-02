<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:for-each select="collection(concat('file:///home/xar/gitrepos/poe_translation/', '?select=*.stringtable;recurse=yes'))">
		<xsl:variable name="newPath"><xsl:value-of select="replace(base-uri(), 'poe_translation', 'poe_translation_bare')" /></xsl:variable>
		<xsl:result-document href="{$newPath}">
			<xsl:for-each select="/StringTableFile/Entries/Entry">
				<xsl:value-of select="DefaultText" />
				<xsl:text>&#10;</xsl:text>
				<xsl:value-of select="FemaleText" />
				<xsl:text>&#10;</xsl:text>
			</xsl:for-each>
		</xsl:result-document>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
