
//$(initDockSize);//perform onload

function initDockSize(){
	var iconArray = $("#dock").find('li a').get(); //retrieve the array of the icons, format[li a id=...]
	var numIcon = iconArray.length;
	var iconWidth = $("#dock").find('li a').width();
	var temp =numIcon*iconWidth;
	$("#dock").css("width",temp);
}