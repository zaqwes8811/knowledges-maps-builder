// State
var g_map = {};
var g_need_set_known = false;
var g_current_word_data = {};
var gUserSummary = new UserSummary([]);
var gView = new View();

function UserSummary(listPagesSum) {
  //var self = this;  // помогает ли вообще - if prototype looks line no!
  this.raw = listPagesSum;
}

UserSummary.prototype.reset = function (listPagesSum) {
  this.raw = listPagesSum;
}

UserSummary.prototype.getGenNames = function (_pageName) {
  // ищем по списку
  var r = _.findWhere(this.raw, {pageName: _pageName});
  return r.genNames;
}

UserSummary.prototype.getPageNames = function () {
  return _.pluck(this.raw, 'pageName');
}

// View
// http://www.electrictoolbox.com/jquery-add-option-select-jquery/
// http://stackoverflow.com/questions/47824/how-do-you-remove-all-the-options-of-a-select-box-and-then-add-one-option-and-se
function View() { }

View.prototype.getCurrentPageName = function () {
  return $('#pages > option:selected').text();
}

View.prototype.getCurrentGenName = function() {
  return $('#pageGenerators > option:selected').text();
};

View.prototype.resetPagesOptions = function(newNames) {
  var pageSelect = $('#pages');
  var pageGens = $('#pageGenerators');
  pageGens.empty();
  
  pageSelect.empty();
  _.each(newNames, function(e) { pageSelect.append(new Option(e, e, true, true)); });  
  
  var currentPageName = this.getCurrentPageName();
  var genNames = gUserSummary.getGenNames(currentPageName);
  _.each(genNames, function(e) { pageGens.append(new Option(e, e, true, true)); }); 
}

// Actions
View.prototype.onCreate = function() {
  $('#know_it').change(function() {
    // this represents the checkbox that was checked
    // do something with it
    var $this = $(this);
    if ($this.is(':checked')) {
      g_need_set_known = true;
    }
  });
  
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/user_summary';
  var jqxhr = $.get(uri)
    .success(function(data) {
      gUserSummary.reset(JSON.parse(data));
      
      // заполняем чекбоксы
      gView.resetPagesOptions(gUserSummary.getPageNames());
    })
    .error(function(data) { 
       alert("error on get sum about user");
    });
}

// http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
function set_know_it() {
  alert("Know");
}

// Zoom:
//   - До определенного момента кружочки не должны выключаться. 
// Например при zoom = 1, когда показываются все элементы.
$(function() {
  function showTooltip(x, y, contents) {
    $("<div id='tooltip'>" + contents + "</div>").css({
      position: "absolute",
      display: "none",
      top: y + 5,
      left: x + 5,
      border: "1px solid #fdd",
      padding: "2px",
      "background-color": "#fee",
      opacity: 0.80
    }).appendTo("body").fadeIn(200);
  }

  var previousPoint = null;
  $("#placeholder").bind("plothover", function (event, pos, item) {
    // Вывод подсказки по элементу
    if (item) {
      if (previousPoint != item.dataIndex) {

        previousPoint = item.dataIndex;

        $("#tooltip").remove();
        var x = item.datapoint[0].toFixed(2),
        y = item.datapoint[1].toFixed(2);

        showTooltip(item.pageX, item.pageY, g_map[Math.floor(x)]);
      }
    } else {
      $("#tooltip").remove();
      previousPoint = null;            
    }
  });
});

function get_data() {
  var request_processor = '/research/get_distribution';
  var response_branch = {'name':'get_axis'};
  var jqxhr = $.get(request_processor, response_branch)
    .success(function(data) {
      process_response(data);
    })
    .error(function(data) { 

    });
}

function process_response(data) {
  var getted_axises = $.parseJSON(data);
  
  // Нужно усреднять данные на отрезках, а через zoom увеличивать.
  var save = 0;
  var zoomed_data = [];
  var catch_every = 1;
  for(var i = 0; i < getted_axises.length; ++i) {
    tmp = []
    for(var name in getted_axises[i]) {
      if (getted_axises[i].hasOwnProperty(name)) {
        tmp.push(i);
        tmp.push(getted_axises[i][name]);
      }
      g_map[tmp[0]] = 'Position : '+tmp[0]+'/'+name+'/'+tmp[1]
    }
    zoomed_data.push(tmp);
  }

  // Функция рисования
  var plot = $.plot("#placeholder", [{ data: zoomed_data, label: "distr(x)"}], {
    series: {
      lines: {show: true},
      points: {show: false}
    },
    grid: {
      hoverable: true,
      clickable: true
    }
  });
}

function get_word_pkg() {
  // маркеруем слово как известное - уже в базе данных
  if (g_current_word_data) {
    (function() {
      var uri = '/pkg';
      var point = 0;
      var args = {'page':'get_axis', 'gen':'', 'point': point};
      var _ = $.get(uri, args)
        .success(function(data) { })
        .error(function(data) { });
      // установлю сразу, но вообще запрос асинхронный, поэтому не очень
      g_need_set_known = false;  
    })();
  }
  
  // делаем запрос
  var uri = '/pkg';
  var args = {'name':'get_axis'};
  var _ = $.get(uri, args)
    .success(function(data) {
      var v = JSON.parse(data);
      g_current_word_data = v;  // FIXME: bad, but...
      $("#word_holder_id").text(v.word);
    })
    .error(function(data) { 

    });
}

$(function() {
  // Handler for .ready() called.
  gView.onCreate();
});