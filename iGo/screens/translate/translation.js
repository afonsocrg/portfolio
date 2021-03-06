var LeftArrow = document.getElementById("leftArrow");
var translationBox = document.getElementById("textBox");
var TTSButton = document.getElementById("TTSContainer");
var TTSIcon = document.getElementById("TTSIcon");

var toShow;
var toBold;
let len;
var saying = false;

const languages = {
    pt: {
        flag: "flag_pt",
        name: "Português",
        sampleText: "O ensino médio e superior cresceu muito nos últimos vinte anos, acompanhando o crescimento demográfico. O aumento das forças produtivas e a inchação da economia, incidiram nas instituições culturais, de que a escola é espinha dorsal."
    },
    en: {
        flag: "flag_uk",
        name: "English",
        sampleText: "Made last it is seen went no just when of by. Occasional entreaties comparison me difficulty so themselves. At brother inquiry of offices without doing my service. As particular to companions at sentiments. Weather however luckily enquire so certain do. Aware did stand was a day under ask."
    },
    ge: {
        flag: "flag_ge",
        name: "Deutsch",
        sampleText: "Wachsamen holzspane in kellnerin filzhutes um he. Du verdrossen in launischen da es lattenzaun. Bodenlosen ri pa zu bescheiden fu feierabend. Pa bummelte im so em eigentum gebogene. Anzeichen in schreiben so kraftiger bekummert aufstehen."
    },
    es: {
        flag: "flag_es",
        name: "Español",
        sampleText: "Colchon los contado don comodas referia promesa dio. Duros eso rio vista dejar mar. Morir el mando antes jamas debia la. Yendo ya ir asise mimar so la. Iba semiramis una emperador non cachazudo infiernos adivinaba distraigo. Ma fortuna se fijaran acabara quejaba relator yo noticia."
    },
    fr: {
        flag: "flag_fr",
        name: "Français",
        sampleText: "Dela de pied ii hors. Mal fin conflit content hauteur fut tendues mineurs des tapisse. Vie uns tarderait cartouche courroies qui cesserent. Sa je bondi sabre noble. Eclairs barbare par epaules non eux qui. Decor me gagne faire menue salle la. Cuirasses but sacrifiez toutefois fabriques citadelle ici."
    },
    it: {
        flag: "flag_it",
        name: "Italiano",
        sampleText: "Pensavo copriva conosco una non cattivo tal. Uno distrutta osi desiderio era soffocato benedetto vertigine talismani dov. Giu dal ricuperata aggiungera silenziosa impazienza. Scorato chinava tuo lei bisogno. Udissi affina piu cui nemica spalle non. The ben ergendosi impudente sconvolge."
    },
    jp: {
        flag: "flag_jp",
        name: "日本人 (Japanese)",
        sampleText: "第六章 第十章 第四章 第九章 第三章 第二章. 復讐者」 . 第八章 第七章 第九章 第六章 第十章 第二章. 復讐者」. 手配書 第十一章 第十七章 第十四章 第十三章 第十六章. 復讐者」. .伯母さん 復讐者」. 第十七章 第十九章 手配書 第十四章 第十二章 第十五章. 復讐者」. 第十五章 第十九章 第十三章 第十六章 手配書 第十七章."
    },
    mn: {
        flag: "flag_mn",
        name: "普通话 (Mandarin)",
        sampleText: "貢院 第八回 了」 德泉淹. 」 耳 意 曰： 事 出 ，可 關雎. 」 關雎 ，可 事. 出 矣 ，可 意 耳 事 去. 第六回 相域 德泉淹 了」 第一回 第五回. 耳 意 覽 矣. 誨 出 覽 此是後話 也懊悔不了 關雎 事 ，愈聽愈惱 饒爾去罷」. ，可 覽 出 耳 事. 分得意 不稱讚 後竊聽 ﻿白圭志 第十一回. 曰： ，愈聽愈惱 饒爾去罷」 此是後話 誨 意 去."
    },
    ru: {
        flag: "flag_ru",
        name: "русский (Russian)",
        sampleText: "Ея яр Не Ко не. Его выя хор Чего Отч реку одра Сый муж Для. ﻿кто. Под буй лик тих был при. Тул тук бою рубище всякая вся иду днешня меж близка пот нег лиется. Зеркале угождал Сегодня Вам Чей хор сломить вне Кончать трудись. Мимо тебе зарь. Ей На да Свое со ко ею Мы трон Ни коим."
    }, 
    unknown: {
        flag: "flag_unknown",
        name: "Selecionar Linguagem"
    }
}

function goBack() {
    document.location.href = "translating.html"
}

function init() {
    translationBox.innerHTML = "";  
    
    toShow = (Math.random() < 0.5 ?
        localStorage.getItem("lang2")
            :
        localStorage.getItem("lang1"));

    let split = languages[toShow].sampleText.split(" ");
    len = split.length;

    for (let i = 0; i < len; i++) {
        var s = document.createElement("span");
        s.className = "translationWord";
        s.id = i;
        s.innerHTML = split[i];

        translationBox.appendChild(s);   
    }   
}

function ttsShow(){
    if(saying) {
        saying = false;
        TTSIcon.src = "../../images/speaker.svg";
        return;
    }

    TTSIcon.src =  "../../images/mutespeaker.svg";
    saying = true;
    toBold = translationBox.children;
    toBold[0].style.fontWeight = 'bold'
    toBold[0].scrollIntoView({behavior: "smooth", block: "end"});
    setTimeout(ttsShowAux, 400, 1);
}

function ttsShowAux(index) {
    toBold[index - 1].style.fontWeight = '';
    if(saying && index < len) {
        toBold[index].style.fontWeight = 'bold';
        toBold[index].scrollIntoView({behavior: "smooth", block: "end"});
        if(saying)
            setTimeout(ttsShowAux, 400, index + 1);
    }
}

LeftArrow.onclick = goBack;
TTSButton.onclick = ttsShow;

init();
