// Подключает карточки
// TODO(zaqwes): Как скрыть пространство имен.

goog.provide('voc_app.cards');  // Export?
goog.require('goog.array');

var CONSTANTS = (function () {
  return {
  // Selectors
  UP_TUNER_SEL : 'div.tuner-base.leafs-tuner-up',
  DOWN_TUNER_SEL : 'div.tuner-base.leafs-tuner-down',
  SUB_CARDS_SEL : ".leafs-container > div.leaf",
  SEL_LEAFS_DECK : '.leafs-container',
  CARD_CONTAINER_SEL : '.card-container',
  INNER_CARDS_CONTAINER: '.slice-inner'
  }
})();
var foneName = 'ba ckground-color';

function tick(seed, sel, count, obj) {
  $(obj).parent().find(sel).each(function (key, value) {
    $(this).css("z-index", (seed+key)%count);
  });
}

function init() {
  pureInit();
}

function pureInit() {
  $(CONSTANTS.CARD_CONTAINER_SEL).each(function(key, value) {
    var content = this;
    // Получить новое слово
    var here = function (response) {
      processOneCard(content, response);
    };

    // Оставить только кнопку обновления
    $(this).find('div.tuner-base.updater')
      .click(function () {
        getOneCardContent(here);});

    // Делаем инициирующий запрос - Шлется только один
    getOneCardContent(here);
  });
}

function processOneCard(obj, dataOneCard) {
  var seedState = 0;
  var countRecords = dataOneCard[0][0].length;

  // Если запись одна, то нужно закрыть тюнеры!

  $(obj).find(CONSTANTS.UP_TUNER_SEL)
    .click(function() {
       seedState = (seedState+1)%countRecords;
       tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {$(this).find('.circle-inner').css(foneName, '#330099');},
      function() {$(this).find('.circle-inner').css(foneName, '#333399');});

  $(obj).find(CONSTANTS.DOWN_TUNER_SEL)
    .click(function() {
      seedState = (seedState-1+countRecords)%countRecords;
      tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {$(this).find('.circle-inner').css(foneName, '#330099');},
      function() {$(this).find('.circle-inner').css(foneName, '#333399');});

  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  var leafsDeck = $('>'+CONSTANTS.SEL_LEAFS_DECK, obj);

  // Сбрасываем
  leafsDeck.empty();

  // Только этот словарь знаяет, какой ключ соответсвует карте
  var createLeafs = function () {
    var cardsHandles = {};

    // Создаем leafs - контейнеры для хранения конечных данных
    var items = dataOneCard[0][0];  // Нужна строгая сортировка!
    $.each(items, function (key, value) {
      cardsHandles[value] = $("<div/>").addClass('leaf').css("z-index", key);
    });
    return cardsHandles;
  }
  
  var leafsHandlers = createLeafs();

  // Заполняем
  var names = dataOneCard[0][0];  // Нужна строгая сортировка!
  $.each(names, function(key_local, value) {
    $(leafsHandlers[value]).appendTo(leafsDeck);
  });
  
  $.each(names, function(key_local, value) {
    var handler = leafsHandlers[value];
    var content = dataOneCard[1][key_local];
    var components = createAnyCard(content);
    for (i = 0; i < components.length; ++i)
      $(components[i]).appendTo(handler);
  });
};

function createTextDeck(content) {
  var countItems = content.length;
  var contentReversed = (goog.array.clone(content)).reverse();
  var  wrapper = $("<div/>").addClass('slice');
  for (var i = 0; i < countItems; ++i) {
    // Текст нужно обернуть получше
    $("<span/>").addClass("text-contents").append(contentReversed[i]).appendTo(
      $("<div/>").addClass("slice-inner").appendTo(wrapper));
  }
  return wrapper;
}

// Так должен заполнятся контент и перевод - само слово должно быть
//   с панелью управления.
function createAnyCard(content) {
  // Набор компонентов, которые подключаются к одному узлу.
  var components = [];
  
  // Текстовое заполнение
  components.push(createTextDeck(content));

  // Добавляем тюнеры только если более одного элемента.
  var countItems = content.length;
  if (countItems > 1) {
    // Общая переменная
    var seed = 0;
    
    // Тюнер вверх
    var main_tuner_up = $("<div/>").addClass("tuner-base slice-tuner-up")
      .click(function() {
        seed = (seed+1)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-up').css(foneName, '#330099');},
        function() {$(this).find('.triangle-up').css(foneName, '#333399');})
        
    var tuner_arrow_up = $("<div/>").addClass("tuner-base slice-tuner-up inner-triangle triangle-up");
    $(tuner_arrow_up).appendTo(main_tuner_up);

    // Тюнер вниз
    var main_tuner_down = $("<div/>").addClass("tuner-base slice-tuner-down")
      .click(function() {
        seed = (seed-1+countItems)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-down').css(foneName, '#330099');},
        function() {$(this).find('.triangle-down').css(foneName, '#333399');})
      
    var tuner_arrow_down = $("<div/>").addClass("tuner-base slice-tuner-down inner-triangle triangle-down");
    $(tuner_arrow_down).appendTo(main_tuner_down);
    
    // Form object graph.
    components.push(main_tuner_up);
    components.push(main_tuner_down);
  }
  return components;  
}

function getOneCardContent(callBackFun) {
  var urlAjax = '/pkg';
  $.ajax({
    type: 'GET',
    url: urlAjax,
    data: { noCache: (new Date().getTime()) + Math.random() }
    })
      .done(function(response) {
        //try {
        var response = $.parseJSON(response);
        callBackFun(response);
        //catch () {}
        })
      .fail(function(data) { 
        alert("error"); 
      })
}

/*

        <!--div class="tuner-base updater">
            <div class="circle-parent">
                <div class="circle-inner">
                    <div class="triangle-inner-right"></div>
                </div>
            </div>
        </div-->
*/
