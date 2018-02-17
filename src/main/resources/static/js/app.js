$(document).ready(function () {

    //Disable overlay for modal
    $('.modal').modal({
        backdrop: false
    });

    //Hide modal for playerselection "INIT"
    $("#selectable-player-modal").modal('hide');

    //Toggle menupoints for corresponding viewport
    $(".dropdown").click(function () {
        var currentWindowsSize = $(window).width();
        if (currentWindowsSize < 767) {
            $(".dropdown-content").slideUp();
            $(this).find(".dropdown-content").slideDown();
        }
    });

    //On click switch to match-type-selection
    $(".season-selection-box a").click(function () {
        var selectedSeasonId = $(this).attr("id");
        $("#season").val(selectedSeasonId);
        $(".right").click();
    });

    //On click switch to player-selection
    $(".match-type-selection-box img").click(function () {
        var selectedMatchType = $(this).attr("id");
        $("#matchType").val(selectedMatchType);
        $(".right").click();
    });

    //Handle swipefunctions 4 small viewport
    $(".new-match-carousel").swiperight(function() {
        $(".right").click();
    });
    $(".new-match-carousel").swipeleft(function() {
        $(".left").click();
    });

    var currentSelectedPosition;
    var currentSelectedPositionId;

    //On click show modal with all players
    $(".player-selection-field").click(function () {
        alert("s")
        var positionToSelectId = $(this).attr("id");
        var positionImage = $(this).data("position");
        var positionId = $(this).data("id");
        currentSelectedPosition = positionToSelectId;
        currentSelectedPositionId = positionId;
        $(".modal-position-image").attr("src", positionImage);
        $("#selectable-player-modal").modal('show');
    });

    $(".player-module").click(function () {
        var playerImage = $(this).find("img").attr("src");
        var playerId = $(this).find("p").attr("id");
        var playerName = $(this).find("p").text();

        //do frontend stuff
        var selectedPostionElement = $("#" + currentSelectedPosition);
        selectedPostionElement.attr("src", playerImage);
        selectedPostionElement.next("p").text(playerName);
        $("#selectable-player-modal").modal('hide');

        console.log(currentSelectedPositionId);
        //refer player_id to hidden input
        $("#" + currentSelectedPositionId).val(playerId)
    });

    //DO form validation
    $('form .new-match-form').submit(function (e) {
        alert("hello");
        var seasonId = $("#season").val();
        var matchType = $("#matchType").val();
        var goalkeeper1 = $("#keeperTeam1").val();
        var goalkeeper2 = $("#keeperTeam2").val();
        var striker1 = $("#strikerTeam1").val();
        var striker2 = $("#strikerTeam2").val();
        var validSeason = isNumber(seasonId);
        var validKeeper1 = isNumber(goalkeeper1);
        var validKeeper2 = isNumber(goalkeeper2);
        var validStriker1 = isNumber(striker1);
        var validStriker2 = isNumber(striker2);
        var validMatchType = checkNullOrEmpty(matchType);
        console.log(validSeason);
        console.log(validMatchType);
        console.log(validStriker2);
        console.log(validStriker1);
        console.log(validKeeper2);
        console.log(validKeeper1);
        if(!validSeason && validKeeper1 && validKeeper2 && validStriker1 && validStriker2 && validMatchType){
            e.preventDefault();
            alert("Ooops something is missing")
        }
    });


    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }
    function checkNullOrEmpty(s) {
        return s !== null && s !== "";
    }
});