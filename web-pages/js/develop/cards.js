// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>
function init() {
  var i = 0;
  var content_items = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
  //var content_items = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
  var data_one_card = {};
  var items = ['word', 'content', 'translate'];
  var total_front_card = items.length;

  // Selectors
  var up_tuner_sel = 'div.tuner-base.tuner-base-up';
  var sub_cards_sel = ".pack-card-container > div.active-card";
  var pack_cards_sel = '.pack-card-container';

  // Обрабатываем все доступные карты
  $('.one-card-container').each(function(key, value) {
    // Создаем подкарты. Так проще будет их подключить, т.к. будут дескрипторы.
    //(function() {  // Похоже влияет на this - не дает вызвать
        // Удаляем старый если есть
        var sub_cards_pkg = $(this).find(pack_cards_sel);
        sub_cards_pkg.empty();

        $.each(items, function (key, value_in) {
          $("<div/>").addClass('active-card').appendTo(sub_cards_pkg).text(value_in);
        });

        // Добавляем новый
        // TODO(zaqwes): Кажется нужна таки обертка для отделения от тюнеров
        //var count_content_items = content_items.length;
        //var full_path_to_content_card = part_name+'.content';
        //for (var i= 0; i < count_content_items; ++i) {
          // Текст нужно обернуть получше
        //  $("<span/>").addClass("text-contents").append(content_items[i]).appendTo(
        //    $("<div/>").addClass("content-stack").appendTo(
        //      $("<div/>").addClass('pack-card-container-inner').appendTo(full_path_to_content_card)));
    //})();

    // Подключаем тюнер-вверх для подкарт
    var seed_state = 0;
    $(this).find(up_tuner_sel)
      .click((function(x) {
        return function() {
          // По подкартам, но по клику. Вызывается ли при загрузке?
          $(this).parent().find(sub_cards_sel).each(function (key, value) {
            $(this).css("z-index", (x-key+total_front_card)%total_front_card);
          });
          x = (x+1)%total_front_card;
        }
      })(seed_state));
    });

    // TODO(zaqwes): Подключаем тюнер-вниз. Кстати, а не будут ли они друг другу мешать?

/*
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
          $("<div/>").addClass('pack-card-container-inner').appendTo(full_path_to_content_card)));
     }


    // Только если более одного элемента.
    if (count_content_items > 1) {
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
            })(seed, count_content_items))
           .appendTo(full_path_to_content_card);

      // Тюнер вниз
      $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(full_path_to_content_card);
    }

    })();


  })(content_items);  // Вызов как бы конструктора.
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