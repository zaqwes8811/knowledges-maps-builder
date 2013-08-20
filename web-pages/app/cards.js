// Подключает карточки
// TODO(zaqwes): Как скрыть пространство имен.

goog.provide('voc_app.cards');  // Export?
goog.require('goog.array');

voc_app.cards.Note = function(obj) {
  this.hello = 'hello';

};

var CONSTANTS = (function () {
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
  //processResponse(voc_app.fakes.getFakeResponse());
}

// Подключает тюнеры?
// TODO(zaqwes): У каждого тюнера есть состояние. Хорошо бы его инкапсулировать.
// Подключаем тюнер-вверх для подкарт - нужно вынести отсюда. Это чистая инициализация.
// TODO(zaqwes): Может тюрены ограничить, чтобы не сбивать с толку при замыкании
// TODO(zaqwes: TOTH: А вообще, понятно ли какая подкарты перед нами?
// TODO(zaqwes): DANGER: Быстро утекает память! Или медленно идет сборка мусора?
// TODO(zaqwes): использовать find() наверное не очень хорошо.
function pureInit() {
  $(CONSTANTS.CARD_CONTAINER_SEL).each(function(key, value) {
    var seedState = 0;
    $(this).find(CONSTANTS.UP_TUNER_SEL)
      .click(function() {
         var COUNT = CONSTANTS.TOTAL_FRONT_CARDS;
         seedState = (seedState+1)%COUNT;
         tick(seedState, CONSTANTS.SUB_CARDS_SEL, COUNT, this);})
      .hover(
        function() {$(this).find('.circle-inner').css('background-color', '#330099');},
        function() {$(this).find('.circle-inner').css('background-color', '#333399');});
    
    $(this).find(CONSTANTS.DOWN_TUNER_SEL)
      .click(function() {
        var COUNT = CONSTANTS.TOTAL_FRONT_CARDS;
        seedState = (seedState-1+COUNT)%COUNT;
        tick(seedState, CONSTANTS.SUB_CARDS_SEL, COUNT, this);})
      .hover(
        function() {$(this).find('.circle-inner').css('background-color', '#330099');},
        function() {$(this).find('.circle-inner').css('background-color', '#333399');});

    // Получить новое слово
    var content = this;
    var here = function (response) {
      processOneCard(content, response);
    };
    
    $(this).find('div.tuner-base.updater')
      .click(function () {
        getOneCardContent(here);});
        
    // Делаем инициирующий запрос - Шлется только один
    getOneCardContent(here);
  });
}


function processOneCard(obj, dataOneCard) {
  // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
  //var subCardsPkg = $(obj).find(CONSTANTS.PACK_CARDS_SEL);
  var subCardsPkg = $('>'+CONSTANTS.PACK_CARDS_SEL, obj);
  
  // TODO(zaqwes): Не утекают ли обработчики тюнеров?
  // http://stackoverflow.com/questions/11726864/jquery-empty-click-and-memory-management
  subCardsPkg.empty();  

  var cardsHandles = {};	

  // Q: Не нравится клонирование, но как обойти массив в обратном порядке не изменяя его?
  $.each((goog.array.clone(CONSTANTS.ITEMS)).reverse(), function (key, value) {
      cardsHandles[value] = $("<div/>").addClass('leaf').appendTo(subCardsPkg);
      $(cardsHandles[value]).css("z-index", key);
      });

  // Добавляем контент
  var leafs_names = [CONSTANTS.CONTENT, CONSTANTS.TRANSLATE, CONSTANTS.WORD];
  $.each(leafs_names, function(key_local, value) {
    var handler = cardsHandles[value];
    var content = dataOneCard[value];
    fillNoWordCard(content, handler);
  });
};


// TODO(zaqwes): TOTH: Что будет менятся от запроса к запросу?
// Обрабатываем все доступные карты
//
// Args:
//   response - [{}, {}]
function processResponse(response) {
  // Для каждой карточки...
  $(CONSTANTS.CARD_CONTAINER_SEL).each(function(key, value) {
    processOneCard(this, response[key]); }); 
}

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

function getCardsContent() {
  $.ajax({
    type: 'GET',
    url: '/pkg'})
    .done(function(response) {
      //try {
      var response = $.parseJSON(response);
      processResponse(response);
      //catch () {}
    })
    .fail(function() { alert("error"); })
}
