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

/***********************************************
* Drop Down Date select script- by JavaScriptKit.com
* This notice MUST stay intact for use
* Visit JavaScript Kit at http://www.javascriptkit.com/ for this script and more
***********************************************/

var monthtext=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec'];

function populatedropdown(dayfield, monthfield, yearfield){
	var today=new Date();
	var dayfield=document.getElementById(dayfield);
	var monthfield=document.getElementById(monthfield);
	var yearfield=document.getElementById(yearfield);
	
	for (var i=0; i<32; i++)
		dayfield.options[i]=new Option(i, i+1);
	
	dayfield.options[today.getDate()]=new Option(today.getDate(), today.getDate(), true, true); //select today's day
	
	for (var m=0; m<12; m++)
		monthfield.options[m]=new Option(monthtext[m], monthtext[m]);
	
	monthfield.options[today.getMonth()]=new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true); //select today's month
	
	var thisyear=today.getFullYear();
	
	for (var y=0; y<20; y++){
		yearfield.options[y]=new Option(thisyear, thisyear);
		thisyear+=1;
	}
	yearfield.options[0]=new Option(today.getFullYear(), today.getFullYear(), true, true); //select today's year
}



/***********************************************************************************************
						LightBox Functions
 TODO ADD stuff
*************************************************************************************************/




