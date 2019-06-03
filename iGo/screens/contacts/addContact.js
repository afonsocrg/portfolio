

var nameField = document.getElementById('nameField');
var ageField = document.getElementById('ageField');

var LeftArrow = document.getElementById('leftArrow');


var radio = document.getElementsByClassName('sex-radio');
var sexFemale = radio[0];
var sexMale = radio[1];

var confirm = document.getElementById('okButton');
var cancel = document.getElementById('cancelButton');
var form = document.getElementById('bioForm');

var nameInput;
var preAge;
var preSex;

function init() {

    if(localStorage.getItem('clean') === 'true'){
       localStorage.setItem('contact-name', '')
       localStorage.setItem('contact-age', '') 
    }

    var content

    var nameInput = localStorage.getItem('contact-name');
	var ageInput = localStorage.getItem('contact-age');

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
    
    if (localStorage.getItem('sex-male-contact') === 'true') checkMale();
	if (localStorage.getItem('sex-female-contact') === 'true') checkFemale();

	showConfirm();
}


function showConfirm() {
	if (toShowConfirm()) {
		confirm.style.display = 'block';
		cancel.style.display = 'block';
		bioForm.style.height = '70%';
	} else {
        confirm.style.display = 'none';
        cancel.style.display = 'none'
		bioForm.style.height = '90%';
	}
}

function toShowConfirm() {
	return (
		localStorage.getItem('contact-name') != null &&
		localStorage.getItem('contact-name') != '' && // user-name is set
		(localStorage.getItem('contact-age') != null && localStorage.getItem('contact-age') != '') && // age is set
		(radio[0].checked || radio[1].checked) // sex is set
	);
}

function checkMale() {
	localStorage.setItem('sex-male-contact', true);
	localStorage.setItem('sex-female-contact', false);
    sexMale.checked = true;
    showConfirm();
}

function checkFemale() {
	localStorage.setItem('sex-female-contact', true);
	localStorage.setItem('sex-male-contact', false);
    sexFemale.checked = true;
    showConfirm();
}

let getName = function() {
    window.location.href = 'contactName.html';
}

let getAge = function() {
	window.location.href = 'addAge.html';
};

let goBack = function() {
	window.location.href = 'contacts.html';
};

let addContact = function() {

    let pins;

    if(localStorage.getItem('pinsToShow') === null){
        localStorage.setItem('pinsToShow', JSON.stringify(pinsToShow))
        pins = pinsToShow 
    } else {
        pins = JSON.parse(localStorage.getItem('pinsToShow'))
    } 


    let pinObject = {
        'id': localStorage.getItem("contact-name").toLowerCase().split(' ').join('_'),
        'type': "friend",
        'x': Math.random()*0.5,
        'y': Math.random()*0.5,
        'name': localStorage.getItem("contact-name")
    }

    pins.push(pinObject)

    localStorage.setItem('pinsToShow', JSON.stringify(pins)) 

    let contacts;

    if(localStorage.getItem('contactList') === 'undefined'){
        localStorage.setItem('contactList', JSON.stringify(contactList))
        contacts = contactList
    } else {
        contacts = JSON.parse(localStorage.getItem('contactList'))
}

    contacts[localStorage.getItem("contact-name")] = {
        "name": localStorage.getItem("contact-name"),
        "photo": "user",
        "gender" : radio[0].checked ? "M" : "F",
        "birthday": localStorage.getItem("contact-age"),
        "age": 19
    }

    localStorage.setItem('contactList', JSON.stringify(contacts))


    let PinsInfo;

    if(localStorage.getItem('pinsInfoToShow') === null){
        localStorage.setItem('pinsInfoToShow', JSON.stringify(pinsInfoToShow))
        PinsInfo = pinsInfoToShow 
    } else {
        PinsInfo = JSON.parse(localStorage.getItem('pinsInfoToShow'))
    }   

    PinsInfo['friends'][localStorage.getItem("contact-name")] = {
        Content1: '19 anos',
        Content2: (Math.round(Math.random()*1000)).toString() + "m",
        Content3: radio[0].checked ? "M" : "F",
        Content4: localStorage.getItem("contact-age"),
        image: 'user'
    }

    localStorage.setItem('pinsInfoToShow', JSON.stringify(PinsInfo))
	
    window.location.href = 'contacts.html';
};

nameField.onclick = getName;
ageField.onclick = getAge;

confirm.onclick = addContact;
cancel.onclick = goBack;

LeftArrow.onclick = goBack;


sexMale.onclick = checkMale;
sexFemale.onclick = checkFemale;

init()