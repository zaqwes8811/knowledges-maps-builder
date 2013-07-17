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
    TOTAL_FRONT_CARDS : items.length,

    // Selectors
    UP_TUNER_SEL : 'div.tuner-base.tuner-base-up',
    DOWN_TUNER_SEL : 'div.tuner-base.tuner-base-down',
    SUB_CARDS_SEL : ".pack-card-container > div.active-card",
    PACK_CARDS_SEL : '.pack-card-container',
    CARD_CONTAINER_SEL : '.one-card-container'
  }
})();

//var Cards = (function)
function init() {
  // Данные которые будут приходить с веб-сервера
  var response = []
  var dataOneCard = {};
  dataOneCard[AConstants.CONTENT] = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
  response.push(dataOneCard);
  var dataTwoCard = {};
  dataTwoCard[AConstants.CONTENT] = ["Hello", "Display."];
  response.push(dataTwoCard);

  processResponse(response);
}


function processResponse(response) {
  // Обрабатываем все доступные карты
  $(AConstants.CARD_CONTAINER_SEL).each(function(key, value) {
    // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
    var subCardsPkg = $(this).find(AConstants.PACK_CARDS_SEL);
    subCardsPkg.empty();
    var cardsHandles = {};
    
    $.each((goog.array.clone(AConstants.ITEMS)).reverse(), function (key, value) {
      cardsHandles[value] = $("<div/>").addClass('active-card').appendTo(subCardsPkg).text(value);
      $(cardsHandles[value]).css("z-index", key)
    });
    
    // Добавляем контент
    /*var content = response[key][ITEMS[CONTENT]];
    var countItems = content.length;
    for (var i= 0; i < countItems; ++i) {
      // Текст нужно обернуть получше
      $("<span/>").addClass("text-contents").append(content[i]).appendTo(
        $("<div/>").addClass("content-stack").appendTo(
          $("<div/>").addClass('pack-card-container-inner').appendTo(cardsHandles[ITEMS[CONTENT]])));
     }

     // Добавляем тюнеры только если более одного элемента.
    if (countItems > 1) {
        // TODO(zaqwes): Тюнер вверх сбивается - один холостой ход.
        var seed = 0;
        // Тюнер вверх
        $("<div/>").addClass("tuner-base tuner-base-up-inner")
           .click((function(seed, items) {
             return (function() {
                 $(this).parent().find('.pack-card-container-inner').each(function(key, value) {
                    $(this).css("z-index", (seed-key+items)%items);
                 });
                 seed = (seed+1)%items;
             })
             })(seed, countItems))
            .appendTo(cardsHandles[ITEMS[CONTENT]]);

        // Тюнер вниз
        $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(cardsHandles[ITEMS[CONTENT]]);
    }  */

    // Подключаем тюнер-вверх для подкарт
    // TODO(zaqwes): Тюнеры конфликтуют и глючат
    // TODO(zaqwes): Может тюрены ограничить, чтобы не сбивать с толку при замыкании
    // TODO(zaqwes: TOTH: А вообще, понятно ли какая подкарты перед нами?
    //(function() {   // Похоже ломает this
    var seedState = 0;
    $(this).find(AConstants.UP_TUNER_SEL)
      .click((function() {
        return function() {
          var COUNT = AConstants.TOTAL_FRONT_CARDS;
          seedState = (seedState+1)%COUNT;
          $(this).parent().find(AConstants.SUB_CARDS_SEL).each(function (key, value) {
            var newPos = (seedState+key)%COUNT;
            $(this).css("z-index", newPos);});
        }
      })());

    // Тюнер вниз
    $(this).find(AConstants.DOWN_TUNER_SEL)
      .click((function() {
        return function() {
          var COUNT = AConstants.TOTAL_FRONT_CARDS;
          seedState = (seedState-1+COUNT)%COUNT;
          $(this).parent().find(AConstants.SUB_CARDS_SEL).each(function (key, value) {
            var newPos = (seedState+key)%COUNT;
            $(this).css("z-index", newPos);});
         }
      })());

      //})();  // Fail
    });  // .each()
}
