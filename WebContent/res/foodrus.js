
function updateQuanity(itemID){
	var qty = document.getElementById(itemID).value;
	document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	if(isNaN(qty)){
			alert("WTF");
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