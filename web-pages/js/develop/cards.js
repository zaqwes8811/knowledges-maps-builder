// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>
function init() {
  var i = 0;
  var content_items = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
  var data_one_card = {};

  // Обработка одной карты
  (function(content_items) {
    var part_name = ".pack-card-container > div.active-card";

    (function () {
    var seed = 0;
    var items = ['word', 'content', 'translate'];
    var TOTAL_CARDS = items.length;

    var binder = { click:
     (function(seed) {
       return function() {
         // Slot
         var ptr = 0;
         var tmp = $(this).parent().find(part_name).each(function () {
           // Порадок важен
           ++ptr; $(this).css("z-index", (seed-ptr+TOTAL_CARDS)%TOTAL_CARDS);});
         seed = (seed+1)%TOTAL_CARDS;
       }
     })(seed)};

    // Когда создается элемент можно сделать удобно бинд через .on().
    $('.one-card-container > div.tuner-base.tuner-base-up').bind(binder);
    }) ();

    (function() {
    // Заполняем контент
    // Удаляем старый если есть

    // Добавляем новый
    // TODO(zaqwes): Кажется нужна таки обертка для отделения от тюнеров
    var count_content_items = content_items.length;
    var full_path_to_content_card = part_name+'.content';
    for (var i= 0; i < count_content_items; ++i) {
      // Текст нужно обернуть получше
      $("<span/>").addClass("text-contents").append(content_items[i]).appendTo(
      $("<div/>").addClass("content-stack").appendTo(
      $("<div/>", {
          class: 'pack-card-container-inner'
      }).appendTo(full_path_to_content_card)));
     }


    // Только если более одного элемента.
    if (count_content_items > 1) {
      var seed = 0;
      // Тюнер вверх
      var needed_class = '.pack-card-container-inner';
      var bind_obj = { click:
        (function(seed, items) {
         return (function() {
           // Slot
           var ptr = 0;
           $(this).parent().find(needed_class).each(
           function() { ++ptr; $(this).css("z-index", (seed-ptr+items)%items); });
           seed = (seed+1)%items;
         })
        })(seed, count_content_items)};
      $("<div/>").addClass("tuner-base tuner-base-up-inner").appendTo(full_path_to_content_card).bind(bind_obj);

      // Тюнер вниз
      $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(full_path_to_content_card);
    }

    })();

  })(content_items);  // Вызов как бы конструктора.
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