var nameField = document.getElementById('nameField');
var ageField = document.getElementById('ageField');

var LeftArrow = document.getElementById('leftArrow');

var medicalButton = document.getElementById('medicalButton');
var medicalFileImage = document.querySelector('#medField .input-image');

var radio = document.getElementsByClassName('sex-radio');
var sexFemale = radio[0];
var sexMale = radio[1];

var confirm = document.getElementById('okButton');
var cancel = document.getElementById('cancelButton');
var form = document.getElementById('bioForm');

var nameInput;
var preAge;
var preMedicalFile;
var preSex;

var updateBloodPressure = document.getElementById('updateBloodPressure');
// initialize input fields
function init() {
	var nameInput = localStorage.getItem('user-name');
	var ageInput = localStorage.getItem('user-age');
	var uploadedMedicalFile = localStorage.getItem('fingerprint');

	var content;

	if (nameInput != null && nameInput != '') {
		content = document.getElementById('nameInput');
		content.className = 'textInput';
		content.innerHTML = nameInput;
	} else {
		content = document.getElementById('nameInput');
		content.className += ' ' + 'empty-input';
		document.getElementById('nameInput').innerHTML = 'Nome'; // placeholder
	}

	if (ageInput != null && ageInput != '') {
		content = document.getElementById('ageInput');
		content.className = 'textInput';
		content.innerHTML = ageInput;
	} else {
		content = document.getElementById('ageInput');
		content.className += ' ' + 'empty-input';
		content.innerHTML = 'Data de Nascimento'; // placeholder
	}

	if (uploadedMedicalFile == 'true') {
		medicalFileImage.src = '../../images/file_checked.svg';
	}

	if (localStorage.getItem('sex-male') === 'true') checkMale();
	if (localStorage.getItem('sex-female') === 'true') checkFemale();

	showConfirm();
}

function showConfirm() {
	if (toShowConfirm()) {
		confirm.style.display = 'block';
		cancel.style.display = 'block';
		bioForm.style.height = '80%';
	} else {
		confirm.style.display = 'none';
		bioForm.style.height = '100%';
	}
}

function toShowConfirm() {
	return (
		localStorage.getItem('user-name') != null &&
		localStorage.getItem('user-name') != '' && // user-name is set
		(localStorage.getItem('user-age') != null && localStorage.getItem('user-age') != '') && // age is set
		(radio[0].checked || radio[1].checked) && // sex is set
		localStorage.getItem('fingerprint') == 'true'
	);
}

function checkMale() {
	localStorage.setItem('sex-male', true);
	localStorage.setItem('sex-female', false);
	sexMale.checked = true;
}

function checkFemale() {
	localStorage.setItem('sex-female', true);
	localStorage.setItem('sex-male', false);
	sexFemale.checked = true;
}

let getName = function() {
	localStorage.setItem('old-name', localStorage.getItem('user-name'));
	localStorage.setItem('description', 'self');
	window.location.href = 'name.html';
};

let getAge = function() {
	localStorage.setItem('old-age', localStorage.getItem('user-age'));
	localStorage.setItem('description', 'age');
	window.location.href = 'self.html';
};

let getFingerprint = function() {
	localStorage.setItem('old-fingerprint', localStorage.getItem('fingerprint'));
	window.location.href = 'fingerprint.html';
};

let goToMenu = function() {
	localStorage.setItem('old-name', localStorage.getItem('user-name'));
	localStorage.setItem('old-age', localStorage.getItem('user-age'));
	localStorage.setItem('old-fingerprint', localStorage.getItem('fingerprint'));
	localStorage.removeItem('description'); // cleanup aux storage
	window.location.href = '../../index.html';
};

let getThreshold = function() {
	localStorage.setItem('slider-name', localStorage.getItem('user-name'));
	window.location.href = 'threshold.html';
};

let goBack = function() {
	localStorage.setItem('user-name', localStorage.getItem('old-name'));
	localStorage.setItem('user-age', localStorage.getItem('old-age'));
	localStorage.setItem('fingerprint', localStorage.getItem('old-fingerprint'));
	window.location.href = '../../index.html';
};

nameField.onclick = getName;
ageField.onclick = getAge;
medicalButton.onclick = getFingerprint;
confirm.onclick = goToMenu;
cancel.onclick = goBack;

LeftArrow.onclick = goBack;

sexMale.onclick = checkMale;
sexFemale.onclick = checkFemale;

updateBloodPressure.onclick = getThreshold;

init();
