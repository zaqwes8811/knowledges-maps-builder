// Подключает карточки
// <span class="text-contents">Display the matched elements with a sliding motion.</span>
function init() {
    var i = 0;
    data_one_card = {};
    (function() {
    var content_items = ["Display the matched elements with a sliding motion.", "Display the matched elements with a sliding motion."];
        var seed = 0;
        var UP_TUNER = '.one-card-container > div.tuner-base.tuner-base-up';
        var items = ['word', 'content', 'translate'];
        var TOTAL_CARDS = items.length;
        var part_name = ".pack-card-container > div.active-card.active-card-";
        $(UP_TUNER).bind(
            { click:
                (function(seed) {
                    return function() {
                        // Slot
                        var container = $(this).parent();
                        for (var i = 0; i < TOTAL_CARDS; ++i)
                            $(container).find(part_name+items[i]).css("z-index", (seed+i)%TOTAL_CARDS);
                        seed = (seed+1)%TOTAL_CARDS;
                    }
                })(seed)});
        // Заполняем контент
        // Удаляем старый если есть

        // Добавляем новый
        //$("<div/>").addClass("content-stack").appendTo(part_name+'content').css('background-color', 'green');
        //$("<div/>").addClass("content-stack").appendTo(part_name+'content').css('background-color', 'red');
        var count_content_items = content_items.length;
        if (count_content_items > 1) {
            var seed = 0;
            $("<div/>").addClass("tuner-base tuner-base-up-inner").appendTo(part_name+'content').bind(
                { click: (function() { return function() {
                    alert('Hello');

                }})()});
            $("<div/>").addClass("tuner-base tuner-base-down-inner").appendTo(part_name+'content');
            // Коннектим обработчики
        }

    })();  // Вызов как бы конструктора.
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}