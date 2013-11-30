
function updateQuanity(itemID){
	var qty = document.getElementById(itemID).value;
	document.getElementById("updatedIDandQty").value = itemID + ";" + qty;
<<<<<<< HEAD
}

function addtoCart(itemID){
	var qty = document.getElementById(itemID).value;
	document.getElementById("addedIDandQty").value = itemID + ";" + qty;
=======
//	alert(document.getElementById("updatedIDandQty"));
>>>>>>> 527b8ad293555609d5fe7115fd9c57ffd6ab0d3a
}