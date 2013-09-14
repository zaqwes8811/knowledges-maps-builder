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

  // Если запись одна, то нужно закрыть тюнеры!

  $(obj).find(CONSTANTS.UP_TUNER_SEL)
    .click(function() {
       seedState = (seedState+1)%countRecords;
       tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {
         $(this).find('.circle-inner').css('background-color', '#330099');
      },
      function() {
         $(this).find('.circle-inner').css('background-color', '#333399');
      });

  $(obj).find(CONSTANTS.DOWN_TUNER_SEL)
    .click(function() {
      seedState = (seedState-1+countRecords)%countRecords;
      tick(seedState, CONSTANTS.SUB_CARDS_SEL, countRecords, this);})
    .hover(
      function() {$(this).find('.circle-inner').css('background-color', '#330099');},
      function() {$(this).find('.circle-inner').css('background-color', '#333399');});

  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  var subCardsPkg = $('>'+CONSTANTS.PACK_CARDS_SEL, obj);

  // TODO(zaqwes): Не утекают ли обработчики тюнеров?
  // http://stackoverflow.com/questions/11726864/jquery-empty-click-and-memory-management
  subCardsPkg.empty();


  // Только этот словарь знаяет, какой ключ соответсвует карте
  var cardsHandles = {};

  // Q: Не нравится клонирование, но как обойти массив в обратном порядке не изменяя его?
  // Создаем leafs - контейнеры для хранения конечных данных
  var items = dataOneCard[0][0];  // Нужна строгая сортировка!
  $.each(items, function (key, value) {
    cardsHandles[value] = $("<div/>").addClass('leaf');
    $(cardsHandles[value]).css("z-index", key);
    $(cardsHandles[value]).appendTo(subCardsPkg);
  });

  // Заполняем
  $.each(items, function(key_local, value) {
    var handler = cardsHandles[value];
    var content = dataOneCard[1][key_local];
    fillNoWordCard(content, handler);
  });

  // Доболнения
};

// Так должен заполнятся контент и перевод - само слово должно быть
//   с панелью управления.
function fillNoWordCard(content, handler) {
  var components = [];
  var countItems = content.length;
  var contentReversed = (goog.array.clone(content)).reverse();
  var  wrapper = $("<div/>").addClass('slice');
  for (var i = 0; i < countItems; ++i) {
    // Текст нужно обернуть получше
    $("<span/>").addClass("text-contents").append(contentReversed[i]).appendTo(
      $("<div/>").addClass("slice-inner").appendTo(wrapper));
  }
  components.push(wrapper);

  // Добавляем тюнеры только если более одного элемента.
  if (countItems > 1) {
    // Общая переменная
    var seed = 0;
    
    // Тюнер вверх
    var main_tuner_up = $("<div/>").addClass("tuner-base slice-tuner-up")
      .click(function() {
        seed = (seed+1)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-up').css('background-color', '#330099');},
        function() {$(this).find('.triangle-up').css('background-color', '#333399');})
        
    var tuner_arroy_up = $("<div/>").addClass("tuner-base slice-tuner-up inner-triangle triangle-up");
    $(tuner_arroy_up).appendTo(main_tuner_up);

    // Тюнер вниз
    var main_tuner_down = $("<div/>").addClass("tuner-base slice-tuner-down")
      .click(function() {
        seed = (seed-1+countItems)%countItems;
        tick(seed, CONSTANTS.INNER_CARDS_CONTAINER, countItems, this);})
      .hover(
        function() {$(this).find('.triangle-down').css('background-color', '#330099');},
        function() {$(this).find('.triangle-down').css('background-color', '#333399');})
      
    var tuner_arroy_down = $("<div/>").addClass("tuner-base slice-tuner-down inner-triangle triangle-down");
    $(tuner_arroy_down).appendTo(main_tuner_down);
    
    // Form object graph.
    components.push(main_tuner_up);
    components.push(main_tuner_down);
  }
  
  for (i = 0; i < components.length; ++i)
    $(components[i]).appendTo(handler);
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
