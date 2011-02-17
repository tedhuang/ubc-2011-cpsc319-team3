/**
 * Javascript file for forget password page
 */

$("document").ready(function() {
	// update name label based on account type chosen
	$("input:[name=accountType]").bind("change",function(){
		if( $("input:[name=accountType]:checked").val() == "searcher" ){
			$("#nameLabel").text("Name: ");
		}
		else{
			$("#nameLabel").text("Company/organization name: ");
		}
	});
	// real-time client side error checking
	$("input").bind("change", validateForm);
	// send request to registeration servlet on submit
	$("#submitButton").bind("click",sendRegRequest);
});