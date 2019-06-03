var key_initFlag = 'initFlag';
var key_locFlag = 'willSendLocation';
var key_fileFlag = 'willSendFile';

var screen = document.getElementById('screen');

var screens = [
	{
		screen: document.getElementById('screen1'),
		ball: document.getElementById('ball1')
	},
	{
		screen: document.getElementById('screen2'),
		ball: document.getElementById('ball2')
	}
];

var currScreen;

var MyWeb = document.getElementById('MyWeb');
var Def = document.getElementById('Def');
var Sos = document.getElementById('Sos');
var Nav = document.getElementById('Nav');
var Translate = document.getElementById('Translate');
var Contacts = document.getElementById('Contacts');
var LeftArrow = document.getElementById('leftArrow');
var RightArrow = document.getElementById('rightArrow');

var currentScroll = 0;

function init() {
	if (localStorage.getItem(key_initFlag) == null) {
		localStorage.setItem(key_initFlag, 'check');
		localStorage.setItem(key_locFlag, 'false');
		localStorage.setItem(key_fileFlag, 'false');

		document.location.href = 'screens/setup/welcome.html';
	}
}

function getNotImplemented() {
	currentScroll = screen.scrollTop;
	document.location.href = 'screens/notImplemented/notImplemented.html';
}

function getSos() {
	currentScroll = screen.scrollTop;
	if (localStorage.getItem('waitingForHelp') == 'true') document.location.href = 'screens/sos/help.html';
	else document.location.href = 'screens/sos/sos.html';
}

function getSetup() {
	currentScroll = screen.scrollTop;
	document.location.href = 'screens/setup/setup.html';
}

function getTranslate() {
	currentScroll = screen.scrollTop;
	document.location.href = 'screens/translate/translate.html';
}

function getNav() {
	currentScroll = screen.scrollTop;
	document.location.href = 'screens/nav/nav.html';
}

function getContacts() {
	currentScroll = screen.scrollTop;
	document.location.href = 'screens/contacts/contacts.html';
}

function pageRight() {
	if (currScreen < screens.length) {
		currScreen++;
		localStorage.setItem('screen', currScreen);
		screens[currScreen].screen.scrollIntoView({ behavior: 'smooth' });
		screens[currScreen].ball.classList.add('selectedNavBall');
		screens[currScreen - 1].ball.classList.remove('selectedNavBall');
	}
}

function pageLeft() {
	if (currScreen > 0) {
		currScreen--;
		localStorage.setItem('screen', currScreen);
		screens[currScreen].screen.scrollIntoView({ behavior: 'smooth' });
		screens[currScreen].ball.classList.add('selectedNavBall');
		screens[currScreen + 1].ball.classList.remove('selectedNavBall');
	}
}

if (localStorage.getItem('screen') === null) {
	currScreen = 0;
} else {
	currScreen = parseInt(localStorage.getItem('screen'), 10);
}

MyWeb.onclick = getNotImplemented;
Contacts.onclick = getContacts;

Def.onclick = getSetup;
Sos.onclick = getSos;
Translate.onclick = getTranslate;
Nav.onclick = getNav;

LeftArrow.onclick = pageLeft;
RightArrow.onclick = pageRight;

screen.scrollTop = currentScroll;

window.onload = function() {
	screens[currScreen].screen.scrollIntoView();
	screens[currScreen].ball.classList.add('selectedNavBall');
};
init();
