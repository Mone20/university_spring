<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <body>
                <h2>Export file</h2>
                <table>
                    <tr>
                        <th>Degree</th>
                    </tr>
                    <xsl:for-each select="Degrees/Degree">
                        <tr>
                            <td><xsl:value-of select="degree"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet> 