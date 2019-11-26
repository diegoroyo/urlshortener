function eventQR(url) {
    $.ajax({
        type: "GET",
        url: "/qr",
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
    event.preventDefault();
    $.ajax({
        type: "POST",
        url: "/link",
        data: $(this).serialize(),
        success: function (msg) {
            // TODO cambiar localhost:8080
            $("#result").html(
                "<div class='alert alert-success lead'><a target='_blank' href='"
                + 'http://localhost:8080/' + msg.id
                + "'>"
                + 'http://localhost:8080/' + msg.id
                + "</a></div>");
            eventQR('http://localhost:8080/' + msg.id);
        },
        error: function (error) {
            $("#result").html(
                "<div class='alert alert-danger lead'>" + JSON.parse(error.responseText).message + "</div>");
        }
    });
};

function eventStatistics(event) {
    event.preventDefault();
    $.ajax({
        type: "GET",
        url: "/statistics",
        data: "short=" + $(this).serializeArray()[0].value,
        success: function (msg) {
            $("#statistics-table").className = "table-responsive";
            var table = '';
            msg.forEach(function (click) {
                table += "<tr>"
                    + "<th scope=\"row\">" + click.clickId + "</th>"
                    + "<td>" + click.created + "</td>"
                    + "<td>" + click.referrer + "</td>"
                    + "<td>" + click.browser + "</td>"
                    + "<td>" + click.platform + "</td>"
                    + "<td>" + click.ip + "</td>"
                    + "<td>" + click.country + "</td>"
                    + "</tr>"
            })
            $("#statistics-rows").html(table);
        },
        error: function () {
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