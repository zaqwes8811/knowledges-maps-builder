// Подключает карточки
function init() {
    var i = 0;
    data_one_card = {};
    (function() {
        var seed = 0;
        var UP_TUNER = '.one-card-container > div.tuner-base.tuner-base-up';
        $(UP_TUNER).bind(
            { click:
                (function(seed) {
                    return function() {
                        // Slot
                        var items = ['word', 'content', 'translate'];
                        var TOTAL_CARDS = items.length;
                        var part_name = ".pack-card-container > div.active-card.active-card-";
                        for (var i = 0; i < TOTAL_CARDS; ++i)
                            $(this).parent().find(part_name+items[i]).css("z-index", (seed+i)%TOTAL_CARDS);
                        seed = (seed+1)%TOTAL_CARDS;
                    }
                })(seed)});
    })();  // Вызов как бы конструктора.
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}