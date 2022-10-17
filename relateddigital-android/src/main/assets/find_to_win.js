let UPDATE_PRODUCT_INTERVAL, SCORE = 0, DURATION, HEIGHT = window.innerHeight, WIDTH = window.innerWidth, PAIR_COUNTER = 0, LAST_CLICKED_CARD_ID = null, PAIR_COUNT = 0, MAX_PAIR_COUNT = 0, CLICKABLE = true, CLICKABLE_DURATION = 1000, PAIRS = [], AUDIO, TIME_INTERVAL;

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
    id: 'rmc-find-to-win',
    bgColor: '',
    bgImg: 'https://picsum.photos/seed/picsum/400/800',
    fontColor: '',
    fontName: 'Helvetica',
    fontFiles: [],
    closeButtonId: 'rmc-close-button',
    closeButton: 'true',
    closeButtonColor: 'black',
    borderRadius: '10px',
    scoreBoardRadius: '5px',
    sound: 'https://bariisarslans.github.io/giftcatchgame/sound.mp3'
};

/**
 * Components defaults
 */
let componentsData = {
    mailSubsScreen: {
        id: "rmc-mail-subs-screen",
        title: { // OPTIONAL
            use: true,
            text: '',
            textColor: '',
            fontSize: ''
        },
        message: { // OPTIONAL
            use: true,
            text: '',
            textColor: '',
            fontSize: ''
        },
        emailPermission: { // OPTIONAL
            use: true,
            id: 'rmc-email-permission-checkbox',
            text: '',
            fontSize: '',
            url: '',
        },
        secondPermission: { // OPTIONAL
            use: true,
            id: 'rmc-second-permission-checkbox',
            text: '',
            fontSize: '',
            url: '',
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-mail-subs-button',
            text: '',
            textColor: '',
            buttonColor: '',
            fontSize: '',
            goScreen: screens.rules,
        },
        emailInput: {
            id: 'rmc-email-input',
            placeHolder: 'Email',
            value: '',
        },
        alerts: {
            invalid_email_message: '',
            check_consent_message: ''
        }
    },
    rulesScreen: {
        bgImage: '',
        id: 'rmc-rules-screen',
        title: { // OPTIONAL
            use: true,
            text: '',
            textColor: '',
            fontSize: ''
        },
        message: { // OPTIONAL
            use: true,
            text: '',
            textColor: '',
            fontSize: ''
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-rules-button',
            text: '',
            textColor: '',
            buttonColor: '',
            fontSize: '',
            goScreen: screens.game,
        },
    },
    gameScreen: {
        id: "rmc-game-screen",
        gameArea: "rmc-game-screen-game-area",
        cards: [],
        scoreboard: {
            id: 'rmc-scoreboard',
            fontSize: '',
            background: '',
            fontColor: '',
            type: 'round', // square | circle | round
            position: 'topLeft', // topLeft | topRight | bottomLeft | bottomRight
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
            use: true,
            text: '',
            loseText: '',
            textColor: '',
            fontSize: ''
        },
        message: { // OPTIONAL
            use: true,
            text: '',
            loseText: '',
            textColor: '',
            fontSize: ''
        },
        score: { // OPTIONAL
            use: true,
            id: 'rmc-finish-score',
            text: '',
        },
        lose: {
            id: 'rmc-finish-lose-img',
            src: '',
            buttonLabel: '',
            loseAndroidLink: '',
            loseIOSLink: '',
            loseButtonColor: '',
            loseButtonTextSize: '',
            loseButtonTextColor: ''
        },
        couponCode: { // OPTIONAL
            use: true,
            id: 'rmc-coupon-code',
            fontSize: '',
        },
        button: { // REQUIRED
            use: true,
            id: 'rmc-finish-button',
            text: '',
            textColor: '',
            buttonColor: '',
            fontSize: '',
            androidLink: '',
            iOSLink: ''
        }
    }
};

/**
 * Card settings
 */
let cardSettings = {
    cardWidth: 150,
    cardHeight: 300,
    cardMargin: 10,
    cardIdPrefix: 'item',
    duration: 500,
    backfaceImg: 'https://picsum.photos/id/29/300/600',
    backfaceColor: 'green',
    emptyBackfaceImg: '',
    emptyBackfaceColor: 'gray',
    emptyFrontImg: '',
    emptyFrontColor: 'gray',
}

/**
 * Game page settins
*/
let gameSettings = {
    duration: 6,
    gameAreaHeight: HEIGHT * 0.7,
    matchedIconEnable: false,
    gameScreenSecondScoreEnable: false
}

/**
 * Coupons
*/
let couponCodes = {
};

let pair = [];

/**
 * Init
 */
function initFindToWinGame(responseConfig) {
    if (utils.getMobileOperatingSystem() == 'iOS') {
        iOSConfigRegulator(responseConfig)
    }
    else {
        androidConfigRegulator(responseConfig);
    }

    config();
}

function androidConfigRegulator(responseConfig) {
    responseConfig = JSON.parse(responseConfig)
    responseConfig.actiondata.ExtendedProps = JSON.parse(unescape(responseConfig.actiondata.ExtendedProps))

    const res = responseConfig.actiondata;
    const ext = res.ExtendedProps;

    const row = res.game_elements.playground_rowcount;
    const col = res.game_elements.playground_columncount;

    const blankCard = ext.game_elements.blankcard_image ? ext.game_elements.blankcard_image : false
    maxPairCalculator(row, col);
    cardSlotAdjuster(row, col, res.game_elements.card_images, blankCard); // buraya boş kart aktif mi bilgisini yolla
    promoCodeCalculator(res.promo_codes);


    // General data
    gameSettings.duration = res.game_elements.duration_of_game;
    generalData.sound = res.game_elements.sound_url;
    generalData.bgColor = ARGBtoRGBA(ext.background_color);
    generalData.bgImg = ext.background_image;
    generalData.closeButtonColor = ARGBtoRGBA(ext.close_button_color);
    generalData.fontName = ext.font_family;
    cardSettings.backfaceImg = ext.game_elements.backofcards_image;
    cardSettings.backfaceColor = ext.game_elements.backofcards_color;
    cardSettings.emptyBackfaceImg = ext.game_elements.blankcard_image;
    cardSettings.emptyFrontImg = ext.game_elements.blankcard_image;

    utils.loadSound();

    if (ext.font_family == 'custom' && utils.getMobileOperatingSystem() == 'Android') {
        generalData.fontName = ext.custom_font_family_android;
        generalData.fontFiles = responseConfig.fontFiles;
        console.log('font files ', generalData.fontFiles);
        addFonts();
    }

    // Mail Form Optionals
    if (res.mail_subscription) {
        activePageData.mailSubsScreen = true;

        if (res.mail_subscription_form.title) {
            componentsData.mailSubsScreen.title.use = true;
            componentsData.mailSubsScreen.title.text = slashController(res.mail_subscription_form.title);
            componentsData.mailSubsScreen.title.textColor = ARGBtoRGBA(ext.mail_subscription_form.title_text_color);
            componentsData.mailSubsScreen.title.fontSize = fontSizeCalculator(ext.mail_subscription_form.title_text_size) + 'px';
        }

        if (res.mail_subscription_form.message) {
            componentsData.mailSubsScreen.message.use = true;
            componentsData.mailSubsScreen.message.text = slashController(res.mail_subscription_form.message);
            componentsData.mailSubsScreen.message.textColor = ARGBtoRGBA(ext.mail_subscription_form.text_color);
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
        // Mail Form Required
        componentsData.mailSubsScreen.button.text = slashController(res.mail_subscription_form.button_label);
        componentsData.mailSubsScreen.button.textColor = ARGBtoRGBA(ext.mail_subscription_form.button_text_color);
        componentsData.mailSubsScreen.button.buttonColor = ARGBtoRGBA(ext.mail_subscription_form.button_color);
        componentsData.mailSubsScreen.button.fontSize = fontSizeCalculator(ext.mail_subscription_form.button_text_size) + 'px';
        componentsData.mailSubsScreen.emailInput.placeHolder = res.mail_subscription_form.placeholder;
        componentsData.mailSubsScreen.alerts.check_consent_message = slashController(res.mail_subscription_form.check_consent_message);
        componentsData.mailSubsScreen.alerts.invalid_email_message = slashController(res.mail_subscription_form.invalid_email_message);
    }

    // Rules Screen Optionals
    if (res.gamification_rules && Object.keys(res.gamification_rules).length > 0) {
        activePageData.rulesScreen = true;

        componentsData.rulesScreen.bgImage = res.gamification_rules.background_image
        componentsData.rulesScreen.button.text = slashController(res.gamification_rules.button_label)
        componentsData.rulesScreen.button.textColor = ARGBtoRGBA(ext.gamification_rules.button_text_color);
        componentsData.rulesScreen.button.buttonColor = ARGBtoRGBA(ext.gamification_rules.button_color);
        componentsData.rulesScreen.button.fontSize = fontSizeCalculator(ext.gamification_rules.button_text_size) + 'px';
    }


    // Game Screen
    componentsData.gameScreen.scoreboard.background = ARGBtoRGBA(ext.game_elements.scoreboard_background_color);
    componentsData.gameScreen.scoreboard.type = ext.game_elements.scoreboard_shape;
    componentsData.gameScreen.scoreboard.position = ext.game_elements.scoreboard_pageposition;
    if (componentsData.gameScreen.scoreboard.type == "") componentsData.gameScreen.scoreboard.type = "roundedcorners"


    // Finish Screen
    if (res.game_result_elements.title) {
        componentsData.finishScreen.title.use = true
        componentsData.finishScreen.title.text = slashController(res.game_result_elements.title)
        componentsData.finishScreen.title.fontSize = fontSizeCalculator(ext.game_result_elements.title_text_size) + 'px'
        componentsData.finishScreen.title.textColor = ARGBtoRGBA(ext.game_result_elements.title_text_color)

    }
    if (res.game_result_elements.message) {
        componentsData.finishScreen.message.use = true
        componentsData.finishScreen.message.text = slashController(res.game_result_elements.message)
        componentsData.finishScreen.message.fontSize = fontSizeCalculator(ext.game_result_elements.text_size) + 'px'
        componentsData.finishScreen.message.textColor = ARGBtoRGBA(ext.game_result_elements.text_color)
    }

    componentsData.finishScreen.button.text = slashController(res.copybutton_label);
    componentsData.finishScreen.button.textColor = ARGBtoRGBA(ext.copybutton_text_color);
    componentsData.finishScreen.button.fontSize = fontSizeCalculator(ext.copybutton_text_size) + 'px';
    componentsData.finishScreen.button.buttonColor = ARGBtoRGBA(ext.copybutton_color);
    componentsData.finishScreen.button.androidLink = res.android_lnk;

    componentsData.finishScreen.lose.buttonLabel = slashController(res.game_result_elements.lose_button_label);
    componentsData.finishScreen.lose.loseAndroidLink = res.game_result_elements.lose_android_lnk;
    componentsData.finishScreen.lose.src = res.game_result_elements.lose_image;
    componentsData.finishScreen.lose.loseButtonTextColor = ARGBtoRGBA(ext.game_result_elements.losebutton_text_color);
    componentsData.finishScreen.lose.loseButtonColor = ARGBtoRGBA(ext.game_result_elements.losebutton_color);
    componentsData.finishScreen.lose.loseButtonTextSize = fontSizeCalculator(ext.game_result_elements.losebutton_text_size) + 'px';



    componentsData.finishScreen.couponCode.background = ARGBtoRGBA(ext.promocode_background_color);
    componentsData.finishScreen.couponCode.textColor = ARGBtoRGBA(ext.promocode_text_color);
    componentsData.finishScreen.couponCode.fontSize = fontSizeCalculator(ext.game_result_elements.text_size) + 'px';



}

function iOSConfigRegulator(responseConfig) {
    console.log(responseConfig);

    const res = responseConfig;

    const row = res.gameElements.playgroundRowcount;
    const col = res.gameElements.playgroundColumncount;

    maxPairCalculator(row, col);
    cardSlotAdjuster(row, col, res.gameElements.cardImages); // burayı androiddeki gibi yap
    promoCodeCalculator(res.promoCodes);

    // General data
    gameSettings.duration = res.gameElements.durationOfGame;
    generalData.sound = res.gameElements.soundUrl;
    generalData.bgColor = ARGBtoRGBA(res.background_color);
    generalData.bgImg = res.backgroundImage;
    generalData.closeButtonColor = ARGBtoRGBA(res.close_button_color);
    generalData.fontName = res.font_family;
    cardSettings.backfaceImg = res.gameElementsExtended.backofcardsImage;
    cardSettings.backfaceColor = res.gameElementsExtended.backofcardsColor;

    if (res.font_family == 'custom' && utils.getMobileOperatingSystem() == 'iOS') {
        generalData.fontName = res.custom_font_family_ios;
        generalData.fontFiles = responseConfig.fontFiles;
        console.log('font files ', generalData.fontFiles);
        addFonts();
    }

    // // Mail Form Optionals
    if (res.mailSubscription) {
        activePageData.mailSubsScreen = true;

        if (res.mailSubscriptionForm.title) {
            componentsData.mailSubsScreen.title.use = true;
            componentsData.mailSubsScreen.title.text = slashController(res.mailSubscriptionForm.title);
            componentsData.mailSubsScreen.title.textColor = ARGBtoRGBA(res.mailExtendedProps.titleTextColor);
            componentsData.mailSubsScreen.title.fontSize = fontSizeCalculator(res.mailExtendedProps.titleTextSize) + 'px';
        }

        if (res.mailSubscriptionForm.message) {
            componentsData.mailSubsScreen.message.use = true;
            componentsData.mailSubsScreen.message.text = slashController(res.mailSubscriptionForm.message);
            componentsData.mailSubsScreen.message.textColor = ARGBtoRGBA(res.mailExtendedProps.textColor);
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

        // // Mail Form Required
        componentsData.mailSubsScreen.button.text = slashController(res.mailSubscriptionForm.buttonTitle);
        componentsData.mailSubsScreen.button.textColor = ARGBtoRGBA(res.mailExtendedProps.buttonTextColor);
        componentsData.mailSubsScreen.button.buttonColor = ARGBtoRGBA(res.mailExtendedProps.buttonColor);
        componentsData.mailSubsScreen.button.fontSize = fontSizeCalculator(res.mailExtendedProps.buttonTextSize) + 'px';
        componentsData.mailSubsScreen.emailInput.placeHolder = res.mailSubscriptionForm.placeholder;
        componentsData.mailSubsScreen.alerts.check_consent_message = slashController(res.mailSubscriptionForm.checkConsentMessage);
        componentsData.mailSubsScreen.alerts.invalid_email_message = slashController(res.mailSubscriptionForm.invalidEmailMessage);
    }

    // // Rules Screen Optionals
    if (res.gamificationRules && Object.keys(res.gamification_rules).length > 0) {
        activePageData.rulesScreen = true;

        componentsData.rulesScreen.bgImage = res.gamificationRules.backgroundImage
        componentsData.rulesScreen.button.text = slashController(res.gamificationRules.buttonLabel)
        componentsData.rulesScreen.button.textColor = ARGBtoRGBA(res.gamificationRulesExtended.buttonTextColor);
        componentsData.rulesScreen.button.buttonColor = ARGBtoRGBA(res.gamificationRulesExtended.buttonColor);
        componentsData.rulesScreen.button.fontSize = fontSizeCalculator(res.gamificationRulesExtended.buttonTextSize) + 'px';
    }

    // Game Screen
    componentsData.gameScreen.scoreboard.background = ARGBtoRGBA(res.gameElementsExtended.scoreboardBackgroundColor);
    componentsData.gameScreen.scoreboard.type = res.gameElementsExtended.scoreboardShape;
    componentsData.gameScreen.scoreboard.position = res.gameElementsExtended.scoreboardPageposition;
    if (componentsData.gameScreen.scoreboard.type == "") componentsData.gameScreen.scoreboard.type = "roundedcorners"

    // Finish Screen
    if (res.gameResultElements.title) {
        componentsData.finishScreen.title.use = true
        componentsData.finishScreen.title.text = slashController(res.gameResultElements.title)
        componentsData.finishScreen.title.fontSize = fontSizeCalculator(res.gameResultElementsExtended.titleTextSize) + 'px'
        componentsData.finishScreen.title.textColor = ARGBtoRGBA(res.gameResultElementsExtended.titleTextColor)
    }
    if (res.gameResultElements.message) {
        componentsData.finishScreen.message.use = true
        componentsData.finishScreen.message.text = slashController(res.gameResultElements.message)
        componentsData.finishScreen.message.fontSize = fontSizeCalculator(res.gameResultElementsExtended.textSize) + 'px'
        componentsData.finishScreen.message.textColor = ARGBtoRGBA(res.gameResultElementsExtended.textColor)
    }

    componentsData.finishScreen.button.text = slashController(res.copybutton_label);
    componentsData.finishScreen.button.textColor = ARGBtoRGBA(res.copybutton_text_color);
    componentsData.finishScreen.button.fontSize = fontSizeCalculator(res.copybutton_text_size) + 'px';
    componentsData.finishScreen.button.buttonColor = ARGBtoRGBA(res.copybutton_color);
    componentsData.finishScreen.button.iOSLink = res.ios_lnk;

    componentsData.finishScreen.lose.buttonLabel = slashController(res.gameResultElements.loseButtonLabel);
    componentsData.finishScreen.lose.loseIOSLink = res.gameResultElements.loseIosLnk;
    componentsData.finishScreen.lose.src = res.gameResultElements.loseImage;
    componentsData.finishScreen.lose.loseButtonTextColor = ARGBtoRGBA(res.gameResultElementsExtended.losebuttonTextColor);
    componentsData.finishScreen.lose.loseButtonColor = ARGBtoRGBA(res.gameResultElementsExtended.losebuttonColor);
    componentsData.finishScreen.lose.loseButtonTextSize = fontSizeCalculator(res.gameResultElementsExtended.losebuttonTextSize) + 'px';

    componentsData.finishScreen.couponCode.background = ARGBtoRGBA(res.promocode_background_color);
    componentsData.finishScreen.couponCode.textColor = ARGBtoRGBA(res.promocode_text_color);
    componentsData.finishScreen.couponCode.fontSize = fontSizeCalculator(res.gameResultElementsExtended.textSize) + 'px';
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
        <a style='font-size:"+ componentsData.mailSubsScreen.emailPermission.fontSize + ";text-decoration: underline;color: black;text-align:left; font-family:" + generalData.fontName + "'\
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
            "<a style='font-size:" + componentsData.mailSubsScreen.emailPermission.fontSize + ";text-decoration: underline;color: black;text-align:left; font-family:" + generalData.fontName + "'\
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
        alert.style.fontSize = "14px";
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
    if (componentsData.gameScreen.scoreboard.position == 'topRight') {
        closeButton.style.left = "0px";
        closeButton.style.right = "auto";
    }

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
        rulesScreen.appendChild(rulesScreenBluredBG)
    }


    var submit = document.createElement("div");
    submit.id = componentsData.rulesScreen.button.id;
    submit.style.backgroundColor = componentsData.rulesScreen.button.buttonColor;
    submit.style.color = componentsData.rulesScreen.button.textColor;
    submit.style.padding = "15px 30px";
    submit.style.fontSize = componentsData.rulesScreen.button.fontSize;
    submit.style.borderRadius = generalData.borderRadius;
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

    var gameArea = document.createElement("DIV");
    gameArea.id = componentsData.gameScreen.gameArea;
    gameArea.style.width = "100%";
    gameArea.style.height = gameSettings.gameAreaHeight;
    gameArea.style.position = "absolute";
    gameArea.style.transform = "translate(-50%, -50%)";
    gameArea.style.top = "50%";
    gameArea.style.left = "50%";
    gameArea.style.textAlign = "center";
    // gameArea.style.backgroundColor = generalData.bgColor;

    gameScreen.appendChild(gameArea);

    MAIN_COMPONENT.appendChild(gameScreen);
    console.log("oyun başladı");

    setTimeout(() => {
        startGame();
        utils.sendReport();
    }, 1);
}

function createCard(data, i) {
    if (!data || !data.name) return

    let id = data.name + '-' + i, turn = false;
    var card = document.createElement('div');
    card.id = id;
    card.style.width = cardSettings.cardWidth + 'px';
    card.style.height = cardSettings.cardHeight + 'px';
    card.style.margin = cardSettings.cardMargin + 'px';
    card.style.borderRadius = '10px';
    card.style.zIndex = '99';
    card.style.overflow = 'hidden';
    card.style.display = 'inline-block';

    let front = document.createElement('div');
    front.style.width = card.style.width;
    front.style.height = card.style.height;
    front.style.overflow = 'hidden';
    front.style.backgroundRepeat = 'no-repeat';
    front.style.backgroundSize = 'cover';
    front.style.backgroundImage = "url('" + data.imgUrl + "')";
    front.style.transformStyle = 'preserve-3d';
    front.style.transition = 'all ' + cardSettings.duration + 'ms cubic-bezier(1, 0.99, 0, -0.02) 0s';
    front.style.position = 'absolute';
    front.style.backfaceVisibility = 'hidden';
    front.style.borderRadius = '10px';
    front.style.transform = 'rotateY(180deg)';


    let back = document.createElement('div');
    back.style.width = card.style.width;
    back.style.height = card.style.height;
    back.style.backgroundRepeat = 'no-repeat';
    back.style.backgroundSize = 'cover';
    back.style.backgroundImage = "url('" + cardSettings.backfaceImg + "')";
    back.style.backgroundColor = cardSettings.backfaceColor;
    back.style.transformStyle = 'preserve-3d';
    back.style.transition = 'all ' + cardSettings.duration + 'ms cubic-bezier(1, 0.99, 0, -0.02) 0s';
    back.style.position = 'absolute';
    back.style.backfaceVisibility = 'hidden';
    back.style.borderRadius = '10px';


    if (data.empty) {
        front.style.backgroundImage = "url('" + cardSettings.emptyFrontImg + "')";
        front.style.backgroundColor = cardSettings.emptyFrontColor;
        back.style.backgroundImage = "url('" + cardSettings.emptyBackfaceImg + "')";
        back.style.backgroundColor = cardSettings.emptyBackfaceColor;
    }

    card.appendChild(front);
    card.appendChild(back);

    card.addEventListener("click", () => {
        if (CLICKABLE) {
            open(card.id, data)
        }
    })

    return card;
}

function cardSlotAdjuster(row, column, images, blankCard) {
    let result = [];
    // console.log("rowColTotalCountWithMod(row , column)",rowColTotalCountWithMod(row , column));
    images.forEach((url, i) => {
        if (result.length < rowColTotalCountWithMod(row, column)) {
            const cardData = { name: 'card' + (i + 1), imgUrl: url }
            result.push(cardData)
            result.push(cardData)
        }
    });

    // 3. parametre responseConfigden çekilecek 
    if (emptyCardDataAddControl(row, column, blankCard)) {
        result.push({ empty: true, name: 'EMPTY_CARD', imgUrl: blankCard })
    }

    result.sort(() => (Math.random() > .5) ? 1 : -1);


    result = slotCreator(row, column, result)
    console.log(result);

    componentsData.gameScreen.cards = result;
    utils.cardSizeCalculate();
}

function emptyCardDataAddControl(row, column, emptyCardActive) {
    if (!row || !column) return false

    let result = false;
    if (emptyCardActive) {
        result = (row * column) % 2 > 0
    }

    return result
}

function rowColTotalCountWithMod(row, column) {
    if (!row || !column) return 0

    const x = row * column
    const result = x % 2

    return result > 0 ? x - 1 : x
}

function slotCreator(r, c, imgs) {
    let arr = []
    for (let i = 0; i < r; i++) {
        arr.push([])
        for (let j = 0; j < c; j++) {
            arr[i].push(imgs[0])
            imgs.shift();
        }
    }
    return arr
}

function cardSizeCalculator(col) {
    let cardWidth = (window.innerWidth / col) - (cardSettings.cardMargin * 2);
    let cardHeight = cardWidth * 2

    cardSettings.cardWidth = cardWidth
    cardSettings.cardHeight = cardHeight
}

function removeEventListener(id) {
    let card = document.querySelector('#' + id)
    card.outerHTML = card.outerHTML
}

function pairController(id, data) {
    if (LAST_CLICKED_CARD_ID == id) {
        return
    }
    LAST_CLICKED_CARD_ID = id;
    pair.push({
        "name": data.name,
        "id": id
    })
    PAIR_COUNTER++;
    if (PAIR_COUNTER == 2) {
        console.log("pair", pair);
        if (pair[0].name == pair[1].name) {
            setTimeout(() => {
                PAIRS.push(pair);
                removeEventListener(pair[0].id);
                removeEventListener(pair[1].id);
                gameSettings.matchedIconEnable && addPairIcon(pair[0].id)
                gameSettings.matchedIconEnable && addPairIcon(pair[1].id)
                resetPairCheckParams();
                console.log("PPPPPAAAIR", PAIRS);
                PAIR_COUNT++;
                finishChecker();
            }, cardSettings.duration);
        }
        else {
            setTimeout(() => {
                close(pair[0].id);
                close(pair[1].id);
                resetPairCheckParams();
            }, cardSettings.duration);
        }
    }
}

function resetPairCheckParams() {
    LAST_CLICKED_CARD_ID = null;
    PAIR_COUNTER = 0;
    pair = [];
}

function clickableController() {
    CLICKABLE = false
    setTimeout(() => {
        CLICKABLE = true
    }, CLICKABLE_DURATION);
}

function addPairIcon(id) {
    var icon = document.createElement("div");
    icon.innerText = "MATCHED " + SCORE;
    icon.style.backgroundColor = 'green';
    icon.style.position = 'relative';
    icon.style.color = '#fff';
    icon.style.borderTopLeftRadius = '10px';
    icon.style.borderTopRightRadius = '10px';
    document.querySelector("#" + id).appendChild(icon)
}

function open(cardId, data) {
    // console.log("CLICKED " + cardId + ":", data);
    let card = document.querySelectorAll('#' + cardId + '>div');
    let front = card[0];
    let back = card[1];
    front.style.transform = "rotate3d(0, 1, 0, 0deg)"
    back.style.transform = "rotate3d(0, 1, 0, 180deg)"
    clickableController();
    pairController(cardId, data)
}

function close(cardId) {
    let card = document.querySelectorAll('#' + cardId + '>div');
    let front = card[0];
    let back = card[1];
    front.style.transform = "rotate3d(0, 1, 0, 180deg)"
    back.style.transform = "rotate3d(0, 1, 0, 0deg)"
}

function updateScore(score) {
    document.querySelector('#' + componentsData.gameScreen.scoreboard.score.id).innerHTML = score + ' SANİYE';

}

function startGame() {
    componentsData.gameScreen.cards.forEach((row, i) => {
        row.forEach((card, j) => {
            if (document.querySelector('#' + componentsData.gameScreen.id) && document.querySelector('#' + componentsData.gameScreen.gameArea)) {
                let tmpCard = createCard(card, i + '-' + j)
                tmpCard && document.querySelector('#' + componentsData.gameScreen.gameArea).appendChild(tmpCard);
            }
        });
    });
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
    dashboard.style.fontSize = "24px";
    dashboard.style.transition = "1s all";
    dashboard = scoreboardPositionChanger(dashboard, componentsData.gameScreen.scoreboard.position);
    componentsData.gameScreen.scoreboard.type !== 'square' && (dashboard.style.borderRadius = componentsData.gameScreen.scoreboard.type == 'circle' ? '50%' : '15px')

    var _duration = document.createElement("div");
    _duration.innerHTML = gameSettings.duration;
    _duration.id = componentsData.gameScreen.scoreboard.countDown.id;
    _duration.style.fontWeight = "bold";
    _duration.style.marginBottom = gameSettings.gameScreenSecondScoreEnable ? "10px" : "0px";
    _duration.style.transition = "1s all";
    _duration.style.width = "150px";
    _duration.style.maxWidth = "150px";
    _duration.style.color = componentsData.gameScreen.scoreboard.fontColor;
    _duration.style.fontFamily = generalData.fontName;
    _duration.style.fontSize = componentsData.gameScreen.scoreboard.fontSize;
    dashboard.appendChild(_duration);

    if (gameSettings.gameScreenSecondScoreEnable) {
        var _score = document.createElement("DIV");
        _score.id = componentsData.gameScreen.scoreboard.score.id;
        _score.innerHTML = SCORE + ' SANİYE';
        _score.style.transition = "1s all";
        _score.style.color = componentsData.gameScreen.scoreboard.fontColor;
        _score.style.fontFamily = generalData.fontName;
        _score.style.fontSize = componentsData.gameScreen.scoreboard.fontSize;

        dashboard.appendChild(_score);
    }
    document.querySelector('#' + componentsData.gameScreen.id).appendChild(dashboard);
    TIME_INTERVAL = utils.startCountDown(document.querySelector('#' + _duration.id), gameSettings.duration);
}

function scoreboardPositionChanger(el, value) {
    switch (value) {
        case 'topLeft':
            el.style.top = '0';
            el.style.left = '0';
            break;
        case 'topRight':
            el.style.top = '0';
            el.style.right = '0';
            break;
        case 'bottomLeft':
            el.style.bottom = '0';
            el.style.left = '0';
            break;
        case 'bottomRight':
            el.style.bottom = '0';
            el.style.right = '0';
            break;
        default:
            el.style.top = '0';
            el.style.left = '0';
            break;
    }
    return el
}

function createFinishScreen(lose) {
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

    if (!lose) {
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
        _score.innerHTML = SCORE + ' SANİYEDE ÇÖZDÜNÜZ';
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

        _score.addEventListener('click', function () {
            utils.copyToClipboard(lose);
            utils.pauseSound();
        });
    }
    else {
        var img = document.createElement("img");
        img.id = componentsData.finishScreen.lose.id;
        img.src = componentsData.finishScreen.lose.src
        img.style.display = "inline-block";
        img.style.margin = "15px 0";
        img.style.width = 'inherit';
        container.appendChild(img);
    }

    var copyButton = document.createElement("div");
    copyButton.id = componentsData.finishScreen.button.id;
    copyButton.style.backgroundColor = lose ? componentsData.finishScreen.lose.loseButtonColor : componentsData.finishScreen.button.buttonColor;
    copyButton.style.color = lose ? componentsData.finishScreen.lose.loseButtonTextColor : componentsData.finishScreen.button.textColor;
    copyButton.style.padding = "15px 30px";
    copyButton.style.fontSize = lose ? componentsData.finishScreen.lose.loseButtonTextSize : componentsData.finishScreen.button.fontSize;
    copyButton.style.borderRadius = generalData.borderRadius;
    copyButton.style.position = "absolute";
    copyButton.style.bottom = "70px";
    copyButton.style.left = "50%";
    copyButton.style.transform = "translate(-50%, 0%) translate3d(0,0,3px)";
    copyButton.style.width = "fit-content";
    copyButton.style.margin = "10px auto";
    copyButton.style.cursor = "pointer";
    copyButton.style.zIndex = "3";
    copyButton.style.fontWeight = "bolder";
    copyButton.style.fontFamily = generalData.fontName;
    copyButton.innerText = lose ? componentsData.finishScreen.lose.buttonLabel : componentsData.finishScreen.button.text;

    container.appendChild(copyButton);

    if (!lose) {utils.saveCodeGotten()}

    copyButton.addEventListener('click', function () {
        utils.copyToClipboard(lose);
        utils.pauseSound();
    });


    copyButton.addEventListener("click", function () {
        if (utils.getMobileOperatingSystem() == 'iOS' && componentsData.finishScreen.button.iOSLink) {
            if (lose) {
                redirection(componentsData.finishScreen.lose.loseIOSLink)
            }
            else {
                redirection(componentsData.finishScreen.button.iOSLink)
            }
        }
    });

    finishScreen.appendChild(container);
    MAIN_COMPONENT.appendChild(finishScreen);
}

function finish(lose) {
    clearInterval(TIME_INTERVAL.timer)
    createFinishScreen(lose);
    document.querySelector('#' + componentsData.gameScreen.id).remove();
}

function maxPairCalculator(row, col) {
    MAX_PAIR_COUNT = Math.floor((row * col) / 2)
    console.log("MAX_PAIR_COUNT", MAX_PAIR_COUNT);
}

function finishChecker() {
    // console.log(PAIR_COUNT,'----',MAX_PAIR_COUNT);
    if (PAIR_COUNT >= MAX_PAIR_COUNT) {
        finish(false);
    }
}

function loseCheck() {
    if (PAIR_COUNT < MAX_PAIR_COUNT) {
        finish(true);
    }
}

function promoCodeCalculator(data) {
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

function scoreCalculator() {
    let durationEl = document.querySelector('#' + componentsData.gameScreen.scoreboard.countDown.id)
    console.log();
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

function ARGBtoRGBA(argb) {
    if (!argb || argb.length < 8) return argb

    if (argb.substr(0, 1) !== '#') return argb.replace(/(..)(......)/, '$2$1')

    return argb.replace(/#(..)(......)/, '#$2$1')
}

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

function slashController(text) {
    if (!text) return text

    let pos = text.indexOf('\\n');
    while (pos > -1) {
        text = text.replace("\\n", "\n");
        pos = text.indexOf('\\n');
    }
    return text
}

function redirection(url) {
    location.href = url
}

function getAndroidLink(lose) {
    if (lose) {
        if (componentsData.finishScreen.lose.loseAndroidLink) {
            return componentsData.finishScreen.lose.loseAndroidLink
        }
        else {
            return ""
        }
    } else {
        if (componentsData.finishScreen.button.androidLink) {
            return componentsData.finishScreen.button.androidLink
        } else {
            return ""
        }
    }
}

let utils = {
    randNum: (min, max) => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    cardSizeCalculate: () => {
        const rowItemCount = componentsData.gameScreen.cards[0].length;
        const colmItemCount = componentsData.gameScreen.cards.length;
        const w = (WIDTH - (rowItemCount * (cardSettings.cardMargin * 2))) / rowItemCount;
        const h = (gameSettings.gameAreaHeight - (colmItemCount * (cardSettings.cardMargin * 2))) / colmItemCount;

        cardSettings.cardWidth = w;
        cardSettings.cardHeight = h;
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
            SCORE = Math.round(that.usedTime / 100)
            // console.log("score",SCORE);
            if (tt <= 0) {
                that.elem.innerHTML = '00:00.00';
                clearInterval(that.timer);
                loseCheck();
            } else {
                var mi = Math.floor(tt / (60 * 100));
                var ss = Math.floor((tt - mi * 60 * 100) / 100);
                var ms = tt - Math.floor(tt / 100) * 100;

                that.elem.innerHTML = that.fillZero(mi) + ":" + that.fillZero(ss) + "." + that.fillZero(ms.toFixed(0));
                gameSettings.gameScreenSecondScoreEnable && updateScore(SCORE)
            }
        };


        if (!that.timer) {
            that.timer = setInterval(that.count, 10);
        }

        that.fillZero = function (num) {
            return num < 10 ? '0' + num : num;
        };

        return that;
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
    copyToClipboard: (lose) => {
        console.log("NATIVE COPYCLIPBORD");
        if (window.Android) {
            Android.copyToClipboard(lose ? "" : couponCodes[SCORE], getAndroidLink(lose))
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

/**
 * Start configs
 */
function config() {
    document.body.setAttribute('style', '-webkit-user-select:none');
    CLICKABLE_DURATION = cardSettings.duration
    pageChecker();
    createCloseButton();
}