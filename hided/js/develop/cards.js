// Подключает карточки
function init() {

    // Создаем один элемент
    var i = 0;
    var bind_obj = { click:
        (function(x) {
            return function() { alert(i); }
        })(i)
    };
   $('.one-card-container > div.one-card-active-item').bind(bind_obj);


}

// Обновляет массив карт. Перезаписывает поля
function reload() {

}