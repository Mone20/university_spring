<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
   <html>
   <body>
   <h2>Export file</h2>
   <table>
   <tr>
       <th>LastName</th>
       <th>FirstName</th>
       <th>MiddleName</th>
       <th>BirthDate</th>
       <th>PositionId</th>
       <th>DegreeId</th>
       <th>ParentId</th>
       <th>Salery</th>
     </tr>
     <xsl:for-each select="Workers/Worker">
     <tr>
       <td><xsl:value-of select="lastName"/></td>
       <td><xsl:value-of select="firstName"/></td>
       <td><xsl:value-of select="middleName"/></td>
       <td><xsl:value-of select="birthDate"/></td>
       <td><xsl:value-of select="positionId"/></td>
       <td><xsl:value-of select="degreeId"/></td>
       <td><xsl:value-of select="parentId"/></td>
         <td><xsl:value-of select="salery"/></td>
     </tr>
     </xsl:for-each>
   </table>
   </body>
   </html>
</xsl:template>

</xsl:stylesheet> 