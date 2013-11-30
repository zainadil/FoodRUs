
function updateQuanity(itemID){
	var qty = document.getElementById(itemID).value;
	document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	document.getElementById("addedIDandQty").value = itemID + ";" + qty;
}