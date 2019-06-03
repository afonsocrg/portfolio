var pinsToShow = [
    {
        'id': 'self',
        'type': 'self',
        'x': 0.515,
        'y': 0.4,
    },
    {
        'id': 'antonio',
        'type': 'friend',
        'x': 0.600,
        'y': 0.4,
        'name': 'António'
        
    },
    {
        'id': 'afonso',
        'type': 'friend',
        'x': 0.400,
        'y': 0.5,
        'name': 'Afonso',
        'bus' : {
            'x' : 0.500,
            'y' : 0.52,
        },
    },
    {
        'id': 'marcelo',
        'type': 'friend',
        'x': 0.500,
        'y': 0.75,
        'name': 'Marcelo'
    },
    {
        'id': 'little_caeser\'s',
        'type': 'restaurant',
        'x': 0.800,
        'y': 0.5,
        'name': 'Little Caeser\'s'
    },
    {
        'id': 'ritz_lisboa',
        'type': 'hotel',
        'x': 0.100,
        'y': 0.7,
        'name': 'Ritz Lisboa' 
    },
    {
        'id': 'museu_de_arte_moderna',
        'type': 'museum',
        'x': 0.400,
        'y': 0.2,
        'name': 'Museu de Arte Moderna'
    },
    {
        'id': 'paragem_de_autocarro',
        'type': 'transport',
        'x': 0.120,
        'y': 0.4,
        'name': 'Paragem de autocarro'
    }
];


const contactList = {
    "António": {
        "name": "António",
        "photo": "user",
        "gender": "M",
        "birthday": "01/01/1998",
        "age": 20
    },
    "Mariana": {
        "name": "Mariana",
        "photo": "user",
        "gender": "F",
        "birthday": "01/01/1998",
        "age": 18
    },
    "Marcelo": {
        "name": "Marcelo",
        "photo": "user",
        "gender": "M",
        "birthday": "01/01/1998",
        "age": 23
    },
    "Joana": {
        "name": "Joana",
        "photo": "user",
        "gender": "M",
        "birthday": "01/01/1998",
        "age": 20
    },
    "Afonso": {
        "name": "Afonso",
        "photo": "user",
        "gender": "M",
        "birthday": "01/01/1998",
        "age": 19
    }
};


const pinsInfoToShow = {
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
};
