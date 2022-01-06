package com.relateddigital.relateddigital_android.push

object TestPush {
    var testImage = """{
    "pushType": "Image",
    "url": "https://www.euromsg.com/",
    "mediaUrl": "https://mcdn01.gittigidiyor.net/ps/banner_1583388342.jpg",
    "pushId": "bea5303f-11aa-4ac7-aae8-2265ba63b535",
    "altUrl": "",
    "sound": "sound",
    "message": "Bugünü kaçırma",
    "title": "BUGÜNE ÖZEL 150 TL İNDİRİM"

}"""
    var testText = """{
    "pushType": "Text",
    "url": "https://www.euromsg.com/",
    "mediaUrl": "",
    "pushId": "bea5303f-11aa-4ac7-aae8-2265ba63b535",
    "altUrl": "",
    "sound": "sound",
    "message": "Buzdolabı, Soğutucu",
    "title": "BUGÜNE ÖZEL 150 TL İNDİRİM"

}"""
    var testCarousel = """{
    "pushType": "Text",
    "url": "https://www.euromsg.com/",
    "mediaUrl": "",
    "pushId": "bea5303f-11aa-4ac7-aae8-2265ba63b535",
    "altUrl": "",
    "sound": "sound",
    "message": "test carousel",
    "title": "test title",
    "elements": [{
            "id": 1,
            "title": "Süper İndirim",
            "content": "Akıllı saatlerde akılalmaz indirimleri kaçırmayın. Harika bir saat",
            "url": "https://www.euromsg.com/",
            "picture": "https://mcdn01.gittigidiyor.net/52578/tn50/525782224_tn50_0.jpg?1583398"
        }, {
            "id": 2,
            "title": "Mükemmel İndirim",
            "content": "Hayatını akıllı saatlerle beraber çok kolaylaştırabilirsin. Akıllı saatleri incele!",
            "url": "https://www.relateddigital.com/",
            "picture": "https://mcdn01.gittigidiyor.net/49668/tn50/496687214_tn50_0.jpg?1583398"
        }, {
            "id": 3,
            "title": "Şaşırtıcı İndirim",
            "content": "Akıllı Saatler ! ",
            "url": "https://www.google.com/",
            "picture": "https://mcdn01.gittigidiyor.net/49101/tn50/491019547_tn50_0.jpg?1583398"
        }
    ]
}"""
}