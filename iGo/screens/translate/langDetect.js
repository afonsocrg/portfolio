const key_lang1 = "lang1";
const key_lang2 = "lang2";
const key_change = "changing_lang";
const langs = [ 
    "en",
    "es",
    "fr",
    "ge",
    "it",
    "jp",
    "mn",
    "pt",
    "ru"
]

var LeftArrow = document.getElementById("leftArrow");;


function goBack() {
    document.location.href = "translate.html"
}


function detect() {
    // use random language
    var newLang = langs[Math.floor(Math.random() * langs.length)]

    var changing = localStorage.getItem(key_change);
    var other = changing == key_lang1 ? key_lang2 : key_lang1;

    // just update if new language different from current
    if(newLang != localStorage.getItem(changing)) {
        // swap languages if one in common
        if(newLang == localStorage.getItem(other)) {
            localStorage.setItem(other, localStorage.getItem(changing));
        }
        localStorage.setItem(changing, newLang);
    }

    goBack();
}

LeftArrow.onclick = goBack;

setTimeout(detect, 2500);