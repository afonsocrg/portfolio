var LeftArrow = document.getElementById('leftArrow');

var friend = document.getElementById('friend');
var restaurant = document.getElementById('restaurant');
var hotel = document.getElementById('hotel');
var museum = document.getElementById('museum');
var transport = document.getElementById('transport');

function showInfo(type) { }

function goBack() {
	document.location.href = 'nav.html';
}


console.log("setting classes");
console.log((localStorage.getItem('friend_visible') === 'true' ? "-toggled" : ""))
friend.className = "cell" + (localStorage.getItem('friend_visible') === 'true' ? "-toggled" : "");
restaurant.className = "cell" + (localStorage.getItem('restaurant_visible') === 'true' ? "-toggled" : "");
hotel.className = "cell" + (localStorage.getItem('hotel_visible') === 'true' ? "-toggled" : "");
museum.className = "cell" + (localStorage.getItem('museum_visible') === 'true' ? "-toggled" : "");
transport.className = "cell" + (localStorage.getItem('transport_visible') === 'true' ? "-toggled" : "");


LeftArrow.onclick = goBack;

friend.onclick = () => {
	if (localStorage.getItem('friend_visible') === 'true') {
		localStorage.setItem('friend_visible', false);
		friend.className = "cell"
	}
	else {
		localStorage.setItem('friend_visible', true);
		friend.className = "cell-toggled"
	}
};

restaurant.onclick = () => {
	if (localStorage.getItem('restaurant_visible') === 'true') {
		localStorage.setItem('restaurant_visible', false);
		restaurant.className = "cell"
	}
	else {
		localStorage.setItem('restaurant_visible', true);
		restaurant.className = "cell-toggled"
	}
};

hotel.onclick = () => {
	if (localStorage.getItem('hotel_visible') === 'true') {
		localStorage.setItem('hotel_visible', false);
		hotel.className = "cell"
	}
	else {
		localStorage.setItem('hotel_visible', true);
		hotel.className = "cell-toggled"
	}
};

museum.onclick = () => {
	if (localStorage.getItem('museum_visible') === 'true') {
		localStorage.setItem('museum_visible', false);
		museum.className = "cell"
	}
	else {
		localStorage.setItem('museum_visible', true);
		museum.className = "cell-toggled"
	}
};

transport.onclick = () => {
	if (localStorage.getItem('transport_visible') === 'true') {
		localStorage.setItem('transport_visible', false);
		transport.className = "cell"
	}
	else {
		localStorage.setItem('transport_visible', true);
		transport.className = "cell-toggled"
	}
};
