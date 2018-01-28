$(document).ready(function () {

    //Toggle menupoints for corresponding viewport
    $(".dropdown").click(function () {
        var currentWindowsSize = $(window).width();
        if (currentWindowsSize < 767) {
            $(".dropdown-content").slideUp();
            $(this).find(".dropdown-content").slideDown();
        }
    });

});