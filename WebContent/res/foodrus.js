
function updateQuanity(itemID) {
	var qty = document.getElementById(itemID).value;

	if(isNaN(qty) || qty > 999 || qty < 0){
		if(qty > 999 )
			alert ("Quantity should be less than 999");
		else if(itemQuantity <= 0)
			alert ("Quantity should be positive");
		else alert("Invalid Quantity");
		return false;
	} else {
		document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	if(isNaN(qty) || qty > 999 || qty < 0){
		if(qty > 999 )
			alert ("Quantity should be less than 999");
		else if(itemQuantity <= 0)
			alert ("Quantity should be greater than 0");
		else alert("Invalid Quantity");
			var id = "q"+itemID;
			document.getElementById(id).style.background="red";
			return false;
	} else{
		document.getElementById("addedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

function validateCredentials(){
	
	var rv = true;
	
	var loginName = document.getElementById("loginName").value;
	var loginPassword = document.getElementById("loginPassword").value;
	
	if (loginName == null || loginPassword == null || loginName.length < 1 || loginPassword.length <1 ) {
		alert("Empty Credentials");
		rv = false;
	}
	return rv;
}

function validateExpressCheckout(){
	
	var rv = true;
	
	var itemNumber = document.getElementById("itemNumber").value;
	var itemQuantity = document.getElementById("itemQuantity").value;
    var loginName = document.getElementById("loginName").value;
	var loginPassword = document.getElementById("loginPassword").value;
	
	var letter = /[a-zA-Z]/; 
    var number = /[0-9]/;
	
	if (loginName == null || loginPassword == null || loginName.length < 1 || loginPassword.length <1 ) {
		alert("Empty Credentials");
		rv = false;
	}
	
    var valid = number.test(itemNumber) && letter.test(itemNumber);
     if(!valid){
    	 alert("Invalid Item-ID");
    	 rv = false;
     }
	if(isNaN(itemQuantity) || itemQuantity >999 || itemQuantity < 1){
		if(itemQuantity > 999)
			alert ("Quantity should be less than 999");
		else if(itemQuantity <= 0)
			alert ("Quantity should be greater than 1");
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

window.setTimeout( closeNotificatonDiv, 1500 );
