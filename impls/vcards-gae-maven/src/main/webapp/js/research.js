
// Class
function PlotView(dal) { 
  this.dal = dal;
  this.store = {};
  this.previousPoint = null;
}

PlotView.prototype.onGetData = function (point) {
  var self = this;
  this.dal.getDistributionAsync(function(data) { 
    self.plot(data); 
  }, point);
}

PlotView.prototype.plot = function (distribution) {
  // FIXME: Нужно усреднять данные на отрезках, а через zoom увеличивать.
  var self = this;
  var allPoints = [];
  var disabledPoints = [];
  var now_points = [];
  var numPoints = distribution.length;

  _.each(distribution, function(e, index) {
    var elem = new protocols.DistributionElem(e);  // FIXME: bad - можно и напрямую пользоваться
    self.store[index] = 'Position : ' + index;

    //if (elem.enabled)  // FAKE
      allPoints.push([index, elem.frequency]);

    if (!elem.enabled)
      disabledPoints.push([index, elem.frequency]);

    if (elem.enabled && elem.inBoundary)
      now_points.push([index, elem.frequency + 5000]);
  });

  // Функция рисования
  $.plot("#placeholder", [
    { data: allPoints, label: "importancies"}, 
    { data: disabledPoints, label: "known", points: { show:true }, lines: {show: false}} , 
    { data: now_points, label: "now active", points: { show:true }, lines: {show: false}} ], 

    {
    series: {
      lines: {show: true},
      points: {show: false}
    },
    grid: {
      hoverable: true,  // FIXME: don't work
      clickable: true
    }
  });
}

 PlotView.prototype._showTooltip = function (pos, item) {
  // Вывод подсказки по элементу
  if (item) {
    if (this.previousPoint != item.dataIndex) {

      this.previousPoint = item.dataIndex;

      $("#tooltip").remove();
      var x = item.datapoint[0].toFixed(2),
      y = item.datapoint[1].toFixed(2);

      this._drawTooltip(item.pageX, item.pageY, this.store[Math.floor(x)]);
    }
  } else {
    $("#tooltip").remove();
    this.previousPoint = null;            
  }
}

PlotView.prototype._drawTooltip = function (x, y, contents) {
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

// Zoom:
//   - До определенного момента кружочки не должны выключаться. 
// Например при zoom = 1, когда показываются все элементы.
PlotView.prototype.reset = function() {
  var self = this;
  this.previousPoint = null;
  $("#placeholder").bind("plothover", function (event, pos, item) {
    self._showTooltip(pos, item)
  });
}