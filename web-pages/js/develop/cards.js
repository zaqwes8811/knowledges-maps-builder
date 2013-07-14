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

                        for (var i = 0; i < TOTAL_CARDS; ++i)
                            $(this).parent().find(part_name+items[i]).css("z-index", (seed+i)%TOTAL_CARDS);
                        seed = (seed+1)%TOTAL_CARDS;
                    }
                })(seed)});
        // Заполняем контент
        // Удаляем старый если есть

        // Добавляем новый
        //$("<div/>").addClass("content-stack").appendTo(part_name+'content').css('background-color', 'green');
        //$("<div/>").addClass("content-stack").appendTo(part_name+'content').css('background-color', 'red');
        $("<div/>").addClass("tuner-base tuner-base-up").appendTo(part_name+'content');
        $("<div/>").addClass("tuner-base tuner-base-down").appendTo(part_name+'content');

    })();  // Вызов как бы конструктора.
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}