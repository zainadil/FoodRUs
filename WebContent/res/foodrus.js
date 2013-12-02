
function updateQuanity(itemID) {
	var qty = document.getElementById(itemID).value;

	if (isNaN(qty)) {
		alert("Invalid Quantity");
		return false;
	} else {
		document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	if(isNaN(qty)){
			alert("Invalid Quantity");
			var id = "q"+itemID;
			document.getElementById(id).style.background="red";
			return false;
	} else{
		document.getElementById("addedIDandQty").value = itemID + ";" + qty;
		return true;
	}
}

// Hack, do something about this later.
function closeNotificatonDiv(){
	document.getElementById("addtoCartNotification").style.display=" none";
}

window.setTimeout( closeNotificatonDiv, 2000 );