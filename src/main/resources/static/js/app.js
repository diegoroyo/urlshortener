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
            // TODO cambiar localhost:8080
            var json = JSON.parse(msg)
            if (json['id'].includes("{0}")) {
                // No link, no qr
                $("#result").html(
                    "<div class='alert alert-success lead'>"
                    + 'http://localhost:8080/' + json['id']
                    +  "</div>");
            } else {
                // Link and QR
                $("#result").html(
                    "<div class='alert alert-success lead'><a target='_blank' href='"
                    + 'http://localhost:8080/' + json['id']
                    + "'>"
                    + 'http://localhost:8080/' + json['id']
                    + "</a></div>");
                eventQR('http://localhost:8080/' + json['id']);
            }
        },
        error: function (error) {
            $("#result").html(
                "<div class='alert alert-danger lead'>" + JSON.parse(error.responseText).message + "</div>");
        }
    });
};

function eventStatistics(event) {
    $('#statistics-head').add('hidden');
    $("#statistics-rows").html('');
    event.preventDefault();
    $.ajax({
        type: "GET",
        url: "/api/statistics",
        data: "short=" + $(this).serializeArray()[0].value + "&pageNumber=0&pageSize=5",
        success: function (msg) {
            $('#statistics-head').removeAttr('hidden');
            var table = '';
            msg.forEach(function (click) {
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
    });