// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>
function init() {
    var i = 0;
    var content_items = ["Hello Display the matched.", "Display the matched elements with a sliding motion."];
    var data_one_card = {};

    // Обработка одной карты
    (function(content_items) {
        var seed = 0;
        var UP_TUNER = '.one-card-container > div.tuner-base.tuner-base-up';
        var items = ['word', 'content', 'translate'];
        var TOTAL_CARDS = items.length;
        var part_name = ".pack-card-container > div.active-card";
        $(UP_TUNER).bind(
            { click:
                (function(seed) {
                    return function() {
                        // Slot
                        var ptr = 0;
                        var tmp = $(this).parent().find(part_name).each(function () {
                            // Порадок важен
                            ++ptr; $(this).css("z-index", (seed-ptr+TOTAL_CARDS)%TOTAL_CARDS);});
                        seed = (seed+1)%TOTAL_CARDS;
                    }
                })(seed)});
        // Заполняем контент
        // Удаляем старый если есть

        // Добавляем новый
        var count_content_items = content_items.length;
        var full_path_to_content_card = part_name+'.content';
        for (var i= 0; i < count_content_items; ++i) {
            // Текст нужно обернуть получше
            var tmp = $("<div/>").addClass("pack-card-container").appendTo(full_path_to_content_card);
            tmp = $("<div/>").addClass("content-stack").appendTo(tmp);
            $("<span/>").addClass("text-contents").appendTo(tmp).append(content_items[i])
         }

        if (count_content_items > 1) {
            var seed = 0;
            // Тюнер вверх
            $("<div/>").addClass("tuner-base tuner-base-up-inner").appendTo(full_path_to_content_card).bind(
                { click: (function(seed) { return function() {
                    // Slot
                    var ptr = 0;
                    $(this).parent().find('.pack-card-container').each(function () {
                        // Порадок важен
                        ++ptr; $(this).css("z-index", (seed-ptr+count_content_items)%count_content_items);});
                    seed = (seed+1)%count_content_items;
                }})(seed)});

            // Тюнер вниз
            $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(full_path_to_content_card);
        }

    })(content_items);  // Вызов как бы конструктора.
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}