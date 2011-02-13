/**
 * Javascript file for user registration page
 */

$("document").ready(function() {
		//real-time client side error checking
		$("input").bind("change", validateForm);
		//send request to registeration servlet on submit
        $("#regForm").bind("submit",sendRegRequest);
    });

function validateForm(evt){
	if( $(this).is("[name=accType]") ){
		if( !$(this).val() ){
			alert("Account type not selected!");
		}
	}
	else if( $(this).attr('id') == "emailAddr" ){
			var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
			var addr = $(this).val();
			if(reg.test(addr) == false) {
				$("#emailErr").text("Invalid Email Address");
			}
			else{
				$("#emailErr").text("");
			}				
	}
	else if( $(this).attr('id') == "pw1" ){
		var reg = /^[A-Za-z0-9]{5,15}$/;
		var pw = $(this).val();
		if(reg.test(pw) == false) {
			$("#pw1Err").text("Password must be 5 to 15 characters long, and contain only letters and numbers.");
		}
		else{
			$("#pw1Err").text("");
		}
	}
	else if( $(this).attr('id') == "pw2" ){
		var pw1 = $("#pw1").val();
		var pw2 = $(this).val();
		if(pw1 != pw2) {
			$("#pw2Err").text("Passwords do not match.");
		}
		else{
			$("#pw2Err").text("");
		}
	}
}

function sendRegRequest(evt){
	
}