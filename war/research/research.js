// State
var gUserSummary = new UserSummary([]);
var gView = new View();
var gPlotView = new PlotView();
var gDataAccessLayer = new DataAccessLayer();
var gCurrentWordData = new CurrentWordData(gView);
var g_map = {};

function Point(page, gen, pos) {
  this.page = page;
  this.gen = gen;
  this.pos = pos;
}

function CurrentWordData(view) {
  this.view = view;
  this.data = {};
}

CurrentWordData.prototype.set = function (data) {
  this.data = data;
}

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
function View() {

  // init know checkbox 
  $('#know_it').change(function() {
    // this represents the checkbox that was checked
    // do something with it
    var $this = $(this);
    if ($this.is(':checked')) {
      // думается лучше выполнить синхронно, хотя если здесь, то все равно
      // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
      g_need_set_known = true;
    }
  });
}

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

View.prototype.drawWordValue = function (word) {
  $("#word_holder_id").text(word);
}

// Actions
View.prototype.onCreate = function() {
  // Get user data
  gDataAccessLayer.getUserSummary(function(data) {
      gUserSummary.reset(JSON.parse(data));
      gView.resetPagesOptions(gUserSummary.getPageNames());});
}

function PlotView() { }

PlotView.prototype.plot = function (data) {
  var getted_axises = $.parseJSON(data);
  
  // FIXME: Нужно усреднять данные на отрезках, а через zoom увеличивать.
  var zoomed_data = [];
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
  var _plot = $.plot("#placeholder", [{ data: zoomed_data, label: "distr(x)"}], {
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

// Zoom:
//   - До определенного момента кружочки не должны выключаться. 
// Например при zoom = 1, когда показываются все элементы.
PlotView.prototype.reset = function() {
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
}

// Ajax wrapper
function DataAccessLayer() { }

DataAccessLayer.prototype.onError = function (message) {
  alert(message);
}

DataAccessLayer.prototype.markIsDone = function (point) {
  var uri = '/pkg';
  var args = point;
  $.get(uri, args)
    .error(function(data) { this.onError(data); });
  // FIXME: better sync()
}

DataAccessLayer.prototype.getWordPkgAsync = function (callback) {
    // делаем запрос
  var uri = '/pkg';
  var args = {'name':'get_axis'};
  var _ = $.get(uri, args)
    .success(callback)
    .error(function(data) { this.onError(data); });
}

DataAccessLayer.prototype.getDistributionAsync = function (callback) {
  var request_processor = '/research/get_distribution';
  var response_branch = {'name':'get_axis'};
  var jqxhr = $.get(request_processor, response_branch)
    .success(callback)
    .error(function(data) { gDataAccessLayer.onError(data); });
}

DataAccessLayer.prototype.getUserSummary = function (callback) {
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/user_summary';
  var jqxhr = $.get(uri)
    .success(callback)
    .error(function(data) { this.onError(data); });
}


// DOM callbacks
function get_data() {
  gDataAccessLayer.getDistributionAsync(function(data) { gPlotView.plot(data); });
}

function get_word_pkg() { 
  // Нужны еще данные - страница и имя генератора

  // делаем запрос
  gDataAccessLayer.getWordPkgAsync(function(data) {
      var v = JSON.parse(data);
      gCurrentWordData.set(v);
      gView.drawWordValue(v.word);
    });
}

$(function() {
  // Handler for .ready() called.
  gView.onCreate();
  gPlotView.reset();
});