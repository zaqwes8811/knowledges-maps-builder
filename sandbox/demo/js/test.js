$(function() {

    var sin = [],
      cos = [];

    for (var i = 0; i < 14; i += 0.1) {
      sin.push([i, Math.random()]);
      cos.push([i, Math.random()]);
    }

    // Функция рисования
    var plot = $.plot("#placeholder", [
      { data: sin, label: "sin(x)"},
      { data: cos, label: "cos(x)"}
    ], {
      series: {
        lines: {
          show: true
        },
        points: {
          show: true
        }
      },
      grid: {
        hoverable: true,
        clickable: true
      }
    });

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

      if ($("#enablePosition:checked").length > 0) {
        var str = "(" + pos.x.toFixed(2) + ", " + pos.y.toFixed(2) + ")";
        $("#hoverdata").text(str);
      }

      if ($("#enableTooltip:checked").length > 0) {
        if (item) {
          if (previousPoint != item.dataIndex) {

            previousPoint = item.dataIndex;

            $("#tooltip").remove();
            var x = item.datapoint[0].toFixed(2),
            y = item.datapoint[1].toFixed(2);

            showTooltip(item.pageX, item.pageY,
                item.series.label + " of " + x + " = " + y);
          }
        } else {
          $("#tooltip").remove();
          previousPoint = null;            
        }
      }
    });

    $("#placeholder").bind("plotclick", function (event, pos, item) {
      if (item) {
        $("#clickdata").text(" - click point " + item.dataIndex + " in " + item.series.label);
        plot.highlight(item.series, item.datapoint);
      }
    });

    // Add the Flot version string to the footer

    $("#footer").prepend("Flot " + $.plot.version + " &ndash; ");
  });

function get_data() {
  var request_processor = '/ajax';
  var response_branch = {'hello':'hallo'};
  var jqxhr = $.get(request_processor, response_branch)
    .success(function(data) {
      process_response(data);
    })
    .error(function(data) { 
      if (data.status == 404) {
            /*err_action(
                'Запрос к несуществующей странице. '+
                'Ошибка 404. Запрос будет повторен.');*/
          }
          if (data.status == 500) {
            /*err_action(
                'Внутренняя ошибка сервера. Ошибка 500. '+
                'Запрос будет повторен.');*/
          }
          if (data.status == 0) {
            /*err_action(
                'Отсутствует связь с сервером. '+
                'Запрос будет повторен.');*/
          }
    });

}

function process_response(data) {
  var sin = [],
  cos = [];

  var tmp = $.parseJSON(data);

  for (var i = 0; i < 4; i += 0.1) {
    //sin.push([i, Math.random()]);
    //sin.push([i, Math.random()]);
    //cos.push([i, Math.random()]);
    cos.push([i, 1]);
  }
  
  // Функция рисования
  var plot = $.plot("#placeholder", [
    { data: tmp, label: "cos(x)"}//   ,
    //{ data: cos, label: "cos(x)"}
  ], {
    series: {
      lines: {
        show: true
      },
      points: {
        show: false //true
      }
    },
    grid: {
      hoverable: true,
      clickable: true
    }//,
    //yaxis: {
   //   min: -0.2,
 //     max: 1.2
 //   }
  });
  
}
