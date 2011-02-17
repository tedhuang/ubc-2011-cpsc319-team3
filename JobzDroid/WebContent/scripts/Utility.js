// Utility javascript functions and classes

/***
 * A string wrapper class that represents xmlhttpsrequest parameters
 * with string manipulation functions (format: action + sessionID + other parameters)
 * Example output: action=register&sessionID=1234&arg1=value1
 */
function Request(){
	this.str = "";
};
Request.prototype.addAction = function(action){
	this.str += "action=" + encodeURIComponent(action);
};
Request.prototype.addSessionID = function(sessionID){
	this.str += "&sessionID=" + encodeURIComponent(sessionID);
};
Request.prototype.addParam = function(param, arg){
	this.str += "&" + param + "=" + encodeURIComponent(arg);
};
Request.prototype.toString = function(){
	return this.str;
};