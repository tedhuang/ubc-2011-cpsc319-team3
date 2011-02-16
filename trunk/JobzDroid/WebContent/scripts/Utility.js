/**
 * Utility javascript functions and classes
 */

/***
 * A string wrapper class that represents xmlhttpsrequest parameters
 * with string manipulation functions 
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