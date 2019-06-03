var LeftArrow = document.getElementById('leftArrow');

var changePopUp = document.getElementById('changePopUp');
var directions = document.getElementById('directionsButton');
var infoForm = document.getElementById('infoForm');

var foot = document.getElementById('footButton');
var transport = document.getElementById('transportButton');
var car = document.getElementById('carButton')

/*const PinsInfo = {
	'friends': {
		'António': {
			Content1: '20 anos',
			Content2: '200m',
			Content3: 'M',
			Content4: '01/01/2019',
			image: 'user',
		},

		'Afonso': {
			Content1: '19 anos',
			Content2: '150m',
			Content3: 'M',
			Content4: '01/01/2019',
			image: 'user'
		},

		'Marcelo': {
			Content1: '23 anos',
			Content2: '500m',
			Content3: 'M',
			Content4: '01/01/2019',
			image: 'user'
		},

		'Mariana': {
			Content1: '19 anos',
			Content2: '500m',
			Content3: 'F',
			Content4: '01/01/2019',
			image: 'user'
		},

		'Joana': {
			Content1: '20 anos',
			Content2: '500m',
			Content3: 'F',
			Content4: '01/01/2019',
			image: 'user'
		},

		image: '../../images/user.svg',
		Title1: 'Idade:',
		Title2: 'Distância:',
		Title3: 'Sexo',
		Title4: 'Data de Nascimento'

	},
	'restaurants': {
		'Little Caeser\'s': {
			Content1: '4.5/5',
			Content2: '400m',
		},
		image: '../../images/Restaurant.svg',
		Title1: 'Rating:',
		Title2: 'Distância:',
	},
	'hotels': {
		'Ritz Lisboa': {
			Content1: '5',
			Content2: '500m',
		},
		image: '../../images/Hotel.svg',
		Title1: 'Estrelas:',
		Title2: 'Distância:',
	},
	'museums': {
		'Museu de Arte Moderna': {
			Content1: '12€',
			Content2: '100m',
		},
		image: '../../images/Museum.svg',
		Title1: 'Preço:',
		Title2: 'Distância:',
	},
	'transports': {
		'Paragem de autocarro': {
			Content1: '5min',
			Content2: '50m',
		},
		image: '../../images/Transport.svg',
		Title1: 'Tempo de espera:',
		Title2: 'Distância:',
	}
};*/


function init() {
	var name = document.getElementById('title');
	var image = document.getElementById('image');
	var detail1Title = document.getElementById('detail1Title');
	var detail1Content = document.getElementById('detail1Content');
	var detail2Title = document.getElementById('detail2Title');
	var detail2Content = document.getElementById('detail2Content');
	var Pin = localStorage.getItem('Pin');
	var type = localStorage.getItem('type');

	let PinsInfo;

    if(localStorage.getItem('pinsInfoToShow') === null){
        localStorage.setItem('pinsInfoToShow', JSON.stringify(pinsInfoToShow))
        PinsInfo = pinsInfoToShow 
    } else {
        PinsInfo = JSON.parse(localStorage.getItem('pinsInfoToShow'))
    }   
	
	name.innerHTML = '<b>' + Pin + '</b>'
	image.src = PinsInfo[type].image;
	detail1Title.innerHTML = '<b>' + PinsInfo[type].Title1 + '</b>';
	detail1Content.innerHTML = '<b>' + PinsInfo[type][Pin].Content1 + '</b>';
	detail2Title.innerHTML = '<b>' + PinsInfo[type].Title2 + '</b>';
	detail2Content.innerHTML = '<b>' + PinsInfo[type][Pin].Content2 + '</b>';


	/*if (type === 'friends') {
		name.innerHTML = '<b>Marcelo Santos</b>'; //If we want to add more, just make this fetch from localStorage
		image.src = '../../images/user.svg';
		detail1Title.innerHTML = '<b>Idade:</b>';
		detail1Content.innerHTML = '<b>20 anos</b>';
		detail2Title.innerHTML = '<b>Distância:</b>';
		detail2Content.innerHTML = '<b>200m</b>';
	} else if (type === 'restaurant') {
		name.innerHTML = "<b>Little Caeser's</b>";
		image.src = '../../images/Restaurant.svg';
		detail1Title.innerHTML = '<b>Rating:</b>';
		detail1Content.innerHTML = '<b>4.5/5</b>';
		detail2Title.innerHTML = '<b>Distância:</b>';
		detail2Content.innerHTML = '<b>400m</b>';
	} else if (type === 'hotel') {
		name.innerHTML = '<b>Ritz Lisboa</b>';
		image.src = '../../images/Hotel.svg';
		detail1Title.innerHTML = '<b>Estrelas</b>';
		detail1Content.innerHTML = '<b>5</b>';
		detail2Title.innerHTML = '<b>Distância:</b>';
		detail2Content.innerHTML = '<b>500m</b>';
	} else if (type === 'museum') {
		name.innerHTML = '<b>Museu de Arte Moderna</b>';
		image.src = '../../images/Museum.svg';
		detail1Title.innerHTML = '<b>Preço</b>';
		detail1Content.innerHTML = '<b>12€</b>';
		detail2Title.innerHTML = '<b>Distância:</b>';
		detail2Content.innerHTML = '<b>100m</b>';
	} else if (type === 'transport') {
		name.innerHTML = '<b>Paragem de autocarro</b>';
		image.src = '../../images/Transport.svg';
		detail1Title.innerHTML = '<b>Tempo de espera</b>';
		detail1Content.innerHTML = '<b>5min</b>';
		detail2Title.innerHTML = '<b>Distância:</b>';
		detail2Content.innerHTML = '<b>50m</b>';
	}*/
}

function clearOtherPaths() {
	for (var i = 0; i < localStorage.length; i++) {
		var name = localStorage.key(i);
		if (name.endsWith('_path')) {
			localStorage.removeItem(name);
		}
	}
}

function givePathFoot() {
	clearOtherPaths();
	localStorage.setItem('friend_visible', true)
	localStorage.setItem(localStorage.getItem('Pin').toLowerCase().split(' ').join('_') + '_foot_path', true)
	window.location.href = 'nav.html'
}

function givePathTransport() {
	clearOtherPaths();
	localStorage.setItem('friend_visible', true)
	localStorage.setItem(localStorage.getItem('Pin').toLowerCase().split(' ').join('_') + '_bus_path', true)
	window.location.href = 'nav.html'
}

function givePathCar() {
	clearOtherPaths();
	localStorage.setItem('friend_visible', true)
	localStorage.setItem(localStorage.getItem('Pin').toLowerCase().split(' ').join('_') + '_car_path', true)
	window.location.href = 'nav.html'
}

function goBack() {
	if (changePopUp.style.display === 'block') {
		changePopUp.style.display = 'none';
		infoForm.style.filter = '';
	} else {
		window.location.href = localStorage.getItem('returnScreen');
	};
}

function showOptions() {
	changePopUp.style.display = 'block';
	infoForm.style.filter = 'blur(8px)';
}

LeftArrow.onclick = goBack;
directions.onclick = showOptions;

foot.onclick = givePathFoot;
transport.onclick = givePathTransport;
car.onclick = givePathCar;

init();
