
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#sidebar-container").toggleClass("active");
    $("#menu-toggle-arrow").toggleClass("active");
});

$("#quickstart-guides").click(function(e) {
    e.preventDefault();
    $("#quickstart-guides-items").toggleClass("active");
    $("#quickstart-guides-chevron").toggleClass("chevron-active");
});

$("#core-docs").click(function(e) {
    e.preventDefault();
    $("#core-docs-items").toggleClass("active");
    $("#core-docs-chevron").toggleClass("chevron-active");
});

$("#android-utils").click(function(e) {
    e.preventDefault();
    $("#android-utils-items").toggleClass("active");
    $("#android-utils-chevron").toggleClass("chevron-active");
});
