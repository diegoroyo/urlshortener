function url(s) {
    var l = window.location;
    return l.protocol + "//" + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + l.pathname + s;
}

function eventQR(url) {
    $.ajax({
        type: "GET",
        url: "/api/qr",
        data: "url=" + url,
        success: function (msg) {
            $("#resultQR").html(
                "<img src='data:image/png;base64," + msg + "'/>"
            );
        },
        error: function (error) {
            $("#resultQR").html(
                "<div class='alert alert-danger lead'>" + JSON.parse(error.responseText).message + "</div>");
        }
    });
}

function eventShortUrl(event) {
    $("#result").html('');
    $("#resultQR").html('');
    event.preventDefault();
    $.ajax({
        type: "POST",
        url: "/api/link",
        data: $(this).serialize(),
        success: function (msg) {
            var json = JSON.parse(msg)
            if (json['id'].includes("{0}")) {
                // No link, no qr
                $("#result").html(
                    "<div class='alert alert-success lead'>"
                    + url(json['id'])
                    +  "</div>");
            } else {
                // Link and QR
                $("#result").html(
                    "<div class='alert alert-success lead'><a target='_blank' href='"
                    + url(json['id'])
                    + "'>"
                    + url(json['id'])
                    + "</a></div>");
                eventQR(url(json['id']));
            }
        },
        error: function (error) {

            $("#result").html(
                "<div class='alert alert-danger lead'>" + JSON.parse(error.responseText).message + "</div>");
        }
    });
};

pageNum = -1
pageSize = 5

function eventStatistics(event) {
    $('#statistics-head').add('hidden');
    $("#statistics-rows").html('');
    event.preventDefault();
    $.ajax({
        type: "GET",
        url: "/api/statistics",
        data: "short=" + $(this).serializeArray()[0].value + "&pageNumber=" + pageNum + "&pageSize=" + pageSize,
        success: function (msg) {
            console.log(msg[0]);
            $('#statistics-head').removeAttr('hidden');
            var table = '';
            msg[0].forEach(function (click) {
                table += "<tr>"
                    + "<th scope=\"row\">" + click.clickId + "</th>"
                    + "<td>" + click.created + "</td>"
                    + "<td>" + click.referer + "</td>"
                    + "<td>" + click.browser + "</td>"
                    + "<td>" + click.platform + "</td>"
                    + "<td>" + click.ip + "</td>"
                    + "</tr>"
            })
            $("#statistics-rows").html(table);
            $('#statistics-back').removeAttr('hidden');
            $("#statistics-button").html('Next page');
        },
        error: function (e) {
            $("#resultQR").html(
                "<div class='alert alert-danger lead'>STATISTICS ERROR</div>");
        }
    });
};

$(document).ready(
    function () {
        $("#shortener").submit(eventShortUrl);
        $("#statistics").submit(eventStatistics);
        $("#statistics-back").click(() => {
            pageNum = pageNum - 1
        });
        $("#statistics-button").click(() => {
            pageNum = pageNum + 1
        });
    });