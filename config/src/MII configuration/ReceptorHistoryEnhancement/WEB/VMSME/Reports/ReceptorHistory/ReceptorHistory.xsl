<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" media-type="text/xml" encoding="UTF-8"/>
	<xsl:template match="/">
			<rows>
				<xsl:for-each select="Rowsets/Rowset/Row"> 
					<row id="A{position()}" >
						<xsl:for-each select="*">
							<cell><xsl:value-of select="."/></cell>
						</xsl:for-each>
					</row>
				</xsl:for-each>
			</rows>
	</xsl:template>
</xsl:stylesheet>