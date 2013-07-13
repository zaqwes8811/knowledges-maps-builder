// Подключает карточки
function init() {
    var i = 0;
    (function(x) {
        var UP_BUTTON = '.one-card-container > div.tuner-base.tuner-base-up';
        $(UP_BUTTON).bind(
            { click:
                (function(x) {
                    return function() {
                        // Slot
                        $(this).parent().find(".pack-card-container > div.active-card.active-card-content").css("background-color", 'gray');
                        $(this).parent().find(".pack-card-container > div.active-card.active-card-content").css("z-index", 1);
                    }
                })()
            });
    })(i);
}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}