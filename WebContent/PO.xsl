<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
	<head>
		<title>Purchase Order Confirmation</title>
	</head>
  <body>

  <h4> 	 Name: <xsl:value-of select="order/customer/name"/>
	<br/>Account: <xsl:value-of select="order/customer/@account"/>
	<br/>Date Submitted: <xsl:value-of select="order/@submitted"/>
  </h4> 

  <hr />

   <table border="1" cellpadding="3"><tbody>
   	<tr>
   	<th>Number</th>
   	<th>Name</th>
   	<th>Price</th>
   	<th>Quantity</th>
   	<th>Extended</th>
   	</tr>

   	<xsl:for-each select="order/items/item">
    <tr>
      	<td><xsl:value-of select="@number"/></td>
        <td><xsl:value-of select="name"/></td>
        <td>$<xsl:value-of select="price"/></td>
        <td><xsl:value-of select="quantity"/></td>
        <td>$<xsl:value-of select="extended"/></td>
    </tr>
	</xsl:for-each>

	</tbody></table>

	<hr />
    <h4> Total: $<xsl:value-of select="order/total"/>
	  <br/> Shipping: $<xsl:value-of select="order/shipping"/>
	  <br/> Taxes: $<xsl:value-of select="order/HST"/>
	  <br/> Grand Total: $<xsl:value-of select="order/grandTotal"/>
	</h4>

  </body>
  </html>
</xsl:template>



</xsl:stylesheet>