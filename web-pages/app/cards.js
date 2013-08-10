// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>

// TODO(zaqwes): Как скрыть пространство имен.

// /app/cards.js

goog.provide('cards');  // Export?

goog.require('goog.array');

var AConstants = (function () {
  var items = ['word', 'content', 'translate'];
  return {
    ITEMS : items,   // Какие есть подкарты
    CONTENT : items[1],
    TRANSLATE : items[2],
    WORD : items[0],
    TOTAL_FRONT_CARDS : items.length,

    // Selectors
    UP_TUNER_SEL : 'div.tuner-base.leafs-tuner-up',
    DOWN_TUNER_SEL : 'div.tuner-base.leafs-tuner-down',
    SUB_CARDS_SEL : ".leafs-container > div.leaf",
    PACK_CARDS_SEL : '.leafs-container',
    CARD_CONTAINER_SEL : '.card-container',
    INNER_CARDS_CONTAINER: '.leafs-container-inner'
  }
})();

function tick(seed, sel, count, obj) {
      $(obj).parent().find(sel).each(function (key, value) {
                  var newPos = (seed+key)%count;
                  $(this).css("z-index", newPos);
                });
       //obj = null;
    }

function getFakeResponse() {
    // Данные которые будут приходить с веб-сервера
    var response = []
    var CONTENT = AConstants.CONTENT;
    var TRANSLATE = AConstants.TRANSLATE;
    response.push({
       "content" : ["Hello Display the matched. Display the matched elements with a sliding motion.", "Display the matched elements with a sliding motion."],
       "translate" : ["Перевод1", "Перевод2"],
       'word': ['matched']
    });

    response.push({
       'content' : ["Hello"],
       'translate' : ["Перевод", "Переводasdf"],
       'word': ['hello']
    });

    response.push({
           'content' : ["Hello", "Hello"],
           'translate' : ["Перевод", "Переводasdf"],
           'word': ['hello']
        });

    response.push({
               'content' : ["Hello", "Hello"],
               'translate' : ["Перевод", "Переводasdf"],
               'word': ['hello']
            });

    return response;
}
//var Cards = (function)
function init() {
    pureInit();
    processResponse(getFakeResponse());
}

// Подключает тюнеры?
function pureInit() {
  $(AConstants.CARD_CONTAINER_SEL).each(function(key, value) {
     // TODO(zaqwes): У каждого тюнера есть состояние. Хорошо бы его инкапсулировать.
     // Подключаем тюнер-вверх для подкарт - нужно вынести отсюда. Это чистая инициализация.
     // TODO(zaqwes): Тюнеры конфликтуют и глючат
     // TODO(zaqwes): Может тюрены ограничить, чтобы не сбивать с толку при замыкании
     // TODO(zaqwes: TOTH: А вообще, понятно ли какая подкарты перед нами?
     // TODO(zaqwes): DANGER: Быстро утекает память! Или медленно идет сборка мусора?
     // TODO(zaqwes): использовать find() наверное не очень хорошо.
     var seedState = 0;
     $(this).find(AConstants.UP_TUNER_SEL)
       .click(function() {
           var COUNT = AConstants.TOTAL_FRONT_CARDS;
           seedState = (seedState+1)%COUNT;
           tick(seedState, AConstants.SUB_CARDS_SEL, COUNT, this);});
     $(this).find(AConstants.DOWN_TUNER_SEL)
       .click(function() {
           var COUNT = AConstants.TOTAL_FRONT_CARDS;
           seedState = (seedState-1+COUNT)%COUNT;
           tick(seedState, AConstants.SUB_CARDS_SEL, COUNT, this);});
  });
}

function processResponse(response) {
    // TODO(zaqwes): TOTH: Что будет менятся от запроса к запросу?
    // Обрабатываем все доступные карты
    $(AConstants.CARD_CONTAINER_SEL).each(function(key, value) {
        // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
        var subCardsPkg = $(this).find(AConstants.PACK_CARDS_SEL);
        subCardsPkg.empty();  // TODO(zaqwes): Не утекают ли обработчики тюнеров?
        // http://stackoverflow.com/questions/11726864/jquery-empty-click-and-memory-management
        var cardsHandles = {};

        $.each((goog.array.clone(AConstants.ITEMS)).reverse(), function (key, value) {
          cardsHandles[value] = $("<div/>").addClass('leaf').appendTo(subCardsPkg);
          $(cardsHandles[value]).css("z-index", key);
        });

        // Добавляем контент
        var tmp = [AConstants.CONTENT, AConstants.TRANSLATE, AConstants.WORD];
        $.each(tmp, function(key_local, value) {
            var handler = cardsHandles[value];
            var content = response[key][value];
            fillNoWordCard(content, handler);
        });

        $.each(function (key, value) { value = null; });
    });  // .each()
}

function fillNoWordCard(content, handler) {
    var countItems = content.length;

    // TODO(zaqwes): Low perfomance! Можно в цикле развернуть.
    var contentReversed = (goog.array.clone(content)).reverse();
    for (var i = 0; i < countItems; ++i) {
        // Текст нужно обернуть получше
        $("<span/>").addClass("text-contents").append(contentReversed[i]).appendTo(
         $("<div/>").addClass("slice").appendTo(handler));//);
         // .appendTo(
           //$("<div/>").addClass('leafs-container-inner')
    }

    // Добавляем тюнеры только если более одного элемента.
    // TODO(zaqwes): Сделать их по середине поля! И тонкими
    if (countItems > 1) {
        var seed = 0;
        $("<div/>").addClass("tuner-base slice-tuner-up")
            .click(function() {
                seed = (seed+1)%countItems;
                tick(seed, AConstants.INNER_CARDS_CONTAINER, countItems, this);})
            .appendTo(handler);
        $("<div/>").addClass("tuner-base slice-tuner-down")
            .click(function() {
                seed = (seed-1+countItems)%countItems;
                tick(seed, AConstants.INNER_CARDS_CONTAINER, countItems, this);})
            .appendTo(handler);
    }
    handler = null;  // TODO(zaqwes): TOTH: Нужно ль?
}


function getCardsContent() {
  $.ajax({
    type: 'GET',
    url: '/pkg'})
    .done(function(response) {

        processResponse($.parseJSON(response));

     })
    .fail(function() { alert("error"); })
}
