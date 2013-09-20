// Подключает карточки
// TODO(zaqwes): Как скрыть пространство имен.

//goog.provide('voc_app.cards');  // Export?
//goog.require('goog.array');

var CONSTANTS = (function () {
  return {
  // Selectors
  SEL_LEFT_TUNER : 'div.card-tuner-base.leafs-tuner-up',
  SEL_RIGHT_TUNER : 'div.card-tuner-base.leafs-tuner-down',
  SUB_CARDS_SEL : ".leafs-container > div.leaf",
  SEL_LEAFS_DECK : '.leafs-container',
  SEL_CARD_CONTAINER : '.card-container',
  INNER_CARDS_CONTAINER: '.layer-inner'
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
  $(CONSTANTS.SEL_CARD_CONTAINER).each(function(key, value) {
    var content = this;
    // Получить новое слово
    var here = function (response) {
      processOneCard(content, response);
    };
    
    // Создаем поле для счетчика активных карточек
    var tunerRight = $(content).find(CONSTANTS.SEL_RIGHT_TUNER);
    var totalItems = $("<div/>").addClass("leafs-total-view");
    var text = $("<span/>").addClass("text-contents").append("");
    $(text).appendTo(totalItems);
    $(totalItems).appendTo(tunerRight);

    // Делаем инициирующий запрос - Шлется только один
    //getOneCardContent(here);
  });
}

function processOneCard(obj, dataOneCard) {
  var seedState = 0;
  var countRecords = dataOneCard[0][0].length;

  // Если запись одна, то нужно закрыть тюнеры!

  $(obj).find(CONSTANTS.SEL_LEFT_TUNER)
    .click(function() {
       seedState = (seedState+1)%countRecords;
       tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {$(this).find('.circle-inner').css(foneName, '#330099');},
      function() {$(this).find('.circle-inner').css(foneName, '#333399');});

  // Пока должно быть ясно, но вообще это не производительно.
  var tunerRight = $(obj).find(CONSTANTS.SEL_RIGHT_TUNER);
  $(tunerRight)
    .click(function() {
      seedState = (seedState-1+countRecords)%countRecords;
      tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {$(this).find('.circle-inner').css(foneName, '#330099');},
      function() {$(this).find('.circle-inner').css(foneName, '#333399');});
      
  // Если не созданы, то нужно удалить
  // Добавляем число записей
  $(obj).find("div.leafs-total-view").find("span.text-contents").text(countRecords.toString());

  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  var leafsDeck = $('>'+CONSTANTS.SEL_LEAFS_DECK, obj);

  // Сбрасываем
  leafsDeck.empty();

  // Только этот словарь знаяет, какой ключ соответсвует карте
  var names = dataOneCard[0][0];  // Нужна строгая сортировка!
  var leafsHandlers = createLeafs(names);

  // Заполняем
  $.each(names, function(key_local, value) {
    $(leafsHandlers[value]).appendTo(leafsDeck);
  });
  
  $.each(names, function(key_local, value) {
    var handler = leafsHandlers[value];
    var content = dataOneCard[1][key_local];
    var fillMap = getFillMap();
    var components = fillMap[value](content);
    for (i = 0; i < components.length; ++i)
      $(components[i]).appendTo(handler);
  });
};

function createLeafs(names) {
  var cardsHandles = {};

  // Создаем leafs - контейнеры для хранения конечных данных
  $.each(names, function (key, value) {
    cardsHandles[value] = $("<div/>").addClass('leaf').css("z-index", key);
  });
  return cardsHandles;
}


// Листы должны быть по возможности независимы, т.к. нужно будет добалять операции
function getFillMap() {
  return {
    "content": createAnyCard, 
    "translate": createAnyCard, 
    "word": createWordLeaf, 
  };
}

function createWordLeaf(content) {
  // Набор компонентов, которые подключаются к одному узлу.
  var components = [];
  
  // Текстовое заполнение
  components.push(createTextDeck(content));
  
  // Тюнеров пока нет
  /*var triangle = $("<div/>").addClass("card-triangle-inner-right updaters-triangle");
  var updaters = $("<div/>").addClass("updaters-tuner updaters");
  $(triangle).appendTo(updaters);*/
  
  var triangle = $("<div/>").addClass("card-triangle-inner-up");
  var circleInner = $("<div/>").addClass("circle-inner");
  var circleParent = $("<div/>").addClass("circle-parent updaters-circle-parent");
  var updaters = $("<div/>").addClass("updaters-tuner updaters");
  $(triangle).appendTo(circleInner);
  $(circleInner).appendTo(circleParent);
  $(circleParent).appendTo(updaters);
  components.push(updaters);
  
  // Оставить только кнопку обновления
  var content = $(CONSTANTS.SEL_CARD_CONTAINER);
  // Получить новое слово
  var here = function (response) {
    processOneCard(content, response);
  };

  // Делаем инициирующий запрос - Шлется только один
  $(updaters)
    .click(function () {
      getOneCardContent(here);});

  return components;
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
    var main_tuner_up = $("<div/>").addClass("card-tuner-base layer-tuner-up")
      .click(function() {
        seed = (seed+1)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-up').css(foneName, '#330099');},
        function() {$(this).find('.triangle-up').css(foneName, '#333399');})
        
    var tuner_arrow_up = $("<div/>").addClass("card-tuner-base layer-tuner-up inner-triangle triangle-up");
    $(tuner_arrow_up).appendTo(main_tuner_up);

    // Тюнер вниз
    var main_tuner_down = $("<div/>").addClass("card-tuner-base layer-tuner-down")
      .click(function() {
        seed = (seed-1+countItems)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-down').css(foneName, '#330099');},
        function() {$(this).find('.triangle-down').css(foneName, '#333399');})
      
    var tuner_arrow_down = $("<div/>").addClass("card-tuner-base layer-tuner-down inner-triangle triangle-down");
    $(tuner_arrow_down).appendTo(main_tuner_down);
    
    // Form object graph.
    components.push(main_tuner_up);
    components.push(main_tuner_down);
  }
  return components;  
}

function createTextDeck(content) {
  var countItems = content.length;
  var  wrapper = $("<div/>").addClass('layer');
  for (var i = countItems-1; i >= 0; --i) {
    //TODO(zaqwes): Текст нужно обернуть получше
    var textContainer = $("<div/>").addClass("layer-inner");
    var text = $("<span/>").addClass("text-contents").append(content[i]);
    $(text).appendTo(textContainer);
    $(textContainer).appendTo(wrapper)
  }
  return wrapper;
}


function getOneCardContent(callBackFun) {
  var response = [];
  var names = ["content", "translate", "word"];
  response[0] = [names];
  var data = [["Hello man", "Hello!"], ["перевод", "еще один"], ["hello"]];
  response[1] = data;
  callBackFun(response)
        
/*
  var urlAjax = '/pkg';
  $.ajax({
    type: 'GET',
    url: urlAjax,
    data: { noCache: (new Date().getTime()) + Math.random() }})
      .done(function(response) {
        var response = $.parseJSON(response);
        callBackFun(response);})
      .fail(function(data) { 
        alert("error"); })*/
}


