// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>
function init() {
  var i = 0;
  var contentItemsOne = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
  var contentItemsTwo = ["Hello", "Display."];

  var ITEMS = ['word', 'content', 'translate'];  // Какие есть подкарты
  var CONTENT = 1;
  var TOTAL_FRONT_CARDS = ITEMS.length;

  var pkg = []
  var dataOneCard = {};
  dataOneCard[ITEMS[CONTENT]] = contentItemsOne;
  pkg.push(dataOneCard);
  var dataTwoCard = {};
  dataTwoCard[ITEMS[CONTENT]] = contentItemsTwo;
  pkg.push(dataTwoCard);

  // Selectors
  var UP_TUNER_SEL = 'div.tuner-base.tuner-base-up';
  var SUB_CARDS_SEL = ".pack-card-container > div.active-card";
  var PACK_CARDS_SEL = '.pack-card-container';

  // Обрабатываем все доступные карты
  $('.one-card-container').each(function(key, value) {
    // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
    var subCardsPkg = $(this).find(PACK_CARDS_SEL);
    subCardsPkg.empty();
    var cardsHandles = {};
    
    $.each(ITEMS, function (key, value) {
      cardsHandles[value] = $("<div/>").addClass('active-card').appendTo(subCardsPkg);
    });
    
    // Добавляем контент
    var content = pkg[key][ITEMS[CONTENT]];
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
    }

    // Подключаем тюнер-вверх для подкарт
    var seedState = 0;
    $(this).find(UP_TUNER_SEL)
      .click((function(x) {
        return function() {
          // По подкартам, но по клику. Вызывается ли при загрузке?
          $(this).parent().find(SUB_CARDS_SEL).each(function (key, value) {
            $(this).css("z-index", (x-key+TOTAL_FRONT_CARDS)%TOTAL_FRONT_CARDS);
          });
          x = (x+1)%TOTAL_FRONT_CARDS;
        }
      })(seedState));
    });

    // TODO(zaqwes): Подключаем тюнер-вниз. Кстати, а не будут ли они друг другу мешать?

/*
    (function() {
    // Заполняем контент
    // Удаляем старый если есть

    // Добавляем новый
    // TODO(zaqwes): Кажется нужна таки обертка для отделения от тюнеров
    var countItems = contentItems.length;
    var full_path_to_content_card = part_name+'.content';
    for (var i= 0; i < countItems; ++i) {
      // Текст нужно обернуть получше
      $("<span/>").addClass("text-contents").append(contentItems[i]).appendTo(
        $("<div/>").addClass("content-stack").appendTo(
          $("<div/>").addClass('pack-card-container-inner').appendTo(full_path_to_content_card)));
     }


    // Только если более одного элемента.
    if (countItems > 1) {
      var seed = 0;
      // Тюнер вверх
      $("<div/>").addClass("tuner-base tuner-base-up-inner")
          .click((function(seed, items) {
            return (function() {
                // Slot
                var ptr = 0;
                $(this).parent().find('.pack-card-container-inner').each(
                function() { ++ptr; $(this).css("z-index", (seed-ptr+items)%items); });
                seed = (seed+1)%items;
            })
            })(seed, countItems))
           .appendTo(full_path_to_content_card);

      // Тюнер вниз
      $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(full_path_to_content_card);
    }

    })();


  })(contentItems);  // Вызов как бы конструктора.
  */
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}

/*
d=document.createElement('div');
$(d).addClass(classname)
    .html(text)
    .appendTo($("#myDiv")) //main div
    .click(function(){
        $(this).remove();
    })
    .hide()
    .slideToggle(300)
    .delay(2500)
    .slideToggle(300)
    .queue(function() {
        $(this).remove();
    });
*/