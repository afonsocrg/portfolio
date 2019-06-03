var key_name = "name";

function insertName() {
    var name_el = document.getElementById(key_name);

    // first name only
    if (name_el != null) {

        var name = localStorage.getItem('user-name').split(' ')[0].slice(0, 9);
        name_el.innerHTML = name;

        if (window.location.href.includes('nav')) {
            name_el.innerHTML = 'GPS'
        } else if (window.location.href.includes('sos')) {
            name_el.innerHTML = 'SOS'
        } else if (window.location.href.includes('translate')) {
            name_el.innerHTML = 'Tradução'
        } else if (window.location.href.includes('contacts')) {
            name_el.innerHTML = 'Contactos'
        }
    }
}


function getDate() {
    var d = new Date()
    var date = document.getElementById("date");
    time = ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    if (date != null) date.innerHTML = time;
}

function autoSOS() {
    var prob = 0.01;
    if (localStorage.getItem("waitingForHelp") != 'true') {
        if (Math.random() < prob) {
            document.location.href = document.location.href.replace(/iGo\/iGo.*/, "iGo/iGo/screens/sos/autocall.html")
        }
    }
}

insertName();
getDate();
setInterval(getDate, 1000);
// setInterval(autoSOS, 5000);
