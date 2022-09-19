let UPDATE_PRODUCT_INTERVAL, SCORE = 0, DURATION, HEIGHT = window.innerHeight, AUDIO;

let MAIN_COMPONENT = document.createElement("DIV");

// Page nums
let screens = {
    form: 1,
    rules: 2,
    game: 3,
    finish: 4
};


let activePageData = {
    mailSubsScreen: false,
    rulesScreen: false,
};

/**
 * Defaults 
 * */
let generalData = {
    id: 'rmc-gift-catch',
    bgColor: 'white',
    bgImg: 'https://picsum.photos/seed/picsum/400/800',
    basketImg: 'https://app.visilabs.net/download/loreal/Game/MobilSenaryolari/materials/bag-min.png',
    sound: '',
    fontColor: 'gray',
    fontName: 'Helvetica',
    fontFiles: [],
    closeButtonId: 'rmc-close-button',
    closeButtonColor: 'black',
    borderRadius: '10px',
    difficulty: 4
};



/**
 * Product settings
 */
let productSettings = {
    productIdPrefix: 'item',
    totalProductCount: 40,
    productTimeOutArray: [],
    productSize: 75,
    turn: false
}

/**
 * Product img
 */
let productImgs = [
]

/**
 * Box drop speed setting
 */
let difficulty = {
    // Selected level
    use: {
        boxTransition: 30,
        boxBottom: 50,
        boxInterval: 200,
    },
    // Levels
    easy: {
        difficulty: 'easy',
        boxTransition: 30,
        boxBottom: 50,
        boxInterval: 250,
    },
    normal: {
        difficulty: 'normal',
        boxTransition: 30,
        boxBottom: 50,
        boxInterval: 200,
    },
    mid: {
        difficulty: 'mid',
        boxTransition: 20,
        boxBottom: 50,
        boxInterval: 100,
    },
    hard: {
        difficulty: 'hard',
        boxTransition: 10,
        boxBottom: 50,
        boxInterval: 50,
    }
}

/**
 * Game page settins
*/
let gameSettings = {
    intersectionPoint: 70, // Intersection point (basket size or around is the ideal point) (It is not calculated automatically to avoid loss of performance)
    basketId: 'basket',
    basketSize: 150,
    lowPowerMode: true, // If true, it does less checking, applies less visual effects
}

/**
 * Coupons
*/
let couponCodes = {
};
/**
 * Components defaults
 */
let componentsData = {
    mailSubsScreen: {
        id: "rmc-mail-subs-screen",
        title: { // OPTIONAL
            use: false,
            text: 'İndirim Kazan',
            textColor: 'lightblue',
            fontSize: '19px'
        },
        message: { // OPTIONAL
            use: false,
            text: 'İndirim Kazanmak için formu doldur ve oyunu oyna.',
            textColor: 'darkblue',
            fontSize: '15px'
        },
        emailPermission: { // OPTIONAL
            use: false,
            id: 'rmc-email-permission-checkbox',
            text: 'Burası eposta izin metnidir. İncelemek için tıklayın.',
            fontSize: '15px',
            url: 'www.google.com',
        },
        secondPermission: { // OPTIONAL
            use: false,
            id: 'rmc-second-permission-checkbox',
            text: 'Kullanım Koşulları\'nı okudum ve kabul ediyorum.',
            fontSize: '15px',
            url: 'www.google.com',
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-mail-subs-button',
            text: 'Kaydet ve Devam Et',
            textColor: 'darkblue',
            buttonColor: 'lightblue',
            fontSize: '15px',
            goScreen: screens.rules,
        },
        emailInput: {
            id: 'rmc-email-input',
            placeHolder: 'Email',
            value: '',
        },
        alerts: {
            invalid_email_message: "Please enter a valid E-Mail Address.",
            check_consent_message: "Please confirm you accept the terms of use."
        }
    },
    rulesScreen: {
        bgImage: "2-min.png",
        id: "rmc-rules-screen",
        title: { // OPTIONAL
            use: false,
            text: 'Kurallar',
            textColor: 'black',
            fontSize: '19px'
        },
        message: { // OPTIONAL
            use: false,
            text: 'Yukarıdan düşen ürünleri Topla',
            textColor: 'black',
            fontSize: '15px'
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-rules-button',
            text: 'Oyuna Başla',
            textColor: 'darkblue',
            buttonColor: 'lightblue',
            fontSize: '15px',
            goScreen: screens.game,
        },
    },
    gameScreen: {
        id: "rmc-game-screen",
        scoreboard: {
            id: 'rmc-scoreboard',
            fontSize: '20px',
            background: 'white',
            fontColor: 'gray',
            type: 'roundedcorners', // square | circle | roundedcorners
            countDown: {
                id: 'rmc-count-down',
            },
            score: {
                id: 'rmc-score'
            }
        }
    },
    finishScreen: {
        id: 'rmc-finish-screen',
        title: { // OPTIONAL
            use: false,
            text: 'Tebrikler',
            loseText: '',
            textColor: 'lightblue',
            fontSize: '19px'
        },
        message: { // OPTIONAL
            use: false,
            text: 'İndirim Kazandınız',
            loseText: '',
            textColor: 'darkblue',
            fontSize: '15px'
        },
        couponCode: { // OPTIONAL
            use: true,
            id: 'rmc-coupon-code',
            fontSize: '15px',
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-finish-button',
            text: 'Kodu Kopyala',
            textColor: 'darkblue',
            buttonColor: 'lightblue',
            fontSize: '15px',
            androidLink: '',
            iOSLink: ''
        }
    }
};
/**
 * Init
 */
function initGame(responseConfig) {
    if (utils.getMobileOperatingSystem() == 'iOS') {
        console.log("RUN IOS");
        iOSConfigRegulator(responseConfig)
    }
    else {
        console.log("RUN ANDROID");
        androidConfigRegulator(responseConfig);
    }

    config();
}

function androidConfigRegulator(responseConfig) {
    responseConfig = JSON.parse(responseConfig)
    responseConfig.actiondata.ExtendedProps = JSON.parse(unescape(responseConfig.actiondata.ExtendedProps))

    console.log(responseConfig);

    const res = responseConfig.actiondata;
    const ext = res.ExtendedProps;

    promoCodeCalculator(res.promo_codes)

    // General data
    generalData.difficulty = parseInt(res.game_elements.downward_speed);
    generalData.basketImg = res.game_elements.gift_catcher_image;
    generalData.bgColor = ext.background_color;
    generalData.bgImg = ext.background_image;
    generalData.closeButtonColor = ext.close_button_color;
    generalData.fontName = ext.font_family;
    generalData.sound = res.game_elements.sound_url;
    utils.loadSound();

    if (ext.font_family == 'custom' && utils.getMobileOperatingSystem() == 'Android') {
        generalData.fontName = ext.custom_font_family_android;
        generalData.fontFiles = responseConfig.fontFiles;
        console.log('font files ',generalData.fontFiles);
        addFonts();
    }

    productImgs = res.game_elements.gift_images;
    productSettings.totalProductCount = res.game_elements.number_of_products;

    // Mail Form Optionals
    if (res.mail_subscription) {
        activePageData.mailSubsScreen = true;

        if (res.mail_subscription_form.title) {
            componentsData.mailSubsScreen.title.use = true;
            componentsData.mailSubsScreen.title.text = slashController(res.mail_subscription_form.title);
            componentsData.mailSubsScreen.title.textColor = ext.mail_subscription_form.title_text_color;
            componentsData.mailSubsScreen.title.fontSize = fontSizeCalculator(ext.mail_subscription_form.title_text_size) + 'px';
        }

        if (res.mail_subscription_form.message) {
            componentsData.mailSubsScreen.message.use = true;
            componentsData.mailSubsScreen.message.text = slashController(res.mail_subscription_form.message);
            componentsData.mailSubsScreen.message.textColor = ext.mail_subscription_form.text_color;
            componentsData.mailSubsScreen.message.fontSize = fontSizeCalculator(ext.mail_subscription_form.text_size) + 'px';
        }

        if (res.mail_subscription_form.emailpermit_text) {
            componentsData.mailSubsScreen.emailPermission.use = true;
            componentsData.mailSubsScreen.emailPermission.text = slashController(res.mail_subscription_form.emailpermit_text);
            componentsData.mailSubsScreen.emailPermission.fontSize = fontSizeCalculator(ext.mail_subscription_form.emailpermit_text_size) + 'px';
            componentsData.mailSubsScreen.emailPermission.url = ext.mail_subscription_form.emailpermit_text_url;
        }

        if (res.mail_subscription_form.consent_text) {
            componentsData.mailSubsScreen.secondPermission.use = true;
            componentsData.mailSubsScreen.secondPermission.text = slashController(res.mail_subscription_form.consent_text);
            componentsData.mailSubsScreen.secondPermission.fontSize = fontSizeCalculator(ext.mail_subscription_form.consent_text_size) + 'px';
            componentsData.mailSubsScreen.secondPermission.url = ext.mail_subscription_form.consent_text_url;
        }
    }

    // Mail Form Required
    componentsData.mailSubsScreen.button.text = slashController(res.mail_subscription_form.button_label);
    componentsData.mailSubsScreen.button.textColor = ext.mail_subscription_form.button_text_color;
    componentsData.mailSubsScreen.button.buttonColor = ext.mail_subscription_form.button_color;
    componentsData.mailSubsScreen.button.fontSize = fontSizeCalculator(ext.mail_subscription_form.button_text_size) + 'px';
    componentsData.mailSubsScreen.emailInput.placeHolder = res.mail_subscription_form.placeholder;
    componentsData.mailSubsScreen.alerts.check_consent_message = slashController(res.mail_subscription_form.check_consent_message);
    componentsData.mailSubsScreen.alerts.invalid_email_message = slashController(res.mail_subscription_form.invalid_email_message);

    // Rules Screen Optionals
    if (res.gamification_ruless) { ///
        activePageData.rulesScreen = true;

        componentsData.rulesScreen.bgImage = res.gamification_rules.background_image
        componentsData.rulesScreen.button.text = slashController(res.gamification_rules.button_label)
        componentsData.rulesScreen.button.textColor = ext.gamification_rules.button_text_color;
        componentsData.rulesScreen.button.buttonColor = ext.gamification_rules.button_color;
        componentsData.rulesScreen.button.fontSize = fontSizeCalculator(ext.gamification_rules.button_text_size) + 'px';
    }

    // Game Screen
    // componentsData.scoreboard.fontSize
    // componentsData.scoreboard.fontColor
    componentsData.gameScreen.scoreboard.background = ext.game_elements.scoreboard_background_color;
    componentsData.gameScreen.scoreboard.type = ext.game_elements.scoreboard_shape;

    if (componentsData.gameScreen.scoreboard.type == "") componentsData.gameScreen.scoreboard.type = "roundedcorners"

    if (res.game_result_elements.title) {
        componentsData.finishScreen.title.use = true
        componentsData.finishScreen.title.text = slashController(res.game_result_elements.title)
        componentsData.finishScreen.title.fontSize = fontSizeCalculator(ext.game_result_elements.title_text_size) + 'px'
        componentsData.finishScreen.title.textColor = ext.game_result_elements.title_text_color

    }
    if (res.game_result_elements.message) {
        componentsData.finishScreen.message.use = true
        componentsData.finishScreen.message.text = slashController(res.game_result_elements.message)
        componentsData.finishScreen.message.fontSize = fontSizeCalculator(ext.game_result_elements.text_size) + 'px'
        componentsData.finishScreen.message.textColor = ext.game_result_elements.text_color
    }

    componentsData.finishScreen.button.text = slashController(res.copybutton_label);
    componentsData.finishScreen.button.textColor = ext.copybutton_text_color;
    componentsData.finishScreen.button.fontSize = fontSizeCalculator(ext.copybutton_text_size) + 'px';
    componentsData.finishScreen.button.buttonColor = ext.copybutton_color;
    componentsData.finishScreen.button.androidLink = res.android_lnk;
    componentsData.finishScreen.button.iOSLink = res.ios_lnk;
}

function iOSConfigRegulator(responseConfig) {
    console.log(responseConfig)


    const res = responseConfig;

    promoCodeCalculator(res.promoCodes)

    // // General data
    generalData.difficulty = parseInt(res.gameElements.downwardSpeed);
    generalData.basketImg = res.gameElements.giftCatcherImage;
    generalData.bgColor = res.background_color;
    generalData.bgImg = res.backgroundImage;
    generalData.closeButtonColor = res.close_button_color;
    generalData.fontName = res.font_family;
    generalData.sound = res.gameElements.soundUrl;
    utils.loadSound();

    if (res.custom_font_family_ios == 'custom' && utils.getMobileOperatingSystem() == 'iOS') {
        generalData.fontName = res.custom_font_family_ios;
    }

    productImgs = res.gameElements.giftImages;
    productSettings.totalProductCount = res.gameElements.numberOfProducts;

    // // Mail Form Optionals
    if (res.mailSubscription) {
        activePageData.mailSubsScreen = true;

        if (res.mailSubscriptionForm.title) {
            componentsData.mailSubsScreen.title.use = true;
            componentsData.mailSubsScreen.title.text = slashController(res.mailSubscriptionForm.title);
            componentsData.mailSubsScreen.title.textColor = res.mailExtendedProps.titleTextColor;
            componentsData.mailSubsScreen.title.fontSize = fontSizeCalculator(res.mailExtendedProps.titleTextSize) + 'px';
        }

        if (res.mailSubscriptionForm.message) {
            componentsData.mailSubsScreen.message.use = true;
            componentsData.mailSubsScreen.message.text = slashController(res.mailSubscriptionForm.message);
            componentsData.mailSubsScreen.message.textColor = res.mailExtendedProps.textColor;
            componentsData.mailSubsScreen.message.fontSize = fontSizeCalculator(res.mailExtendedProps.textSize) + 'px';
        }

        if (res.mailSubscriptionForm.emailPermitText) {
            componentsData.mailSubsScreen.emailPermission.use = true;
            componentsData.mailSubsScreen.emailPermission.text = slashController(res.mailSubscriptionForm.emailPermitText);
            componentsData.mailSubsScreen.emailPermission.fontSize = fontSizeCalculator(res.mailExtendedProps.emailPermitTextSize) + 'px';
            componentsData.mailSubsScreen.emailPermission.url = res.mailExtendedProps.emailPermitTextUrl;
        }

        if (res.mailSubscriptionForm.consentText) {
            componentsData.mailSubsScreen.secondPermission.use = true;
            componentsData.mailSubsScreen.secondPermission.text = slashController(res.mailSubscriptionForm.consentText);
            componentsData.mailSubsScreen.secondPermission.fontSize = fontSizeCalculator(res.mailExtendedProps.consentTextSize) + 'px';
            componentsData.mailSubsScreen.secondPermission.url = res.mailExtendedProps.consentTextUrl;
        }
    }

    // // Mail Form Required
    componentsData.mailSubsScreen.button.text = slashController(res.mailSubscriptionForm.buttonTitle);
    componentsData.mailSubsScreen.button.textColor = res.mailExtendedProps.buttonTextColor;
    componentsData.mailSubsScreen.button.buttonColor = res.mailExtendedProps.buttonColor;
    componentsData.mailSubsScreen.button.fontSize = fontSizeCalculator(res.mailExtendedProps.buttonTextSize) + 'px';
    componentsData.mailSubsScreen.emailInput.placeHolder = res.mailSubscriptionForm.placeholder;
    componentsData.mailSubsScreen.alerts.check_consent_message = slashController(res.mailSubscriptionForm.checkConsentMessage);
    componentsData.mailSubsScreen.alerts.invalid_email_message = slashController(res.mailSubscriptionForm.invalidEmailMessage);

    // // Rules Screen Optionals
    if (res.gamificationRules) {
        activePageData.rulesScreen = true;

        componentsData.rulesScreen.bgImage = res.gamificationRules.backgroundImage
        componentsData.rulesScreen.button.text = slashController(res.gamificationRules.buttonLabel)
        componentsData.rulesScreen.button.textColor = res.gamificationRulesExtended.buttonTextColor;
        componentsData.rulesScreen.button.buttonColor = res.gamificationRulesExtended.buttonColor;
        componentsData.rulesScreen.button.fontSize = fontSizeCalculator(res.gamificationRulesExtended.buttonTextSize) + 'px';
    }

    // Game Screen
    // componentsData.scoreboard.fontSize
    // componentsData.scoreboard.fontColor
    componentsData.gameScreen.scoreboard.background = res.gameElementsExtended.scoreboardBackgroundColor;
    componentsData.gameScreen.scoreboard.type = res.gameElementsExtended.scoreboardShape;

    if (componentsData.gameScreen.scoreboard.type == "") componentsData.gameScreen.scoreboard.type = "roundedcorners"

    if (res.gameResultElements.title) {
        componentsData.finishScreen.title.use = true
        componentsData.finishScreen.title.text = slashController(res.gameResultElements.title)
        componentsData.finishScreen.title.fontSize = fontSizeCalculator(res.gameResultElementsExtended.titleTextSize) + 'px'
        componentsData.finishScreen.title.textColor = res.gameResultElementsExtended.titleTextColor

    }
    if (res.gameResultElements.message) {
        componentsData.finishScreen.message.use = true
        componentsData.finishScreen.message.text = slashController(res.gameResultElements.message)
        componentsData.finishScreen.message.fontSize = fontSizeCalculator(res.gameResultElementsExtended.textSize) + 'px'
        componentsData.finishScreen.message.textColor = res.gameResultElementsExtended.textColor
    }

    componentsData.finishScreen.button.text = slashController(res.copybutton_label);
    componentsData.finishScreen.button.textColor = res.copybutton_text_color;
    componentsData.finishScreen.button.fontSize = fontSizeCalculator(res.copybutton_text_size) + 'px';
    componentsData.finishScreen.button.buttonColor = res.copybutton_color;
    componentsData.finishScreen.button.iOSLink = res.ios_lnk;
}

/**
 * Start configs
 */
function config() {
    document.body.setAttribute('style', '-webkit-user-select:none');
    setDifficulty();
    utils.calculateTotalDuration();
    pageChecker();
    createCloseButton();
}

/**
 * Start page check
 */
function pageChecker() {
    createMainComponents();
    if (activePageData.mailSubsScreen) {
        createMailSubsScreen()
    }
    else if (!activePageData.mailSubsScreen && activePageData.rulesScreen) {
        createRulesScreen()
    }
    else {
        createGameScreen()
        createScoreBoard()
    }
}

/*
 * Main(container) component
 */
function createMainComponents() {
    MAIN_COMPONENT.id = generalData.id;
    MAIN_COMPONENT.style.width = "100%";
    MAIN_COMPONENT.style.height = "100%";
    MAIN_COMPONENT.style.top = "0";
    MAIN_COMPONENT.style.left = "0";
    MAIN_COMPONENT.style.zIndex = "9999";
    MAIN_COMPONENT.style.position = "absolute";
    document.body.appendChild(MAIN_COMPONENT);
}

/**
 * Mail subscribe form screen
 */
function createMailSubsScreen() {
    var mailSubsScreen = document.createElement("DIV");
    mailSubsScreen.id = componentsData.mailSubsScreen.id;
    mailSubsScreen.style.width = "100%";
    mailSubsScreen.style.height = "100%";
    mailSubsScreen.style.backgroundColor = generalData.bgColor;
    mailSubsScreen.style.backgroundImage = "url('" + generalData.bgImg + "')";
    mailSubsScreen.style.backgroundRepeat = "no-repeat";
    mailSubsScreen.style.backgroundSize = "cover";
    mailSubsScreen.style.backgroundPosition = "center";
    mailSubsScreen.style.transition = "all 1s";
    mailSubsScreen.style.position = "fixed";
    mailSubsScreen.style.top = "0";
    mailSubsScreen.style.left = "0";
    mailSubsScreen.style.zIndex = "998";

    var container = document.createElement("DIV");
    container.id = "rmc-container";
    container.style.width = "80%";
    container.style.height = "auto";
    container.style.position = "absolute";
    container.style.transform = "translate(-50%, -50%)";
    container.style.top = "50%";
    container.style.left = "50%";
    container.style.textAlign = "center";

    if (componentsData.mailSubsScreen.title.use) {
        var title = document.createElement("DIV");
        title.id = "rmc-mail-subs-title";
        title.style.color = componentsData.mailSubsScreen.title.textColor;
        title.style.fontSize = componentsData.mailSubsScreen.title.fontSize;
        title.style.display = "inline-block";
        title.style.width = "100%";
        title.style.margin = "15px 0";
        title.style.fontFamily = generalData.fontName;
        title.innerText = componentsData.mailSubsScreen.title.text;
        container.appendChild(title);
    }

    if (componentsData.mailSubsScreen.message.use) {
        var message = document.createElement("DIV");
        message.id = "rmc-mail-subs-message";
        message.style.color = componentsData.mailSubsScreen.message.textColor;
        message.style.fontSize = componentsData.mailSubsScreen.message.fontSize;
        message.style.display = "inline-block";
        message.style.width = "100%";
        message.style.margin = "15px 0";
        message.style.fontFamily = generalData.fontName;
        message.innerText = componentsData.mailSubsScreen.message.text;
        container.appendChild(message);
    }


    var input = document.createElement("INPUT");
    input.setAttribute("type", "email");
    input.setAttribute("placeholder", componentsData.mailSubsScreen.emailInput.placeHolder);
    input.id = componentsData.mailSubsScreen.emailInput.id;
    input.style.backgroundColor = "white";
    input.style.width = "100%";
    input.style.padding = "9px";
    input.style.border = "1px solid " + generalData.fontColor;
    input.style.borderRadius = generalData.borderRadius;
    input.style.maxWidth = "-webkit-fill-available";
    input.style.fontSize = "19px";
    input.style.fontWeight = "bold";
    input.style.margin = "15px 0";
    input.style.color = generalData.fontColor;
    input.style.marginBottom = "12px";
    input.style.display = "inline-block";
    input.style.fontFamily = generalData.fontName;
    container.appendChild(input);

    var emailAlert = document.createElement("div");
    emailAlert.id = 'emailAlert';
    container.appendChild(emailAlert);



    if (componentsData.mailSubsScreen.emailPermission.use) {
        var emailPermission = document.createElement("DIV");
        emailPermission.style.color = "black";
        emailPermission.style.fontSize = "13px";
        emailPermission.style.margin = "15px 0";
        emailPermission.style.width = "100%";
        emailPermission.style.display = "flex";
        emailPermission.style.alignItems = "center";

        emailPermission.innerHTML = "<input style='width:20px;height:20px;display:block;margin-right:7px;float:left' id='" + componentsData.mailSubsScreen.emailPermission.id + "' type='checkbox'>\
        <a style='font-size:"+ componentsData.mailSubsScreen.emailPermission.fontSize + ";text-decoration: underline;color: black; font-family:" + generalData.fontName + "'\
        href='"+ componentsData.mailSubsScreen.emailPermission.url + "'>" + componentsData.mailSubsScreen.emailPermission.text;
        container.appendChild(emailPermission);

        var checkboxAlert1 = document.createElement("div");
        checkboxAlert1.id = 'checkboxAlert1';
        container.appendChild(checkboxAlert1);
    }

    if (componentsData.mailSubsScreen.emailPermission.use) {
        var secondPermission = document.createElement("DIV");
        secondPermission.style.color = "black";
        secondPermission.style.fontSize = "13px";
        secondPermission.style.margin = "15px 0";
        secondPermission.style.width = "100%";
        secondPermission.style.display = "flex";
        secondPermission.style.alignItems = "center";

        secondPermission.innerHTML = "<input style='width:20px;height:20px;display:block;margin-right:7px;float:left' id='" + componentsData.mailSubsScreen.secondPermission.id + "' type='checkbox' >" +
            "<a style='font-size:" + componentsData.mailSubsScreen.emailPermission.fontSize + ";text-decoration: underline;color: black; font-family:" + generalData.fontName + "'\
                href='"+ componentsData.mailSubsScreen.secondPermission.url + "'>\
                "+ componentsData.mailSubsScreen.secondPermission.text;
        container.appendChild(secondPermission);

        var checkboxAlert2 = document.createElement("div");
        checkboxAlert2.id = 'checkboxAlert2';
        container.appendChild(checkboxAlert2);
    }

    var submit = document.createElement("button");
    submit.id = componentsData.mailSubsScreen.button.id;
    submit.style.backgroundColor = componentsData.mailSubsScreen.button.buttonColor;
    submit.style.color = componentsData.mailSubsScreen.button.textColor;
    submit.style.padding = "15px 30px";
    submit.style.fontSize = componentsData.mailSubsScreen.button.fontSize;
    submit.style.borderRadius = generalData.borderRadius;
    submit.style.border = 0;
    submit.style.position = "absolute";
    submit.style.bottom = "70px";
    submit.style.left = "50%";
    submit.style.width = "fit-content";
    submit.style.transform = "translate(-50%, 0%)";
    submit.style.cursor = "pointer";
    submit.style.fontWeight = "bolder";
    submit.style.fontFamily = generalData.fontName;
    submit.innerText = componentsData.mailSubsScreen.button.text;

    activePageData.rulesScreen && createRulesScreen();

    submit.addEventListener("click", function () {
        if (emailChecker()) {
            removeAlert("emailAlert")
            if (emailPermitChecker() && secondPermitChecker()) {
                utils.subscribe(document.querySelector("#" + componentsData.mailSubsScreen.emailInput.id).value.toLowerCase());
                if (document.querySelector("#" + componentsData.mailSubsScreen.id)) {
                    document.querySelector("#" + componentsData.mailSubsScreen.id).remove();
                    if (!activePageData.rulesScreen) {
                        createGameScreen();
                        createScoreBoard();
                    }
                }
            } else {
                if (!emailPermitChecker()) {
                    alertChecker(componentsData.mailSubsScreen.alerts.check_consent_message, 'checkboxAlert1')
                }
                else {
                    removeAlert("checkboxAlert1")
                }
                if (!secondPermitChecker()) {
                    alertChecker(componentsData.mailSubsScreen.alerts.check_consent_message, 'checkboxAlert2')
                }
                else {
                    removeAlert("checkboxAlert2")
                }
            }
        } else {
            alertChecker(componentsData.mailSubsScreen.alerts.invalid_email_message, 'emailAlert')
        }
    });

    mailSubsScreen.appendChild(submit);
    mailSubsScreen.appendChild(container);
    MAIN_COMPONENT.appendChild(mailSubsScreen);
}

function createAlert(text, id) {
    if (!document.querySelector('#' + id).innerText) {
        var alert = document.createElement("div");
        alert.innerText = text;
        alert.style.width = "100%";
        alert.style.padding = "5px 10px";
        alert.style.zIndex = "999";
        alert.style.textAlign = "left";
        alert.style.color = "#000";
        alert.style.fontSize= "14px";
        alert.style.fontFamily = generalData.fontName;
        alert.style.transform = "translate3d(0,0,3px)";

        document.querySelector('#' + id).appendChild(alert)
    }
    else {

    }
}

function removeAlert(id) {
    if (document.querySelector('#' + id)) {
        document.querySelector('#' + id).innerText = ""
    }
}

function alertChecker(text, id) {
    switch (id) {
        case "checkboxAlert1":
            if (emailPermitChecker()) {
                document.querySelector('#' + id).innerText = ""
            } else {
                createAlert(text, id)
            }
            break;
        case "checkboxAlert2":
            if (secondPermitChecker()) {
                document.querySelector('#' + id).innerText = ""
            } else {
                createAlert(text, id)
            }
            break;
        case "emailAlert":
            if (emailChecker()) {
                document.querySelector('#' + id).innerText = ""
            } else {
                createAlert(text, id)
            }
            break;
        default:
            break;
    }
}

function emailChecker() {
    if (document.querySelector("#" + componentsData.mailSubsScreen.emailInput.id)) {
        var email = document.querySelector("#" + componentsData.mailSubsScreen.emailInput.id).value.toLowerCase();
        var pattern = new RegExp("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}");
        var emailStatus = pattern.test(email);
        if (emailStatus == true) {
            console.log("email checker", true)
            return true
        }
        else {
            console.log("email checker", false)
            return false
        }
    }
    else {
        console.log("email checker", false)
        return false
    }
}

function emailPermitChecker() {
    if (document.querySelector("#" + componentsData.mailSubsScreen.emailPermission.id)) {
        if (document.querySelector("#" + componentsData.mailSubsScreen.emailPermission.id).checked) {
            return true
        }
        else {
            return false
        }
    }
    else {
        return false
    }
}

function secondPermitChecker() {
    if (document.querySelector("#" + componentsData.mailSubsScreen.secondPermission.id)) {
        if (document.querySelector("#" + componentsData.mailSubsScreen.secondPermission.id).checked) {
            return true
        }
        else {
            return false
        }
    }
    else {
        return false
    }
}





/**
 * Close button
 */
function createCloseButton() {
    var closeButton = document.createElement("BUTTON");
    closeButton.id = generalData.closeButtonId;
    closeButton.innerHTML = "&#10006;";
    closeButton.style.position = "absolute";
    closeButton.style.right = "0px";
    closeButton.style.top = "0px";
    closeButton.style.border = "0";
    closeButton.style.color = generalData.closeButtonColor;
    closeButton.style.padding = "5px 10px";
    closeButton.style.cursor = "pointer";
    closeButton.style.fontSize = "29px";
    closeButton.style.borderRadius = generalData.borderRadius;
    closeButton.style.backgroundColor = 'rgba(0,0,0,0)';
    closeButton.style.zIndex = "999";
    closeButton.style.transform = "translate3d(0,0,3px)";

    closeButton.addEventListener("click", function () {
        utils.close();
        document.querySelector("#" + generalData.id) ? document.querySelector("#" + generalData.id).remove() : null;
        document.querySelector("#" + componentsData.mailSubsScreen.id) ? document.querySelector("#" + componentsData.mailSubsScreen.id).remove() : null;
        document.querySelector("#" + componentsData.rulesScreen.id) ? document.querySelector("#" + componentsData.rulesScreen.id).remove() : null;
        document.querySelector("#" + generalData.closeButtonId) ? document.querySelector("#" + generalData.closeButtonId).remove() : null;
        document.documentElement.style.overflow = 'auto';
        utils.pauseSound();
        console.log('Oyun Kapatıldı');
    });

    MAIN_COMPONENT.appendChild(closeButton);
}

/**
 * Rules screen
 */
function createRulesScreen() {
    var rulesScreen = document.createElement("DIV");
    rulesScreen.id = componentsData.rulesScreen.id;
    rulesScreen.style.width = "100%";
    rulesScreen.style.height = "100%";
    rulesScreen.style.position = "fixed";
    rulesScreen.style.transition = "all 1s";
    rulesScreen.style.top = "0";
    rulesScreen.style.left = "0";
    rulesScreen.style.zIndex = "997";

    var rulesIMG = document.createElement("DIV");
    rulesIMG.style.width = "100%";
    rulesIMG.style.height = "100%";
    rulesIMG.style.backgroundImage = "url('" + (componentsData.rulesScreen.bgImage ? componentsData.rulesScreen.bgImage : generalData.bgImg) + "')";
    rulesIMG.style.backgroundRepeat = "no-repeat";
    rulesIMG.style.backgroundSize = "contain";
    rulesIMG.style.backgroundPosition = "center";
    rulesIMG.style.position = "fixed";
    rulesIMG.style.transition = "all 1s";
    rulesIMG.style.top = "0";
    rulesIMG.style.left = "0";
    rulesIMG.style.zIndex = "2";
    rulesIMG.style.transform = " translate3d(0,0,3px)";

    if (componentsData.rulesScreen.bgImage) {
        var rulesScreenBluredBG = document.createElement("DIV");
        rulesScreenBluredBG.style.width = "100%";
        rulesScreenBluredBG.style.height = "100%";
        rulesScreenBluredBG.style.backgroundImage = "url('" + componentsData.rulesScreen.bgImage + "')";
        rulesScreenBluredBG.style.backgroundRepeat = "no-repeat";
        rulesScreenBluredBG.style.backgroundSize = "center";
        rulesScreenBluredBG.style.backgroundPosition = "center";
        rulesScreenBluredBG.style.position = "fixed";
        rulesScreenBluredBG.style.transition = "all 1s";
        rulesScreenBluredBG.style.top = "0";
        rulesScreenBluredBG.style.left = "0";
        rulesScreenBluredBG.style.zIndex = "1";
        rulesScreenBluredBG.style.filter = "blur(35px)";
    }


    var submit = document.createElement("button");
    submit.id = componentsData.rulesScreen.button.id;
    submit.style.backgroundColor = componentsData.rulesScreen.button.buttonColor;
    submit.style.color = componentsData.rulesScreen.button.textColor;
    submit.style.padding = "15px 30px";
    submit.style.fontSize = componentsData.rulesScreen.button.fontSize;
    submit.style.borderRadius = generalData.borderRadius;
    submit.style.border = 0;
    submit.style.position = "absolute";
    submit.style.bottom = "70px";
    submit.style.left = "50%";
    submit.style.width = "fit-content";
    submit.style.transform = "translate(-50%, 0%) translate3d(0,0,3px)";
    submit.style.cursor = "pointer";
    submit.style.zIndex = "3";
    submit.style.fontWeight = "bolder";
    submit.style.fontFamily = generalData.fontName;
    submit.innerText = componentsData.rulesScreen.button.text;

    rulesScreen.appendChild(rulesIMG)
    rulesScreen.appendChild(rulesScreenBluredBG)
    rulesScreen.appendChild(submit);
    MAIN_COMPONENT.appendChild(rulesScreen);


    submit.addEventListener("click", function () {
        document.querySelector("#" + componentsData.rulesScreen.id) ? document.querySelector("#" + componentsData.rulesScreen.id).remove() : null;
        createGameScreen();
        createScoreBoard();
    });


}

/**
 * Game area
 */
function createGameScreen() {
    var gameScreen = document.createElement("DIV");
    gameScreen.id = componentsData.gameScreen.id;
    gameScreen.style.width = "100%";
    gameScreen.style.height = "100%";
    gameScreen.style.backgroundColor = generalData.bgColor;
    gameScreen.style.backgroundImage = "url('" + generalData.bgImg + "')";
    gameScreen.style.backgroundRepeat = "no-repeat";
    gameScreen.style.backgroundSize = "cover";
    gameScreen.style.backgroundPosition = "center";
    gameScreen.style.transition = "all 1s";
    gameScreen.style.position = "fixed";
    gameScreen.style.top = "0";
    gameScreen.style.left = "0";
    gameScreen.style.zIndex = "995";
    MAIN_COMPONENT.appendChild(gameScreen);
    console.log("oyun başladı");


    setTimeout(() => {
        createBasket();
        startGame();
        utils.sendReport();
    }, 1);
}

/**
 * Basket 
 */
function createBasket() {
    var bar = document.createElement("div");
    barStatu = true;
    isRun = true;

    bar.id = gameSettings.basketId;
    bar.style.width = gameSettings.basketSize + 'px';
    bar.style.height = gameSettings.basketSize + 'px';
    bar.style.position = "fixed";
    bar.style.bottom = "0px";
    bar.style.left = "50%";
    bar.style.zIndex = "9999";
    bar.style.backgroundRepeat = "no-repeat";
    bar.style.backgroundSize = "contain";
    bar.style.backgroundImage = "url('" + generalData.basketImg + "')";
    bar.style.transition = "0s all";
    bar.style.animation = "none";

    window.addEventListener("touchstart", function () {
        if (isRun) {
            document.documentElement.style.overflow = 'hidden';
        } else {
            document.documentElement.style.overflow = 'auto';
        }
    });

    bar.addEventListener("touchstart", function (e) {
        if (isRun == true) {
            bar.style.left = (e.touches[0].clientX - (parseFloat(bar.style.width) / 2)) + "px";
        }
    });

    window.addEventListener("touchmove", function (e) {
        if (isRun == true) {
            bar.style.left = (e.touches[0].clientX - (parseFloat(bar.style.width) / 2)) + "px";
        }
    });

    window.addEventListener("touchend", function () {

    });

    bar.addEventListener("mousedown", function (e) {
        if (barStatu == true) {
            bar.style.left = (e.clientX - (parseFloat(bar.style.width) / 2)) + "px";
        }
    });

    window.addEventListener("mousemove", function (e) {
        if (barStatu == true) {
            bar.style.left = (e.clientX - (parseFloat(bar.style.width) / 2)) + "px";
        }
    });

    window.addEventListener("mouseup", function (e) {

    });

    document.querySelector('#' + componentsData.gameScreen.id).appendChild(bar);
}

/**
 * Function that creates products to fall
*/
function createProduct(id, url) {
    var div = document.createElement('div');
    div.id = id;
    div.style.width = productSettings.productSize + 'px';
    div.style.height = productSettings.productSize + 'px';
    div.style.left = Math.floor((Math.random() * (window.innerWidth - 100)) + 1) + 'px';
    div.style.bottom = HEIGHT + productSettings.productSize + 'px';
    div.style.position = 'fixed';
    div.style.borderRadius = '50px';
    !gameSettings.lowPowerMode && (div.style.background = 'green');
    div.style.backgroundRepeat = 'no-repeat';
    div.style.backgroundSize = 'contain';
    div.style.backgroundImage = "url('" + url + "')";
    div.style.zIndex = '99';
    div.style.transition = 'all .' + difficulty.use.boxTransition + 's cubic-bezier(1, 0.99, 0, -0.02) 0s';
    div.style.display = 'inline-block';
    div.style.transform = 'rotate(0deg)';
    div.setAttribute('catchable', true);

    return div;
}

function startGame() {
    for (let i = 0; i < productSettings.totalProductCount; i++) {
        setTimeout(function () {
            if (document.querySelector('#' + componentsData.gameScreen.id)) {
                document.querySelector('#' + componentsData.gameScreen.id).appendChild(createProduct(productSettings.productIdPrefix + i, productImgs[Math.floor(Math.random() * productImgs.length)]));
            }
        }, productSettings.productTimeOutArray[i]);
    }

    updateProductOperation();
}

function updateProduct(id) {
    if (document.querySelector('#' + id)) {
        var drop = document.querySelector('#' + id);
        drop.style.bottom = (parseFloat(drop.style.bottom) - difficulty.use.boxBottom) + "px";
        if (productSettings.turn) {
            var deg = parseFloat(drop.style.transform.substr(7, drop.style.transform.length));
            drop.style.transform = "rotate(" + (deg + 30) + "deg)";
        }

        if (parseFloat(drop.style.bottom) <= gameSettings.intersectionPoint) {
            var productLeftCorner = utils.getOffset(document.querySelector('#' + id)).left;
            var basketLeftCorner = utils.getOffset(document.querySelector('#' + gameSettings.basketId)).left;
            let catchable = 'true';
            if (!gameSettings.lowPowerMode) {
                catchable = drop.getAttribute('catchable');
            }

            if (
                (productLeftCorner + (productSettings.productSize / 2)) >= basketLeftCorner &&
                productLeftCorner <= (basketLeftCorner + gameSettings.basketSize) &&
                (productLeftCorner + (productSettings.productSize / 2)) <= (basketLeftCorner + gameSettings.basketSize) &&
                catchable == 'true'
            ) {
                SCORE++;

                document.querySelector('#' + componentsData.gameScreen.scoreboard.score.id).innerHTML = SCORE + ' PUAN';
                console.log('SCORE:', SCORE)
                drop.remove();
            }
            else {
                if (!gameSettings.lowPowerMode) {
                    catchable == 'true' && drop.setAttribute('catchable', false);

                    drop.style.background = 'red';
                    if (parseFloat(drop.style.bottom) < -200) {
                        drop.remove();
                    }
                }
            }
            if (gameSettings.lowPowerMode) {
                drop.remove();
            }
            if (id === productSettings.productIdPrefix + (productSettings.totalProductCount - 1)) {
                if (!document.querySelector('#' + productSettings.productIdPrefix + (productSettings.totalProductCount - 1))) {
                    clearInterval(UPDATE_PRODUCT_INTERVAL);
                    isRun = false;
                    barStatu = false;
                    console.log("Game finish");
                    finish();
                }
            }
        }
    }
}

/**
 * Operations during the flow of products
 */
function updateProductOperation() {
    UPDATE_PRODUCT_INTERVAL = setInterval(() => {
        for (let i = 0; i < productSettings.totalProductCount; i++) {
            updateProduct(productSettings.productIdPrefix + i)
        }
    }, difficulty.use.boxInterval);
}

/**
 * Score board
 */
function createScoreBoard() {
    var dashboard = document.createElement("div");
    dashboard.id = componentsData.gameScreen.scoreboard.id;
    dashboard.style.padding = componentsData.gameScreen.scoreboard.type === 'circle' ? '40px 0px' : '10px 0px';
    dashboard.style.position = "fixed";
    dashboard.style.color = "white";
    dashboard.style.textAlign = "center";
    dashboard.style.background = componentsData.gameScreen.scoreboard.background;
    dashboard.style.width = "150px";
    dashboard.style.maxWidth = "150px";
    dashboard.style.margin = "5px";
    dashboard.style.backgroundSize = "contain";
    dashboard.style.left = "0";
    dashboard.style.fontSize = "24px";
    dashboard.style.transition = "1s all";
    componentsData.gameScreen.scoreboard.type !== 'square' && (dashboard.style.borderRadius = componentsData.gameScreen.scoreboard.type == 'circle' ? '50%' : '15px')

    var _duration = document.createElement("div");
    _duration.innerHTML = DURATION;
    _duration.id = componentsData.gameScreen.scoreboard.countDown.id;
    _duration.style.fontWeight = "bold";
    _duration.style.marginBottom = "10px";
    _duration.style.transition = "1s all";
    _duration.style.width = "150px";
    _duration.style.maxWidth = "150px";
    _duration.style.color = componentsData.gameScreen.scoreboard.fontColor;
    _duration.style.fontFamily = generalData.fontName;
    _duration.style.fontSize = componentsData.gameScreen.scoreboard.fontSize;
    dashboard.appendChild(_duration);

    var _score = document.createElement("DIV");
    _score.id = componentsData.gameScreen.scoreboard.score.id;
    _score.innerHTML = SCORE + ' PUAN';
    _score.style.transition = "1s all";
    _score.style.color = componentsData.gameScreen.scoreboard.fontColor;
    _score.style.fontFamily = generalData.fontName;
    _score.style.fontSize = componentsData.gameScreen.scoreboard.fontSize;

    dashboard.appendChild(_score);
    document.querySelector('#' + componentsData.gameScreen.id).appendChild(dashboard);
    utils.startCountDown(document.querySelector('#' + _duration.id), DURATION);
}

function createFinishScreen() {
    var finishScreen = document.createElement("DIV");
    finishScreen.id = componentsData.finishScreen.id;
    finishScreen.style.width = "100%";
    finishScreen.style.height = "100%";
    finishScreen.style.backgroundColor = generalData.bgColor;
    finishScreen.style.backgroundImage = "url('" + generalData.bgImg + "')";
    finishScreen.style.backgroundRepeat = "no-repeat";
    finishScreen.style.backgroundSize = "cover";
    finishScreen.style.backgroundPosition = "center";
    finishScreen.style.transition = "all 1s";
    finishScreen.style.position = "fixed";
    finishScreen.style.top = "0";
    finishScreen.style.left = "0";
    finishScreen.style.zIndex = "994";

    var container = document.createElement("DIV");
    container.id = "rmc-finish-container";
    container.style.width = "100%";
    container.style.height = "auto";
    container.style.position = "absolute";
    container.style.transform = "translate(-50%, -50%)";
    container.style.top = "50%";
    container.style.left = "50%";
    container.style.textAlign = "center";

    if (componentsData.finishScreen.title.use) {
        var title = document.createElement("DIV");
        title.id = "rmc-finish-title";
        title.style.color = componentsData.finishScreen.title.textColor;
        title.style.fontSize = componentsData.finishScreen.title.fontSize;
        title.style.display = "inline-block";
        title.style.margin = "15px 0";
        title.style.width = 'inherit';
        title.style.fontFamily = generalData.fontName;
        title.innerText = utils.winCheck() ? componentsData.finishScreen.title.text : componentsData.finishScreen.title.loseText;
        container.appendChild(title);
    }

    if (componentsData.finishScreen.message.use) {
        var message = document.createElement("DIV");
        message.id = "rmc-finish-message";
        message.style.color = componentsData.finishScreen.message.textColor;
        message.style.fontSize = componentsData.finishScreen.message.fontSize;
        message.style.display = "inline-block";
        message.style.margin = "15px 0";
        message.style.width = 'inherit';
        message.style.fontFamily = generalData.fontName;
        message.innerText = utils.winCheck() ? componentsData.finishScreen.message.text : componentsData.finishScreen.message.loseText;
        container.appendChild(message);
    }

    var _score = document.createElement("DIV");
    _score.id = 'rmc-finish-finish';
    _score.innerHTML = SCORE + ' PUAN';
    _score.innerHTML += '<br> ' + couponCodes[SCORE];
    _score.style.transition = "1s all";
    _score.style.padding = componentsData.gameScreen.scoreboard.type === 'circle' ? (utils.winCheck() ? '40px 30px' : '70px 20px') : '15px 10px';
    _score.style.width = 'fit-content';
    _score.style.margin = '0 auto';
    _score.style.color = componentsData.gameScreen.scoreboard.fontColor;
    _score.style.fontFamily = generalData.fontName;
    _score.style.fontSize = componentsData.gameScreen.scoreboard.fontSize;
    _score.style.background = componentsData.gameScreen.scoreboard.background;
    componentsData.gameScreen.scoreboard.type !== 'square' && (_score.style.borderRadius = componentsData.gameScreen.scoreboard.type == 'circle' ? '50%' : '15px');
    container.appendChild(_score);

    if (SCORE > 0) {
        var copyButton = document.createElement("button");
        copyButton.id = componentsData.finishScreen.button.id;
        copyButton.style.backgroundColor = componentsData.finishScreen.button.buttonColor;
        copyButton.style.color = componentsData.finishScreen.button.textColor;
        copyButton.style.padding = "15px 30px";
        copyButton.style.fontSize = componentsData.finishScreen.button.fontSize;
        copyButton.style.borderRadius = generalData.borderRadius;
        copyButton.style.border = 0;
        copyButton.style.width = "fit-content";
        copyButton.style.margin = "10px auto";
        copyButton.style.cursor = "pointer";
        copyButton.style.zIndex = "3";
        copyButton.style.fontWeight = "bolder";
        copyButton.style.fontFamily = generalData.fontName;
        copyButton.innerText = componentsData.finishScreen.button.text;

        container.appendChild(copyButton);

        _score.addEventListener('click', function () {
            utils.copyToClipboard();
            utils.pauseSound();
        });

        copyButton.addEventListener('click', function () {
            utils.copyToClipboard();
            utils.pauseSound();
        });

        copyButton.addEventListener("click", function () {
            if (utils.getMobileOperatingSystem() == 'iOS' && componentsData.finishScreen.button.iOSLink)
                location.href = componentsData.finishScreen.button.iOSLink

            // Android link yönlendirme native taraftan yapılıyor
            // if (utils.getMobileOperatingSystem() == 'Android' && componentsData.finishScreen.button.androidLink)
            //     location.href = componentsData.finishScreen.button.androidLink
        });
    }


    finishScreen.appendChild(container);
    MAIN_COMPONENT.appendChild(finishScreen);
}

function getAndroidLink() {
    if (componentsData.finishScreen.button.androidLink) {
        return componentsData.finishScreen.button.androidLink
    } else {
        return ""
    }
}

function finish() {
    createFinishScreen();
    utils.saveCodeGotten()

    document.querySelector('#' + componentsData.gameScreen.id).remove();
}


let utils = {
    randNum: (min, max) => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    getOffset: (el) => {
        var _x = 0;
        var _y = 0;
        while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
            _x += el.offsetLeft - el.scrollLeft;
            _y += el.offsetTop - el.scrollTop;
            el = el.offsetParent;
        }
        return {
            top: _y,
            left: _x
        };
    },
    startCountDown: (elem, seconds) => {
        var that = {};

        that.elem = elem;
        that.seconds = seconds;
        that.totalTime = seconds * 100;
        that.usedTime = 0;
        that.startTime = +new Date();
        that.timer = null;

        that.count = function () {
            that.usedTime = Math.floor((+new Date() - that.startTime) / 10);

            var tt = that.totalTime - that.usedTime;
            if (tt <= 0) {
                that.elem.innerHTML = '00:00.00';
                clearInterval(that.timer);
            } else {
                var mi = Math.floor(tt / (60 * 100));
                var ss = Math.floor((tt - mi * 60 * 100) / 100);
                var ms = tt - Math.floor(tt / 100) * 100;

                that.elem.innerHTML = that.fillZero(mi) + ":" + that.fillZero(ss) + "." + that.fillZero(ms.toFixed(0));
            }
        };


        if (!that.timer) {
            that.timer = setInterval(that.count, 1);
        }

        that.fillZero = function (num) {
            return num < 10 ? '0' + num : num;
        };

        return that;
    },
    calculateTotalDuration: () => {
        let finalProductStartTime = productSettings.productTimeOutArray[productSettings.productTimeOutArray.length - 1];

        let lastProductFinishTime = ((HEIGHT - gameSettings.basketSize) / difficulty.use.boxBottom) * difficulty.use.boxInterval;

        DURATION = (finalProductStartTime + lastProductFinishTime) / 1000;
        console.log('TOTAL SÜRE', DURATION);
    },
    getMobileOperatingSystem: () => {
        var userAgent = navigator.userAgent || navigator.vendor || window.opera;

        if (/windows phone/i.test(userAgent)) {
            return "Windows Phone";
        }

        if (/android/i.test(userAgent)) {
            return "Android";
        }

        if (/iPad|iPhone|iPod|Mac/.test(userAgent) && !window.MSStream) {
            return "iOS";
        }

        return "unknown";
    },
    getBrowser: () => {
        try {


            // Opera 8.0+
            var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
            if (isOpera) return "Opera"

            // Firefox 1.0+
            var isFirefox = typeof InstallTrigger !== 'undefined';
            if (isFirefox) return "Firefox"

            // Safari 3.0+ "[object HTMLElementConstructor]" 
            var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || (typeof safari !== 'undefined' && window['safari'].pushNotification));
            if (!isSafari) isSafari = /iP(ad|hone|od).+Version\/[\d\.]+.*Safari/i.test(navigator.userAgent);

            if (isSafari) return "Safari"

            // Internet Explorer 6-11
            var isIE = /*@cc_on!@*/false || !!document.documentMode;
            if (isIE) return "IE"

            // Edge 20+
            var isEdge = !isIE && !!window.StyleMedia;
            if (isEdge) return "Edge"

            // Chrome 1 - 79
            var isChrome = !!window.chrome && (!!window.chrome.webstore || !!window.chrome.runtime);
            if (isChrome) return "Chrome"

            // Edge (based on chromium) detection
            var isEdgeChromium = isChrome && (navigator.userAgent.indexOf("Edg") != -1);
            if (isEdgeChromium) return "EdgeChromium"

            // Blink engine detection
            // var isBlink = (isChrome || isOpera) && !!window.CSS;

            return "unknown"
        } catch (error) {
            console.log("getBrowser error", error);
            return "unknown"
        }
    },
    winCheck: () => {
        return SCORE > 0 ? true : false
    },
    copyToClipboard: () => {
        console.log("NATIVE COPYCLIPBORD");
        if (window.Android) {
            Android.copyToClipboard(couponCodes[SCORE], getAndroidLink())
        } else if (window.webkit.messageHandlers.eventHandler) {
            window.webkit.messageHandlers.eventHandler.postMessage({
                method: "copyToClipboard",
                couponCode: couponCodes[SCORE]
            })
        }
    },
    sendReport: () => {
        console.log("NATIVE SENDREPORT");
        if (window.Android) {
            Android.sendReport()
        } else if (window.webkit && window.webkit.messageHandlers) {
            window.webkit.messageHandlers.eventHandler.postMessage({
                method: "sendReport"
            })
        }
    },
    close: () => {
        console.log("NATIVE CLOSE");
        if (window.Android) {
            Android.close()
        } else if (window.webkit && window.webkit.messageHandlers) {
            window.webkit.messageHandlers.eventHandler.postMessage({
                method: "close"
            })
        }
    },
    subscribe: (email) => {
        console.log("NATIVE SUBSCRIBE");

        if (!email) return

        if (window.Android) {
            Android.subscribeEmail(email.trim())
        } else if (window.webkit && window.webkit.messageHandlers) {
            window.webkit.messageHandlers.eventHandler.postMessage({
                method: "subscribeEmail",
                email: email.trim()
            })
        }
    },
    saveCodeGotten: () => {
        console.log("NATIVE CODE GOTTEN");
        if (window.Android) {
            Android.saveCodeGotten(couponCodes[SCORE])
        } else if (window.webkit && window.webkit.messageHandlers) {
            window.webkit.messageHandlers.eventHandler.postMessage({
                method: "saveCodeGotten",
                email: couponCodes[SCORE]
            })
        }
    },
    loadSound: () => {
        AUDIO = document.createElement("audio")
        AUDIO.src = generalData.sound;
        AUDIO.currentTime = 0;
        AUDIO.setAttribute("playsinline", true);
        AUDIO.setAttribute("preload", "auto");
        AUDIO.setAttribute("loop", true);
        AUDIO.setAttribute("autoplay", true);
        document.querySelector('head').appendChild(AUDIO);
        try {
            if (utils.getBrowser() == 'Safari') {
                let html = document.querySelector('html');
                html.addEventListener('touchstart', () => { AUDIO.play(); html.removeEventListener('touchstart', () => { }) })
                html.addEventListener('click', () => { AUDIO.play(); html.removeEventListener('click', () => { }) })
            }
            else {
                AUDIO.play();
            }
        } catch (error) {
            console.log("loadSound error", error);
        }
    },
    pauseSound: () => {
        try {
            const audio = document.querySelector("audio")
            if (audio) {
                !audio.paused && audio.pause();
                audio.remove()
            } else {
                console.log("not closed sounnd");
            }
        } catch (error) {
            console.log(error);
        }
    },
};

function promoCodeCalculator(data) {
    console.log(data);
    let codes = { 0: "" }, counter = 1;
    data.forEach(range => {
        if (range.rangebottom == range.rangetop) {
            codes[counter] = range.staticcode;
            counter++;
        }
        else {
            for (let index = range.rangebottom; index <= range.rangetop; index++) {
                codes[counter] = range.staticcode;
                counter++;
            }
        }
    });

    couponCodes = codes;
}

function addFonts() {
	if (generalData.fontFiles === undefined) {
		return
	}
	var addedFontFiles = [];
	for (var fontFileIndex in generalData.fontFiles) {
		var fontFile = generalData.fontFiles[fontFileIndex];
		if (addedFontFiles.includes(fontFile)) {
			continue;
		}
		var fontFamily = fontFile.split(".")[0];
		var newStyle = document.createElement('style');
		var cssContent = "@font-face{font-family:" + fontFamily + ";src:url('" + fontFile + "');}";
		newStyle.appendChild(document.createTextNode(cssContent));
		document.head.appendChild(newStyle);
		addedFontFiles.push(fontFile);
	}
};

function fontSizeCalculator(BEFS) {
    BEFS = parseInt(BEFS)
    switch (BEFS) {
        case 10:
            return 28
            break;
        case 9:
            return 26
            break;
        case 8:
            return 24
            break;
        case 7:
            return 22
            break;
        case 6:
            return 20
            break;
        case 5:
            return 18
            break;
        case 4:
            return 16
            break;
        case 3:
            return 14
            break;
        case 2:
            return 12
            break;
        case 1:
            return 10
            break;
        default:
            return 18
            break;
    }
}

function slashController(text){
    let pos = text.indexOf('\\n');
    while (pos > -1) {
        text = text.replace("\\n", "\n");
        pos = text.indexOf('\\n');
    }
    return text
}

/**
 * Difficulty level check
 */
function setDifficulty() {
    switch (generalData.difficulty) {
        case 1:
            difficulty.use = { ...difficulty.easy };
            timeOuts(1000, 1200);
            break;
        case 2:
            difficulty.use = { ...difficulty.normal };
            timeOuts(700, 900);
            break;
        case 3:
            difficulty.use = { ...difficulty.mid };
            timeOuts(400, 700);
            break;
        case 4:
            difficulty.use = { ...difficulty.hard };
            timeOuts(100, 150);
            break;
        default:
            difficulty.use = { ...difficulty.normal };
            timeOuts(700, 900);
            break;
    }
    console.log(difficulty.use)
}

/**
 * Box timeouts
 */
function timeOuts(minDiff, maxDiff) {
    let beforeNumber = 0;
    for (let i = 0; i < productSettings.totalProductCount; i++) {
        let timeOut = beforeNumber + utils.randNum(minDiff, maxDiff);
        beforeNumber = timeOut;
        productSettings.productTimeOutArray.push(timeOut)
    }
}

