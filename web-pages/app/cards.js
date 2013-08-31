// Подключает карточки
// TODO(zaqwes): Как скрыть пространство имен.

goog.provide('voc_app.cards');  // Export?
goog.require('goog.array');

voc_app.cards.Note = function(obj) {
  this.hello = 'hello';
};

var CONSTANTS = (function () {
  return {
  // Selectors
  UP_TUNER_SEL : 'div.tuner-base.leafs-tuner-up',
  DOWN_TUNER_SEL : 'div.tuner-base.leafs-tuner-down',
  SUB_CARDS_SEL : ".leafs-container > div.leaf",
  PACK_CARDS_SEL : '.leafs-container',
  CARD_CONTAINER_SEL : '.card-container',
  INNER_CARDS_CONTAINER: '.slice-inner'
  }
})();

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
  //TODO(zaqwes): Инкапсулировать в карточку
  var seedState = 0;
    var countRecords = dataOneCard[0][0].length;
    $(obj).find(CONSTANTS.UP_TUNER_SEL)
      .click(function() {
         var COUNT = countRecords;
         seedState = (seedState+1)%COUNT;
         tick(seedState, CONSTANTS.SUB_CARDS_SEL, COUNT, this);})
      .hover(
        function() {$(this).find('.circle-inner').css('background-color', '#330099');},
        function() {$(this).find('.circle-inner').css('background-color', '#333399');});

    $(obj).find(CONSTANTS.DOWN_TUNER_SEL)
      .click(function() {
        var COUNT = countRecords;
        seedState = (seedState-1+COUNT)%COUNT;
        tick(seedState, CONSTANTS.SUB_CARDS_SEL, COUNT, this);})
      .hover(
        function() {$(this).find('.circle-inner').css('background-color', '#330099');},
        function() {$(this).find('.circle-inner').css('background-color', '#333399');});

  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  var subCardsPkg = $('>'+CONSTANTS.PACK_CARDS_SEL, obj);
  
  // TODO(zaqwes): Не утекают ли обработчики тюнеров?
  // http://stackoverflow.com/questions/11726864/jquery-empty-click-and-memory-management
  subCardsPkg.empty();  

  var cardsHandles = {};	

  // Q: Не нравится клонирование, но как обойти массив в обратном порядке не изменяя его?
  var items = dataOneCard[0][0];  // Нужна строгая сортировка!
  $.each(items, function (key, value) {
          cardsHandles[value] = $("<div/>").addClass('leaf').appendTo(subCardsPkg);
          $(cardsHandles[value]).css("z-index", key);
      });

  // Добавляем контент
  $.each(items, function(key_local, value) {
    var handler = cardsHandles[value];
    var content = dataOneCard[1][key_local];
    fillNoWordCard(content, handler);
  });
};


function fillNoWordCard(content, handler) {
  var countItems = content.length;

  // TODO(zaqwes): Low perfomance! Можно в цикле развернуть.
  var contentReversed = (goog.array.clone(content)).reverse();
  var  wrapper = $("<div/>").addClass('slice').appendTo(handler);
  for (var i = 0; i < countItems; ++i) {
    // Текст нужно обернуть получше
    $("<span/>").addClass("text-contents").append(contentReversed[i]).appendTo(
      $("<div/>").addClass("slice-inner").appendTo(wrapper));
  }

  // Добавляем тюнеры только если более одного элемента.
  // TODO(zaqwes): Сделать их по середине поля! И тонкими
  if (countItems > 1) {
    var seed = 0;
    var main_tuner = $("<div/>").addClass("tuner-base slice-tuner-up")
      .click(function() {
        seed = (seed+1)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);
      })
      .hover(
        function() {$(this).find('.triangle-up').css('background-color', '#330099');},
        function() {$(this).find('.triangle-up').css('background-color', '#333399');})
      .appendTo(handler)
    $("<div/>").addClass("tuner-base slice-tuner-up inner-triangle triangle-up").appendTo(main_tuner);
    
    // Тюнер вниз
    main_tuner = $("<div/>").addClass("tuner-base slice-tuner-down")
      .click(function() {
        seed = (seed-1+countItems)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);
      })
      .hover(
        function() {$(this).find('.triangle-down').css('background-color', '#330099');},
        function() {$(this).find('.triangle-down').css('background-color', '#333399');})
      .appendTo(handler);
    $("<div/>").addClass("tuner-base slice-tuner-down inner-triangle triangle-down").appendTo(main_tuner);
  }
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
      .fail(function() { alert("error"); })
}
