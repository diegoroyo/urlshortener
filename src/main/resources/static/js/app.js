function eventQR(url) {
    $.ajax({
        type: "GET",
        url: "/qr",
        data: "url='" + url + "'",
        success: function (msg) {
            $("#resultQR").html(
                "<img src='data:image/png;base64," + msg + "'/>"
            );
        },
        error: function () {
            $("#resultQR").html(
                "<div class='alert alert-danger lead'>QR CODE ERROR</div>");
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
        error: function () {
            $("#result").html(
                "<div class='alert alert-danger lead'>ERROR</div>");
        }
    });
};

$(document).ready(
    function () {
        $("#shortener").submit(eventShortUrl)
    });