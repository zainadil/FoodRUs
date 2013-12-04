
function updateQuanity(itemID) {
	var qty = document.getElementById(itemID).value;

	if (isNaN(qty) || (qty > 999)) {
		if(qty > 999)
			alert ("Quantity should be less than 999");
		else 
			alert("Invalid Quantity");
		return false;
	} else {
		document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	if(isNaN(qty) || qty > 999){
		if(qty > 999)
			alert ("Quantity should be less than 999");
		else alert("Invalid Quantity");
			var id = "q"+itemID;
			document.getElementById(id).style.background="red";
			return false;
	} else{
		document.getElementById("addedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

function validateExpressCheckout(){
	
	var rv = true;
	
	var itemNumber = document.getElementById("itemNumber").value;
	var itemQuantity = document.getElementById("itemQuantity").value;
	
	var letter = /[a-zA-Z]/; 
    var number = /[0-9]/;
    var valid = number.test(itemNumber) && letter.test(itemNumber);
     if(!valid){
    	 alert("Invalid Item-ID");
    	 rv = false;
     }
	if(isNaN(itemQuantity) || itemQuantity >999){
		if(itemQuantity > 999)
			alert ("Quantity should be less than 999");
		else alert("Invalid Quantity");
		rv =  false;
	}	
	
	return rv;
}

function searchValidation(){
	var rv = true;
	var search = document.getElementById("search").value;
	
	var letter = /[a-zA-Z]/; 
    var number = /[0-9]/;
    var valid = number.test(search) || letter.test(search);
    if(!valid){
    	rv = false;
    	alert("Invalid Search Query!");
    	
    }
    return rv;
}

// Hack, do something about this later.
function closeNotificatonDiv(){
	document.getElementById("addtoCartNotification").style.display=" none";
}

window.setTimeout( closeNotificatonDiv, 2000 );