
let leftArrow = document.getElementById("leftArrow");
var container = document.getElementById("manual-container")

let contacts;

if(localStorage.getItem('contactList') === null){
    localStorage.setItem('contactList', JSON.stringify(contactList))
    contacts = contactList
} else {
    contacts = JSON.parse(localStorage.getItem('contactList'))
}


/*const contactList = {
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
};*/


function goBack() {
    document.location.href = "../../index.html"
}

function seeContact(event) {
    console.log(contacts[event.target.classList[1]]);
    localStorage.setItem('returnScreen', '../contacts/contacts.html');
    localStorage.setItem('type', 'friends');
    localStorage.setItem('Pin', contacts[event.target.classList[1]].name);
    document.location.href = "../nav/pinInfo.html";
}

function addNewContact() {
    localStorage.setItem('clean', true);
    document.location.href = "addContact.html"
}


let newContact_box = document.createElement("div")
let newContact_container = document.createElement("div")
let contactimg = document.createElement("img")
let contact_name = document.createElement("div")

contact_name.className = "lang-name"
contact_name.classList.add("Adicionar_Contacto")
contact_name.innerHTML = "Adicionar Contacto"

contactimg.className = "flag"
contactimg.src = "../../images/userAdd.svg"
contactimg.classList.add("Adicionar_Contacto")

newContact_container.className = "flag-container"
newContact_container.classList.add("Adicionar_Contacto")

newContact_box.className = "lang-box"
newContact_box.classList.add("Adicionar_Contacto")

newContact_container.appendChild(contactimg)

newContact_box.appendChild(newContact_container)
newContact_box.appendChild(contact_name)

container.appendChild(newContact_box)
newContact_box.onclick = addNewContact

for (let cont in contacts) {
    
    let lang_box = document.createElement("div")
    let flag_container = document.createElement("div")
    let img = document.createElement("img")
    let lang_name = document.createElement("div")
    let key = contacts[cont].name;

    lang_name.className = "lang-name"
    lang_name.classList.add(key)
    lang_name.innerHTML = contacts[cont].name;

    img.className = "flag"
    img.src = "../../images/" + contacts[cont].photo + ".svg"
    img.classList.add(key)

    flag_container.className = "flag-container"
    flag_container.classList.add(key);

    lang_box.className = "lang-box"
    lang_box.classList.add(key)
    

    flag_container.appendChild(img)

    lang_box.appendChild(flag_container)
    lang_box.appendChild(lang_name)

    container.appendChild(lang_box)

    lang_box.onclick = seeContact;
}




leftArrow.onclick = goBack;