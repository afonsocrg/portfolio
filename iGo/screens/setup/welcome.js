welcome = document.getElementById("welcome-msg");
igo = document.getElementById("igo");

function init() {
    setTimeout(() => {
        welcome.style.opacity = '1';
        welcome.style.top = '1cm';
    }, 100);

    setTimeout(() => {
        igo.style.opacity = '1';
        igo.style.top = '1.5cm';
        welcome.style.opacity = '1';
    }, 100);
}

function next() {
    window.location.href = "setup.html"
}

setTimeout(next, 3000);
init();
