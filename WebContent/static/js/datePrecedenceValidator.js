var introducedInput = $("#introduced");
var discontinuedInput = $("#discontinued");

var oldIntroducedValue;
var oldDiscontinuedValue;

function rememberDateValues() {
	oldIntroducedValue = introducedInput.val();
	oldDiscontinuedValue = discontinuedInput.val();
}
rememberDateValues();

function checkDatePrecedence(event) {
	if(introducedInput.val() != "" && discontinuedInput.val() != "") {
		if(discontinuedInput.val() < introducedInput.val()){
			discontinuedInput.parent().addClass("has-error");
			introducedInput.val(oldIntroducedValue);
			discontinuedInput.val(oldDiscontinuedValue);
		} else {
			discontinuedInput.parent().removeClass("has-error");
		}
	}
	rememberDateValues();
}

introducedInput.change(checkDatePrecedence);
discontinuedInput.change(checkDatePrecedence);