function HelperUtility() {

}

HelperUtility.prototype.getObjectXHR = function() {
    var xmlhttp;
	if(window.XMLHttpRequest){
	    xmlhttp = new XMLHttpRequest();
	}else{
	    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xmlhttp;
};

HelperUtility.prototype.assert = function(var b) {
	if(b){
		alert("Test Passed!");
    }else {
		alert("Test Failed!");
    }	
};

HelperUtility.prototype.track = function(String field, String content) {
	document.getElementById(field).innerhtml = content;
}

HelperUtility.prototype.equals = function(var str1, var str2) {
	return str1.toLowerCase() == str2.toLowerCase();
};

var helper = new HelperUtility();