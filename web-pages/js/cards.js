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
  LAYERS_CONTAINER: '.layer-inner'
  }
})();
var foneName = 'background-color';

function tick(seed, sel, count, obj) {
  $(obj).parent().find(sel).each(function (key, value) {
    $(this).css("z-index", (seed+key)%count);
  });
}

function init() {
  pureInit();
}

var cardsContainer = []; // все равно хранить где-то нужно.

function pureInit() {
  $(CONSTANTS.SEL_CARD_CONTAINER).each(function(key, value) {
    var oneCard = new OneCard(this);
    cardsContainer.push(oneCard);
    oneCard.exchange();
  });
}

// Prototype
function OneCard (context) {
  this.context_ = context;
}
 
OneCard.prototype.exchange = function () {
  var response = [];
  var names = ["content", "translate", "word"];
  response[0] = [names];
  var data = [["Hello man", "Hello!"], ["перевод", "еще один"], ["hello", "tryam"]];
  response[1] = data;
  this.processResponse(response);
        
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

// Листы должны быть по возможности независимы, т.к. нужно будет добалять операции
OneCard.prototype.getFillMap_ = function () {
  return {
    "content": createAnyCard, 
    "translate": createAnyCard, 
    "word": createAnyCard,//createWordLeaf, 
  };
}

OneCard.prototype.processResponse = function (response) {
  var countLeafs = response[0][0].length;
  var names = response[0][0];  // Важна строгая сортировка!
  /*var seedState = 0;
  

  // Если запись одна, то нужно закрыть тюнеры!
  $(this.context_).find(CONSTANTS.SEL_LEFT_TUNER)
    .click(function() {
       seedState = (seedState+1)%countLeafs;
       tick(seedState, CONSTANTS.SUB_CARDS_SEL, countLeafs, this);})
    .hover(
      function() {$(this).find('.circle-child').css(foneName, '#330099');},
      function() {$(this).find('.circle-child').css(foneName, '#333399');});

  // Пока должно быть ясно, но вообще это не производительно.
  var tunerRight = $(this.context_).find(CONSTANTS.SEL_RIGHT_TUNER);
  $(tunerRight)
    .click(function() {
      seedState = (seedState-1+countLeafs)%countLeafs;
      tick(seedState, CONSTANTS.SUB_CARDS_SEL, countLeafs, this);})
    .hover(
      function() {$(this).find('.circle-child').css(foneName, '#330099');},
      function() {$(this).find('.circle-child').css(foneName, '#333399');});
  */
    
  // Если не созданы, то нужно удалить
  // Добавляем число записей
  $(this.context_)
    .find("div.leafs-total-view")
    .find("span.text-contents")
    .text(countLeafs.toString());
  
  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  var leafsDeck = $('>'+CONSTANTS.SEL_LEAFS_DECK, this.context_);

  // Сбрасываем
  leafsDeck.empty();
  
  // Только этот словарь знаяет, какой ключ соответсвует карте
  var leafsHandlers = viewCreateLeafs(names);
  
  // Заполняем
  $.each(names, function(key_local, value) {
    $(leafsHandlers[value]).appendTo(leafsDeck);
  });
  
  var here = this;
  
  $.each(names, function(key_local, value) {
    var handler = leafsHandlers[value];
    var content = response[1][key_local];
    var fillMap = here.getFillMap_();
    var components = fillMap[value](content);
    for (i = 0; i < components.length; ++i)
      $(components[i]).appendTo(handler);
  });
};

function viewCreateLeafs(names) {
  var cardsHandles = {};

  // Создаем leafs - контейнеры для хранения конечных данных
  $.each(names, function (key, value) {
    cardsHandles[value] = $("<div/>").addClass('leaf').css("z-index", key);});
  return cardsHandles;
}

function createWordLeaf(content) {
  // Набор компонентов, которые подключаются к одному узлу.
  var components = [];
  
  // Текстовое заполнение
  components.push(createTextDeck(content));
  
  // Тюнеров пока нет
  /*var triangle = $("<div/>").addClass("triangle-inner-right card-updaters-triangle");
  var card-updaters = $("<div/>").addClass("card-updaters-tuner card-updaters");
  $(triangle).appendTo(card-updaters);*/
  
  /*
  var triangle = $("<div/>").addClass("triangle-inner-up");
  var circleInner = $("<div/>").addClass("circle-child");
  var circleParent = $("<div/>").addClass("circle-parent card-updaters-circle-parent");
  var card-updaters = $("<div/>").addClass("card-updaters-tuner card-updaters");
  $(triangle).appendTo(circleInner);
  $(circleInner).appendTo(circleParent);
  $(circleParent).appendTo(card-updaters);
  components.push(card-updaters);*/
  
  // Оставить только кнопку обновления
  var content = $(CONSTANTS.SEL_CARD_CONTAINER);
  // Получить новое слово
  var here = function (response) {
    processOneCard(content, response);
  };

  // Делаем инициирующий запрос - Шлется только один
  $('card-updaters')
    .click(function () {
      exchange(here);});

  return components;
}

function Tuner() {
  this.seed = 0;
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
    // Тюнер вверх
    var mainTunerUp = $("<div/>").addClass("layer-tuner-base layer-tuner-up");
    var tunerArrowUp = $("<div/>").addClass("small-triangle-base small-triangle-up "
        +"layer-triangle-position-base layer-triangle-position-up");
    $(tunerArrowUp).appendTo(mainTunerUp);

    // Тюнер вниз
    var mainTunerDown = $("<div/>").addClass("layer-tuner-base layer-tuner-down");
    var tunerArrowDown = $("<div/>").addClass("small-triangle-base small-triangle-down "
        +"layer-triangle-position-base layer-triangle-position-down");
    $(tunerArrowDown).appendTo(mainTunerDown);
    
    // Подключаем действия
    var seed = 0;
    var clickUp = function() {
        seed = (seed+1)%countItems;
        tick(seed, CONSTANTS.LAYERS_CONTAINER, countItems, mainTunerUp);}
    
    
    var clickDown = function() {
        seed = (seed-1+countItems)%countItems;
        tick(seed, CONSTANTS.LAYERS_CONTAINER, countItems, mainTunerDown);}
        
    // Само подключение
    $(mainTunerUp).click(clickUp);
    $(mainTunerDown).click(clickDown);

    // Form object graph.
    components.push(mainTunerUp);
    components.push(mainTunerDown);
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





